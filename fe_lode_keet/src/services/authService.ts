import api from "@/libs/axios";

export const authService = {
  signUp: async (username: string, password: string, email: string) => {
    const response = await api.post(
      "/auth/signup",
      { username, password, email },
      { withCredentials: true }
    );
    return response.data;
  },

  signIn: async (username: string, password: string) => {
    const response = await api.post(
      "/auth/signin",
      { username, password },
      { withCredentials: true }
    );
    return response.data; // access token
  },

  signOut: async () => {
    return await api.post("/auth/signout", { withCredentials: true });
  },

  fetchUser: async () => {
    const response = await api.get("/user", { withCredentials: true });
    return response.data;
  },

  renewToken: async (username: string, password: string, email: string) => {
    // Renew Token request
  },
};
