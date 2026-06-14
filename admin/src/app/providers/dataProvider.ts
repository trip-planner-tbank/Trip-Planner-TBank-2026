import simpleRestProvider from "ra-data-simple-rest";

import { httpClient } from "../../shared/api/httpClient";
import { API_URL } from "../../shared/config/env";

export const dataProvider = simpleRestProvider(API_URL, httpClient);
