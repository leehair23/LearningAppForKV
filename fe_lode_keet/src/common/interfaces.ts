export interface AuthState {
  accessToken: string | unknown;
  refreshToken: string | unknown;
  loading: boolean;
  user: unknown;
  username: string;
  email: string;
  password: string;

  setUsername: (username: string) => void;
  setEmail: (email: string) => void;
  setPassword: (password: string) => void;
  resetForm: () => void;
  setAccessToken: (token: string | unknown) => void;
  setRefreshToken: (token: string | unknown) => void;
  setLoading: (loading: boolean) => void;
  setUser: (user: unknown) => void;
  clearState: () => void;
}
