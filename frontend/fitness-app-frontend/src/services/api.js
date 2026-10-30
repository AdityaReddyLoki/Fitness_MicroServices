import axios from "axios";
import { store } from "../store/store";

const API = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080",
});

API.interceptors.request.use(
  (config) => {
    const token = store.getState().auth.token;

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error),
);

export const addActivity = (activity) => API.post("/api/activities", activity);

export const getActivities = () => API.get("/api/activities");

export const getActivityDetail = (id) => API.get(`/api/activities/${id}`);

export const getActivityRecommendation = (id) =>
  API.get(`/api/recommendations/activity/${id}`);
