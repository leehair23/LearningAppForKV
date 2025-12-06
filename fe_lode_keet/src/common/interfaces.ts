import type { T_UserData } from "./types";

export interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  loading: boolean;
  user: T_UserData | null;
  username: string;
  email: string;
  password: string;
  isAuthenticated: boolean;

  setAuthenticated: (isAuthenticated: boolean) => void;
  setUsername: (username: string) => void;
  setEmail: (email: string) => void;
  setPassword: (password: string) => void;
  resetForm: () => void;
  setAccessToken: (token: string | null) => void;
  setRefreshToken: (token: string | null) => void;
  setLoading: (loading: boolean) => void;
  setUser: (user: T_UserData | null) => void;
  clearState: () => void;
}
