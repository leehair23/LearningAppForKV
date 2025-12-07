import { create } from "zustand";
import type { AuthState } from "@/common/interfaces";
import { persist } from "zustand/middleware";
import { Constants } from "@/common/constants";
import type { T_UserData } from "@/common/types";

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      email: "",
      password: "",
      username: "",
      accessToken: null,
      refreshToken: null,
      loading: false,
      user: null,
      isAuthenticated: false,

      setAuthenticated: (isAuthenticated: boolean) => set({ isAuthenticated }),
      setAccessToken: (accessToken: string | null) => {
        if (accessToken) {
          localStorage.setItem(
            Constants.LOCAL_STORAGE_KEYS.ACCESS_TOKEN,
            accessToken
          );
          set({ isAuthenticated: true });
        } else {
          localStorage.removeItem(Constants.LOCAL_STORAGE_KEYS.ACCESS_TOKEN);
          set({ isAuthenticated: false });
        }
        set({ accessToken });
      },
      setLoading: (loading: boolean) => {
        set({ loading });
      },
      setUser: (userData: T_UserData | null) => {
        set({ user: userData });
      },
      setRefreshToken: (refreshToken: string | null) => {
        if (refreshToken) {
          localStorage.setItem(
            Constants.LOCAL_STORAGE_KEYS.REFRESH_TOKEN,
            refreshToken
          );
        } else {
          localStorage.removeItem(Constants.LOCAL_STORAGE_KEYS.REFRESH_TOKEN);
        }
        set({ refreshToken });
      },
      clearState: () => {
        set({
          email: "",
          password: "",
          username: "",
          loading: false,
          accessToken: null,
          refreshToken: null,
          user: null,
          isAuthenticated: false,
        });
        localStorage.clear();
      },
      setUsername: (username) => set({ username }),
      setEmail: (email) => set({ email }),
      setPassword: (password) => set({ password }),
      resetForm: () => set({ email: "", password: "", username: "" }),
    }),
    {
      name: Constants.LOCAL_STORAGE_KEYS.AUTH_STORE,
      partialize: (state) => ({
        user: state.user,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);
