import type { AuthProvider } from "react-admin";

import { API_URL } from "../../shared/config/env";
import { httpClient } from "../../shared/api/httpClient";
import { authStorage } from "../../features/auth/authStorage";
import type { TokenResponse } from "../../shared/types";

interface LoginCredentials {
  username: string;
  password: string;
}

interface SignupCredentials {
  username: string;
  email: string;
  password: string;
}

interface JwtPayload {
  sub?: string;
  username?: string;
  role?: string;
  permissions?: string[];
  exp?: number;
}

const decodeJwt = (token: string): JwtPayload | null => {
  try {
    const base64 = token.split(".")[1];
    if (!base64) return null;
    const json = atob(base64.replace(/-/g, "+").replace(/_/g, "/"));
    return JSON.parse(json) as JwtPayload;
  } catch {
    return null;
  }
};

const isTokenExpired = (token: string): boolean => {
  const payload = decodeJwt(token);
  if (!payload?.exp) return false;
  return payload.exp * 1000 < Date.now();
};

export const authProvider: AuthProvider & { signup: (params: SignupCredentials) => Promise<void> } = {
  login: async ({ username, password }: LoginCredentials) => {
    const { json } = await httpClient(`${API_URL}/auth/login`, {
      method: "POST",
      body: JSON.stringify({ username, password }),
    });

    const tokenResponse = json as TokenResponse;
    if (!tokenResponse.accessToken || !tokenResponse.refreshToken) {
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
    if (!accessToken || isTokenExpired(accessToken)) {
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
    const accessToken = authStorage.getAccessToken();
    if (!accessToken) {
      throw new Error("Unauthorized");
    }

    const payload = decodeJwt(accessToken);
    const id = payload?.sub ?? payload?.username ?? "unknown";
    const fullName = payload?.username ?? id;

    return { id, fullName };
  },

  getPermissions: async () => {
    const accessToken = authStorage.getAccessToken();
    if (!accessToken) {
      throw new Error("Unauthorized");
    }

    const payload = decodeJwt(accessToken);
    return payload?.permissions ?? (payload?.role ? [payload.role] : []);
  },

  signup: async ({ username, email, password }: SignupCredentials) => {
    await httpClient(`${API_URL}/auth/signup`, {
      method: "POST",
      body: JSON.stringify({ username, email, password }),
    });
  },
};
