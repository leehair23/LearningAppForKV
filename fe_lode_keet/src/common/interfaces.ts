export interface AuthState {
  accessToken: string | unknown;
  refreshToken: string | unknown;
  loading: boolean;
  user: any;
  setAccessToken: (token: string | unknown) => void;
  setRefreshToken: (token: string | unknown) => void;
  setLoading: (loading: boolean) => void;
  setUser: (user: unknown) => void;
  clearState: () => void;
}
