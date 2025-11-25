export interface AuthState {
  accessToken: string | unknown;
  loading: boolean;
  user: any;
  setAccessToken: (accessToken: string | unknown) => void;
  clearState: () => void;
  fetchUser: () => Promise<void>;
  signUp: (username: string, password: string, email: string) => Promise<void>;
  signIn: (username: string, password: string) => Promise<void>;
  signOut: () => Promise<void>;
  renewToken: () => Promise<void>;
}
