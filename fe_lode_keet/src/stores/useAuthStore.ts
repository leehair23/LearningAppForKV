import { create } from "zustand";
import type { AuthState } from "@/common/interfaces";
import { persist } from "zustand/middleware";

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
        set({
          email: "",
          password: "",
          username: "",
          loading: false,
          accessToken: null,
          refreshToken: null,
          user: null,
        });
      },
      setUsername: (username) => set({ username }),
      setEmail: (email) => set({ email }),
      setPassword: (password) => set({ password }),
      resetForm: () => set({ email: "", password: "", username: "" }),
    }),
    {
      name: "auth-storage",
      partialize: (state) => ({ email: state.email }), // Only persist email
    }
  )
);
