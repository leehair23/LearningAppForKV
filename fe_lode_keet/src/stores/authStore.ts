import { create } from "zustand";
import type { AuthState } from "@/common/interfaces";

export const mockGenerateAccessTokenAsync = (
  username: string,
  password: string
) => {
  return new Promise((resolve) => {
    setTimeout(function () {
      console.log("Mock accessToken generated here");
      const token = `${username}_accessToken_hehehe`;
      resolve(token);
    }, 100);
  });
};

export const mockSignUp = (
  username: string,
  password: string,
  email: string
) => {
  return new Promise((resolve) => {
    setTimeout(function () {
      console.log("Mock Sign up here");
      const userData = {
        id: "id123ZxC",
        username,
        email,
      };
      resolve(userData);
    }, 100);
  });
};

export const mockGenerateUserData = () => {
  return new Promise((resolve) => {
    setTimeout(function () {
      console.log("Mock user data here");
      const userData = {
        id: "id123ZxC",
        username: "Niga",
        email: "nigacoding@gmail.com",
      };
      resolve(userData);
    }, 100);
  });
};

export const authStore = create<AuthState>((set, get) => ({
  accessToken: null,
  refreshToken: null,
  loading: false,
  user: null,
  setAccessToken: (accessToken: unknown) => {
    set({ accessToken });
  },
  setLoading: (loading: boolean) => {
    set({ loading });
  },
  setUser: (userData: unknown) => {
    set({ user: userData });
  },
  setRefreshToken: (refreshToken: unknown) => {
    set({ refreshToken });
  },
  clearState: () => {
    set({ accessToken: null, user: null, loading: false, refreshToken: null });
  },
}));
