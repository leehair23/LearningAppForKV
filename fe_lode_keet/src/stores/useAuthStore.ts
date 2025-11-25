import { create } from "zustand";
import { toast } from "sonner";
import type { AuthState } from "@/common/interfaces";

function mockGenerateAccessTokenAsync(username: string, password: string) {
  return new Promise((resolve) => {
    setTimeout(function () {
      console.log("Mock accessToken generated here");
      const token = `${username}_accessToken_hehehe`;
      resolve(token);
    }, 100);
  });
}

function mockSignUp(username: string, password: string, email: string) {
  return new Promise((resolve) => {
    setTimeout(function () {
      console.log("Mock Sign up here");
      const userData = {
        id: "id123ZxC",
        username,
        email,
      };
      resolve(userData);
    }, 100);
  });
}

function mockGenerateUserData() {
  return new Promise((resolve) => {
    setTimeout(function () {
      console.log("Mock user data here");
      const userData = {
        id: "id123ZxC",
        username: "Niga",
        email: "nigacoding@gmail.com",
      };
      resolve(userData);
    }, 100);
  });
}

export const useAuthStore = create<AuthState>((set, get) => ({
  accessToken: null,
  loading: false,
  user: null,
  setAccessToken: (accessToken) => {
    set({ accessToken });
  },
  clearState: () => {
    set({ accessToken: null, user: null, loading: false });
  },
  fetchUser: async () => {
    try {
      set({ loading: true });
      const user = await mockGenerateUserData();

      set({ user });
    } catch (error) {
      console.error(error);
      set({ user: null, accessToken: null });
      toast.error("❌Error occurred during fetching user data!");
    } finally {
      set({ loading: false });
    }
  },
  signUp: async (username, password, email) => {
    try {
      set({ loading: true });

      await mockSignUp(username, password, email);

      toast.success("Sign in successfully✅");
    } catch (error) {
      console.error(error);
      toast.error("Đăng ký không thành công");
    } finally {
      set({ loading: false });
    }
  },
  signIn: async (username, password) => {
    try {
      set({ loading: true });

      // Signin here
      const accessToken = await mockGenerateAccessTokenAsync(
        username,
        password
      );
      get().setAccessToken(accessToken);

      await get().fetchUser();

      toast.success(`Welcome back, ${username} 🙌`);
    } catch (error) {
      console.error(error);
      toast.error("❌Error occurred during Signing in!");
    } finally {
      set({ loading: false });
    }
  },
  signOut: async () => {
    try {
      get().clearState();
      toast.success("Good bye🤞");
    } catch (error) {
      console.error(error);
      toast.error("❌Error occurred during Logging out!");
    } finally {
      set({ loading: false });
    }
  },
  renewToken: async () => {
    try {
      set({ loading: true });
      const { user, fetchUser, setAccessToken } = get();
      const renewedAccessToken = await mockGenerateAccessTokenAsync(
        "renew_accessToken",
        ""
      );

      setAccessToken(renewedAccessToken);

      if (user) {
        await fetchUser();
      }
    } catch (error) {
      console.error(error);
      toast.error("❌Session is expired, please Sign in again!");
    } finally {
      set({ loading: false });
    }
  },
}));
