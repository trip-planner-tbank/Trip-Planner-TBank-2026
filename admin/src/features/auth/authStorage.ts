const ACCESS_TOKEN_KEY = "tripPlanner.accessToken";
const REFRESH_TOKEN_KEY = "tripPlanner.refreshToken";

export type TokenClaims = {
  sub?: string;
  uid?: number;
  role?: "ADMIN" | "USER";
  exp?: number;
};

export const authStorage = {
  getAccessToken() {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  },

  getRefreshToken() {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  },

  getClaims(): TokenClaims | null {
    const token = this.getAccessToken();
    if (!token) return null;
    try {
      const payload = token.split(".")[1];
      return JSON.parse(
        decodeURIComponent(
          atob(payload.replace(/-/g, "+").replace(/_/g, "/"))
            .split("")
            .map((character) => `%${character.charCodeAt(0).toString(16).padStart(2, "0")}`)
            .join(""),
        ),
      ) as TokenClaims;
    } catch {
      return null;
    }
  },

  setTokens(accessToken: string, refreshToken?: string) {
    localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);

    if (refreshToken) {
      localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
    }
  },

  clear() {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
  },
};
