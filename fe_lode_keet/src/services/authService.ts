import {
  mockGenerateAccessTokenAsync,
  mockGenerateUserData,
  authStore,
} from "@/stores/authStore";
import { Constants } from "@/common/constants";
import { toast } from "sonner";
import jwt from "jsonwebtoken";
import { T_AuthPayload } from "@/common/types";

export class AuthService {
  private static instance: AuthService;

  private JWT_SECRET = process.env.JWT_SECRET!;

  // Private constructor for singleton pattern
  private constructor() {}

  // Singleton getter
  public static getInstance(): AuthService {
    if (!AuthService.instance) {
      AuthService.instance = new AuthService();
    }
    return AuthService.instance;
  }

  // Sign up method
  public async signUp(username: string, password: string, email: string) {
    const { setUser, setAccessToken, setLoading, setRefreshToken } =
      authStore.getState();
    try {
      setLoading(true);

      const response = await fetch(`${Constants.BASE_URL}/auth/signup`, {
        method: Constants.REQUEST_METHODS.POST,
        headers: Constants.HEADERS,
        body: JSON.stringify({
          username,
          password,
          email,
        }),
      });

      const { access_token: accessToken, refresh_token: refreshToken } =
        await response.json();

      const parsedDataFromToken: T_AuthPayload | null =
        this.verifyToken(accessToken);

      if (parsedDataFromToken) {
        const { role, sub, email } = parsedDataFromToken;
        setUser({ role, email, sub });
        setAccessToken(accessToken);
        setRefreshToken(refreshToken);
        localStorage.setItem(
          Constants.LOCAL_STORAGE_KEYS.ACC_TOKEN,
          accessToken
        );
        toast.success("Sign up successfully✅");
      } else {
        throw Error("Access Token probably is null");
      }
      return response; // remember to change this to response.data later
    } catch (error) {
      console.error(error);
      toast.error("❌Error occurred during signing up!");
    } finally {
      setLoading(false);
    }
  }

  // Sign in method
  public async signIn(username: string, password: string) {
    const response = await api.post(
      "/auth/signin",
      { username, password },
      { withCredentials: true }
    );
    const dataToSave = response.data;
    const localStorageToken = localStorage.getItem(
      Constants.LOCAL_STORAGE_KEYS.ACC_TOKEN
    );
    if (!localStorageToken) {
      const key = Constants.LOCAL_STORAGE_KEYS.ACC_TOKEN;
      localStorage.setItem(key, JSON.stringify(dataToSave));
    }
    return dataToSave; // remember to change this to response.data later
  }

  // Sign out method
  public async signOut() {
    const { clearState } = authStore.getState();

    try {
      clearState();
      localStorage.clear();
      return await api.post("/auth/signout", { withCredentials: true });
    } catch (error) {
      console.error(error);
      toast.error("❌Error occurred during Logging out!");
    } finally {
      toast.success("Good bye🤞");
    }
  }

  // Fetch user method
  public async fetchUser() {
    const { setUser, setAccessToken, setLoading } = authStore.getState();
    try {
      setLoading(true);
      const response = await api.get("/user", { withCredentials: true });
      const user = await this.mockGenerateUserData();

      setUser(user);
      return response.data;
    } catch (error) {
      setUser(null);
      setAccessToken(null);
      console.error(error);
      toast.error("❌Error occurred during fetching user data!");
    } finally {
      setLoading(false);
    }
  }

  // Renew token method
  public async renewToken(username: string, password: string, email: string) {
    try {
      const { user, setUser, setAccessToken, refreshToken } =
        authStore.getState();
      const renewedAccessToken = await this.mockGenerateAccessTokenAsync(
        "renew_accessToken",
        ""
      );

      setAccessToken(renewedAccessToken);

      if (user) {
        await this.fetchUser();
      }
    } catch (error) {
      console.error(error);
      toast.error("❌Session is expired, please Sign in again!");
    } finally {
      // Empty finally block as in original
    }
  }

  // Extract token method
  public extractToken(tokenStr: string): string {
    const bearerString = "Bearer ";
    if (!tokenStr) return "";

    const token = tokenStr.replace(bearerString, "");
    return token;
  }

  // Verify token method
  public verifyToken(token: string): T_AuthPayload | null {
    try {
      return jwt.verify(token, this.JWT_SECRET) as T_AuthPayload;
    } catch (error) {
      console.error("JWT verification failed:", error);
      return null;
    }
  }

  // Mock methods from original code (kept as private since they were used internally)
  private async mockGenerateAccessTokenAsync(
    type: string,
    message: string
  ): Promise<string> {
    // Your mock implementation
    return "mock_token";
  }

  private async mockGenerateUserData(): Promise<any> {
    // Your mock implementation
    return { username: "mock_user" };
  }
}

export const authService = AuthService.getInstance();
