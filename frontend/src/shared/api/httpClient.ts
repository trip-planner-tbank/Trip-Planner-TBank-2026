import { fetchUtils } from "react-admin";

import { authStorage } from "../../features/auth/authStorage";

export const httpClient: typeof fetchUtils.fetchJson = (url, options = {}) => {
  const headers =
    options.headers instanceof Headers
      ? options.headers
      : new Headers(options.headers ?? { Accept: "application/json" });

  const token = authStorage.getAccessToken();
  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }

  return fetchUtils.fetchJson(url, {
    ...options,
    headers,
  });
};
