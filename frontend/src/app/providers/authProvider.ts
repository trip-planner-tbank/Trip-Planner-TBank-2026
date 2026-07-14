import type { AuthProvider } from "react-admin";

import { authStorage } from "../../features/auth/authStorage";
import { API_URL } from "../../shared/config/env";
import { httpClient } from "../../shared/api/httpClient";

interface LoginCredentials {
  username: string;
  password: string;
}

interface SignupCredentials {
  username: string;
  email: string;
  password: string;
}

export const authProvider: AuthProvider & { signup: (params: SignupCredentials) => Promise<void> } = {
  login: async ({ username, password }: LoginCredentials) => {
    const { json } = await httpClient(`${API_URL}/auth/login`, {
      method: "POST",
      body: JSON.stringify({ username, password }),
    });

    const tokenResponse = json as { accessToken: string; refreshToken?: string };
    if (!tokenResponse.accessToken) {
      throw new Error("Invalid response from server");
    }

    authStorage.setTokens(tokenResponse.accessToken, tokenResponse.refreshToken);
  },

  logout: async () => {
    const refreshToken = authStorage.getRefreshToken();

    if (refreshToken) {
      try {
        await httpClient(`${API_URL}/auth/logout`, {
          method: "POST",
          body: JSON.stringify({ refreshToken }),
        });
      } catch {
        // Ignore logout request errors and still clear local tokens.
      }
    }

    authStorage.clear();
  },

  checkAuth: async () => {
    const accessToken = authStorage.getAccessToken();
    if (!accessToken) {
      throw new Error("Unauthorized");
    }
    const claims = authStorage.getClaims();
    if (claims?.exp && claims.exp * 1000 < Date.now()) {
      authStorage.clear();
      throw new Error("Unauthorized");
    }
  },

  checkError: async (error) => {
    const status = error?.status;
    if (status === 401 || status === 403) {
      authStorage.clear();
      throw error;
    }
  },

  getIdentity: async () => {
    const claims = authStorage.getClaims();
    if (!claims) {
      throw new Error("Unauthorized");
    }
    return {
      id: String(claims.uid ?? claims.sub ?? "unknown"),
      fullName: claims.sub ?? "Trip Planner User",
    };
  },

  getPermissions: async () => {
    const claims = authStorage.getClaims();
    if (!claims) {
      throw new Error("Unauthorized");
    }
    return claims.role ?? "USER";
  },

  signup: async ({ username, email, password }: SignupCredentials) => {
    await httpClient(`${API_URL}/auth/signup`, {
      method: "POST",
      body: JSON.stringify({ username, email, password }),
    });
  },
};
