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
        username,
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
    const accessToken = authStorage.getAccessToken();
    if (accessToken) {
      await fetch(`${API_URL}/auth/logout`, {
        method: "POST",
        headers: { Authorization: `Bearer ${accessToken}` },
      }).catch(() => undefined);
    }
    authStorage.clear();
  },

  async checkAuth() {
    if (!authStorage.getAccessToken()) {
      throw new Error("Authentication required");
    }
  },

  async checkError(error) {
    if (error.status === 401) {
      authStorage.clear();
      throw new Error("Session expired");
    }
  },

  async getIdentity() {
    const claims = authStorage.getClaims();
    return {
      id: claims?.uid ?? claims?.sub ?? "user",
      fullName: claims?.sub ?? "Trip Planner User",
    };
  },

  async getPermissions() {
    return authStorage.getClaims()?.role ?? "USER";
  },
};
