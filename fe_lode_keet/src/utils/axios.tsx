import axios from "axios";
import { useAuthStore } from "@/stores/useAuthStore";
import { Constants } from "@/common/constants";

// Create axios instance
const api = axios.create({
  baseURL: Constants.BASE_URL,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor to add token
api.interceptors.request.use(
  (config) => {
    // Skip adding token for auth endpoints
    const isAuthEndpoint = config.url?.includes("/auth/");
    if (!isAuthEndpoint && typeof window !== "undefined") {
      // Get token from localStorage OR Zustand store
      const token =
        localStorage.getItem(Constants.LOCAL_STORAGE_KEYS.AUTH_STORE) ||
        useAuthStore.getState().accessToken;

      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle token refresh
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const ogRequest = error.config;
    const refreshToken = localStorage.getItem(
      Constants.LOCAL_STORAGE_KEYS.REFRESH_TOKEN
    );

    if (
      error.response &&
      error.response.status === 401 &&
      error.config &&
      !error.config.__isRetryRequest &&
      refreshToken
    ) {
      ogRequest._retry = true;
    }
  }
);

export default api;
