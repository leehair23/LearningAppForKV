import { useAuthStore } from "@/stores/useAuthStore";
import { Constants } from "@/common/constants";
import { toast } from "sonner";
import { decode } from "jsonwebtoken";
import type { T_AuthPayload } from "@/common/types";
import api from "@/utils/axios";

export class AuthService {
  private static instance: AuthService;

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

      const response = await api.post(`/auth/signup`, {
        username,
        password,
        email,
      });

      console.log(response);
      const { access_token: accessToken, refresh_token: refreshToken } =
        await response.data;

      const parsedDataFromAccToken: T_AuthPayload | null =
        this.decodeToken(accessToken);

      if (parsedDataFromAccToken) {
        const { role, sub, email, userId } = parsedDataFromAccToken;
        setUser({ role, email, sub, userId, username });
        setAccessToken(accessToken);
        setRefreshToken(refreshToken);
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
      // const response = await api.post(`/auth/signin`, {
      //   username,
      //   password,
      // });

      // const { access_token, refresh_token } = await response.data;

      // Fake data for testing
      const access_token =
        "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtaWtlIiwicm9sZSI6IlVTRVIiLCJpc3MiOiJzZWxmIiwiZXhwIjoxNzY1MTI1NDY2LCJpYXQiOjE3NjUwMzkwNjYsInVzZXJJZCI6Ijg1MGMyMjk2LWViZTktNDE5Mi04NDQxLTdiM2RiNWJjOTU4ZCIsImVtYWlsIjoiIG1pbmtlbmRhbmcyQGV4bS5jb20gIn0.vNBPZOzkJzP-YHho1fZUhJ0rxHw2P15SpH8r4jaxsQs";

      const refresh_token =
        "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJzZWxmIiwic3ViIjoibWlrZSIsImV4cCI6MTc2NTY0Mzg2NiwiaWF0IjoxNzY1MDM5MDY2fQ.YnN5jaKs7UFcCN6NAdplSIg0aeZQV3E1u_Sk2u83EJE";

      const parsedDataFromAccToken: T_AuthPayload | null =
        this.decodeToken(access_token);

      if (parsedDataFromAccToken) {
        const { role, sub, email, userId } = parsedDataFromAccToken;
        setUser({ role, email, sub, userId });
        setAccessToken(access_token);
        setRefreshToken(refresh_token);
      } else {
        throw Error("Null token, please check");
      }
      return true;
    } catch (error) {
      console.error(error);
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
  public decodeToken(token: string): T_AuthPayload | null {
    try {
      const data = decode(token) as T_AuthPayload;
      return data;
    } catch (error) {
      console.error("JWT verification failed:", error);
      return null;
    }
  }
}

export const authService = AuthService.getInstance();
