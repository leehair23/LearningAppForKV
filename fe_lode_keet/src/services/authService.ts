import {
  mockGenerateAccessTokenAsync,
  mockGenerateUserData,
  authStore,
} from "@/stores/authStore";
import { Connstants } from "@/common/constants";
import { toast } from "sonner";

export const authService = {
  signUp: async (username: string, password: string, email: string) => {
    const { setUser, setAccessToken, setLoading, setRefreshToken } =
      authStore.getState();
    try {
      setLoading(true);

      const response = await fetch("/auth/signup", {
        method: Connstants.REQUEST_METHODS.POST,
        headers: Connstants.HEADERS,
        body: JSON.stringify({
          username,
          password,
          email,
        }),
      });

      const data = await response.json();
      // const { user, accessToken, refreshToken } = response.data;

      setUser(data?.user || null);
      setAccessToken(data?.accessToken || null);
      setRefreshToken(data?.refreshToken || null);
      toast.success("Sign up successfully✅");
      return response; // remember to change this to response.data later
    } catch (error) {
      console.error(error);
      toast.error("❌Error occurred during signing up!");
    } finally {
      setLoading(false);
    }
  },

  signIn: async (username: string, password: string) => {
    const response = await api.post(
      "/auth/signin",
      { username, password },
      { withCredentials: true }
    );
    const dataToSave = response.data;
    const localStorageToken = localStorage.getItem(
      Connstants.LOCAL_STORAGE_KEYS.ACC_TOKEN
    );
    if (!localStorageToken) {
      localStorage.setItem(key, dataToSave);
    }
    return dataToSave; // remember to change this to response.data later
  },

  signOut: async () => {
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
  },

  fetchUser: async () => {
    const { setUser, setAccessToken, setLoading } = authStore.getState();
    try {
      setLoading(true);
      const response = await api.get("/user", { withCredentials: true });
      const user = await mockGenerateUserData();

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
  },

  renewToken: async (username: string, password: string, email: string) => {
    try {
      const { user, setUser, setAccessToken, refreshToken } =
        authStore.getState();
      const renewedAccessToken = await mockGenerateAccessTokenAsync(
        "renew_accessToken",
        ""
      );

      setAccessToken(renewedAccessToken);

      if (user) {
        await authService.fetchUser();
      }
    } catch (error) {
      console.error(error);
      toast.error("❌Session is expired, please Sign in again!");
    } finally {
    }
  },

  verifyToken: async (tokenStr: string) => {
    const bearerString = "Bearer ";
    if (!tokenStr) return "";

    const token = tokenStr.replace(bearerString, "");
    return token;
  },
};
