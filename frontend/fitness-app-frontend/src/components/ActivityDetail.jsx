import { useEffect, useState } from "react";
import { useParams } from "react-router";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  CircularProgress,
  Divider,
  Typography,
} from "@mui/material";
import { getActivityDetail, getActivityRecommendation } from "../services/api";

const ActivityDetail = () => {
  const { id } = useParams();

  const [activity, setActivity] = useState(null);
  const [recommendation, setRecommendation] = useState(null);
  const [recommendationLoading, setRecommendationLoading] = useState(true);
  const [recommendationError, setRecommendationError] = useState("");
  const [recommendationRetry, setRecommendationRetry] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchActivityDetail = async () => {
      try {
        setLoading(true);
        setError("");

        const response = await getActivityDetail(id);
        setActivity(response.data);
      } catch (err) {
        console.error("Failed to fetch activity details:", err);
        setError("Unable to load activity details. Please try again.");
      } finally {
        setLoading(false);
      }
    };

    fetchActivityDetail();
  }, [id]);

  useEffect(() => {
    let active = true;

    const fetchRecommendation = async () => {
      setRecommendationLoading(true);
      setRecommendationError("");

      try {
        const response = await getActivityRecommendation(id);
        if (active) {
          setRecommendation(response.data);
        }
      } catch (err) {
        console.error("Failed to fetch activity recommendation:", err);
        if (active) {
          setRecommendationError(
            "Unable to load the recommendation. It may still be processing; retry shortly.",
          );
        }
      } finally {
        if (active) {
          setRecommendationLoading(false);
        }
      }
    };

    fetchRecommendation();
    return () => {
      active = false;
    };
  }, [id, recommendationRetry]);

  if (loading) {
    return (
      <Box sx={{ display: "flex", justifyContent: "center", p: 3 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity="error" sx={{ m: 2 }}>
        {error}
      </Alert>
    );
  }

  if (!activity) {
    return <Typography>Activity not found.</Typography>;
  }

  return (
    <Box sx={{ maxWidth: 800, mx: "auto", p: 2 }}>
      <Card sx={{ mb: 2 }}>
        <CardContent>
          <Typography variant="h5" gutterBottom>
            Activity Details
          </Typography>

          <Typography>Type: {activity.type}</Typography>
          <Typography>Duration: {activity.duration} minutes</Typography>
          <Typography>Calories Burned: {activity.caloriesBurned}</Typography>

          {activity.createdAt && (
            <Typography>
              Date: {new Date(activity.createdAt).toLocaleString()}
            </Typography>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardContent>
          <Typography variant="h5" gutterBottom>
            AI Recommendation
          </Typography>

          {recommendationLoading && <CircularProgress size={24} />}

          {recommendationError && (
            <Alert
              severity="info"
              action={
                <Button
                  color="inherit"
                  size="small"
                  onClick={() => setRecommendationRetry((retry) => retry + 1)}
                >
                  Retry
                </Button>
              }
            >
              {recommendationError}
            </Alert>
          )}

          {recommendation && (
            <>
              <Typography variant="h6">Analysis</Typography>
              <Typography paragraph sx={{ whiteSpace: "pre-line" }}>
                {recommendation.recommendation}
              </Typography>

              {["improvements", "suggestions", "safety"].map((section) => {
                const items = recommendation[section];

                if (!Array.isArray(items) || items.length === 0) {
                  return null;
                }

                const title =
                  section.charAt(0).toUpperCase() + section.slice(1);

                return (
                  <Box key={section}>
                    <Divider sx={{ my: 2 }} />

                    <Typography variant="h6">{title}</Typography>

                    {items.map((item, index) => (
                      <Typography key={index} paragraph>
                        • {item}
                      </Typography>
                    ))}
                  </Box>
                );
              })}
            </>
          )}
        </CardContent>
      </Card>
    </Box>
  );
};

export default ActivityDetail;
