import { fetchUtils } from "react-admin";

import { authStorage } from "../../features/auth/authStorage";
import { API_URL } from "../config/env";

async function request(url: string, options: fetchUtils.Options = {}, retry = true) {
  const headers =
    options.headers instanceof Headers
      ? options.headers
      : new Headers(options.headers ?? { Accept: "application/json" });

  const token = authStorage.getAccessToken();

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  try {
    return await fetchUtils.fetchJson(url, { ...options, headers });
  } catch (error: any) {
    const refreshToken = authStorage.getRefreshToken();
    if (retry && error?.status === 401 && refreshToken && !url.endsWith("/auth/refresh")) {
      const response = await fetch(`${API_URL}/auth/refresh`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ refreshToken }),
      });
      if (response.ok) {
        const tokens = await response.json();
        authStorage.setTokens(tokens.accessToken, tokens.refreshToken);
        return request(url, options, false);
      }
      authStorage.clear();
    }
    throw error;
  }
}

export const httpClient: typeof fetchUtils.fetchJson = request;
