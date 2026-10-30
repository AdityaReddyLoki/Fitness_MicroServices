import { useContext, useEffect } from "react";
import { BrowserRouter as Router, Navigate, Route, Routes } from "react-router";
import { Box, Button, Typography } from "@mui/material";
import { AuthContext } from "react-oauth2-code-pkce";
import { useDispatch } from "react-redux";

import { logout, setCredentials } from "./store/authSlice";
import ActivityForm from "./components/ActivityForm";
import ActivityList from "./components/ActivityList";
import ActivityDetail from "./components/ActivityDetail";

const ActivitiesPage = () => {
  return (
    <Box sx={{ p: 2 }}>
      <Typography variant="h4" gutterBottom>
        My Fitness Activities
      </Typography>

      <ActivityForm onActivityAdded={() => window.location.reload()} />

      <ActivityList />
    </Box>
  );
};

function App() {
  const { token, tokenData, logIn, logOut, error } = useContext(AuthContext);

  const dispatch = useDispatch();

  useEffect(() => {
    if (!token) {
      dispatch(logout());
      return;
    }

    if (tokenData?.sub) {
      dispatch(setCredentials({ token, user: tokenData }));
    }
  }, [token, tokenData, dispatch]);

  return (
    <Router>
      {error && (
        <Typography color="error" role="alert" sx={{ p: 2 }}>
          {String(error)}
        </Typography>
      )}

      {!token ? (
        <Box
          sx={{
            minHeight: "100vh",
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            justifyContent: "center",
            textAlign: "center",
            p: 2,
          }}
        >
          <Typography variant="h4" gutterBottom>
            Welcome to the Fitness Tracker App
          </Typography>

          <Typography variant="subtitle1" sx={{ mb: 3 }}>
            Please log in to access your activities.
          </Typography>

          <Button variant="contained" size="large" onClick={() => logIn()}>
            Login with Keycloak
          </Button>
        </Box>
      ) : (
        <Box sx={{ p: 2 }}>
          <Box
            sx={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              mb: 3,
            }}
          >
            <Typography variant="h5">AI Fitness Tracker</Typography>

            <Button
              variant="contained"
              color="secondary"
              onClick={() => {
                dispatch(logout());
                logOut();
              }}
            >
              Logout
            </Button>
          </Box>

          <Routes>
            <Route path="/activities" element={<ActivitiesPage />} />

            <Route path="/activities/:id" element={<ActivityDetail />} />

            <Route path="/" element={<Navigate to="/activities" replace />} />

            <Route path="*" element={<Navigate to="/activities" replace />} />
          </Routes>
        </Box>
      )}
    </Router>
  );
}

export default App;
