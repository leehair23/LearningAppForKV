import { useAuthStore } from "@/stores/useAuthStore";
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
      useAuthStore.getState();
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

      const parsedDataFromAccToken: T_AuthPayload | null =
        this.verifyToken(accessToken);

      const parsedDataFromRefToken: T_AuthPayload | null =
        this.verifyToken(refreshToken);

      if (parsedDataFromAccToken && parsedDataFromRefToken) {
        const { role, sub, email, userId } = parsedDataFromAccToken;
        setUser({ role, email, sub, userId, username });
        setAccessToken(accessToken);
        setRefreshToken(refreshToken);
        localStorage.clear();
        localStorage.setItem(
          Constants.LOCAL_STORAGE_KEYS.ACC_TOKEN,
          accessToken
        );
        localStorage.setItem(
          Constants.LOCAL_STORAGE_KEYS.REF_tOKEN,
          refreshToken
        );
        toast.success("Sign up successfully✅");
      } else {
        throw Error("Null token, please check");
      }
      return;
    } catch (error) {
      console.error(error);
      toast.error("❌Error occurred during signing up!");
    } finally {
      setLoading(false);
    }
  }

  // Sign in method
  public async signIn(username: string, password: string) {
    const { setUser, setAccessToken, setLoading, setRefreshToken } =
      useAuthStore.getState();
    try {
      setLoading(true);

      const response = await fetch(`${Constants.BASE_URL}/auth/signin`, {
        method: Constants.REQUEST_METHODS.POST,
        headers: Constants.HEADERS,
        body: JSON.stringify({
          username,
          password,
        }),
      });

      const { access_token: accessToken, refresh_token: refreshToken } =
        await response.json();

      const parsedDataFromAccToken: T_AuthPayload | null =
        this.verifyToken(accessToken);

      const parsedDataFromRefToken: T_AuthPayload | null =
        this.verifyToken(refreshToken);

      if (parsedDataFromAccToken && parsedDataFromRefToken) {
        const { role, sub, email, userId } = parsedDataFromAccToken;
        setUser({ role, email, sub, userId, username });
        setAccessToken(accessToken);
        setRefreshToken(refreshToken);

        localStorage.clear();
        localStorage.setItem(
          Constants.LOCAL_STORAGE_KEYS.ACC_TOKEN,
          accessToken
        );
        localStorage.setItem(
          Constants.LOCAL_STORAGE_KEYS.REF_tOKEN,
          refreshToken
        );

        toast.success("Sign in successfully✅");
      } else {
        throw Error("Null token, please check");
      }
      return;
    } catch (error) {
      console.error(error);
      toast.error("❌Error occurred during signing up!");
    } finally {
      setLoading(false);
    }
  }

  // Sign out method
  public async signOut() {
    const { clearState } = useAuthStore.getState();

    try {
      clearState();
      localStorage.clear();
      return await fetch(`${Constants.BASE_URL}/auth/signin`, {
        method: Constants.REQUEST_METHODS.GET,
        headers: Constants.HEADERS,
      });
    } catch (error) {
      console.error(error);
      toast.error("❌Error occurred during Logging out!");
    } finally {
      toast.success("Good bye🤞");
    }
  }

  // Fetch user method
  public async fetchUser() {
    // const { setUser, setAccessToken, setLoading } = useAuthStore.getState();
    // try {
    //   setLoading(true);
    //   const response = await api.get("/user", { withCredentials: true });
    //   const user = await this.mockGenerateUserData();
    //   setUser(user);
    //   return response.data;
    // } catch (error) {
    //   setUser(null);
    //   setAccessToken(null);
    //   console.error(error);
    //   toast.error("❌Error occurred during fetching user data!");
    // } finally {
    //   setLoading(false);
    // }
  }

  // Renew token method
  public async renewToken(username: string, password: string, email: string) {
    // try {
    //   const { user, setUser, setAccessToken, refreshToken } =
    //     useAuthStore.getState();
    //   const renewedAccessToken = await this.mockGenerateAccessTokenAsync(
    //     "renew_accessToken",
    //     ""
    //   );
    //   setAccessToken(renewedAccessToken);
    //   if (user) {
    //     await this.fetchUser();
    //   }
    // } catch (error) {
    //   console.error(error);
    //   toast.error("❌Session is expired, please Sign in again!");
    // } finally {
    //   // Empty finally block as in original
    // }
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
}

export const authService = AuthService.getInstance();
