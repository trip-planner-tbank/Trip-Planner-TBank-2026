import type { AuthProvider } from "react-admin";

import { authStorage } from "../../features/auth/authStorage";
import { API_URL } from "../../shared/config/env";

type TokenResponse = {
  accessToken: string;
  refreshToken?: string;
};

export const authProvider: AuthProvider = {
  async login({ username, password }) {
    const response = await fetch(`${API_URL}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: username,
        password,
      }),
    });

    if (!response.ok) {
      throw new Error("Invalid email or password");
    }

    const data = (await response.json()) as TokenResponse;
    authStorage.setTokens(data.accessToken, data.refreshToken);
  },

  async logout() {
    authStorage.clear();
  },

  async checkAuth() {
    if (!authStorage.getAccessToken()) {
      throw new Error("Authentication required");
    }
  },

  async checkError(error) {
    if (error.status === 401 || error.status === 403) {
      authStorage.clear();
      throw new Error("Session expired");
    }
  },

  async getIdentity() {
    return {
      id: "admin",
      fullName: "Trip Planner Admin",
    };
  },

  async getPermissions() {
    return "admin";
  },
};
