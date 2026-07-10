import simpleRestProvider from "ra-data-simple-rest";

import { API_URL } from "../../shared/config/env";
import { httpClient } from "../../shared/api/httpClient";
import type { GetListParams, GetListResult, GetManyParams, GetManyResult, GetManyReferenceParams, GetManyReferenceResult, Identifier, RaRecord } from "react-admin";

const baseProvider = simpleRestProvider(API_URL, httpClient);

function normalizeArray<T>(value: unknown): T[] {
  return Array.isArray(value) ? (value as T[]) : [];
}

function getListData<T extends RaRecord>(json: unknown): T[] {
  if (Array.isArray(json)) return json as T[];
  if (json && typeof json === "object" && "content" in json) {
    return normalizeArray<T>((json as { content?: T[] }).content);
  }
  return [];
}

function matchesFilter(record: RaRecord, filter: Record<string, unknown>): boolean {
  return Object.entries(filter).every(([key, value]) => {
    if (value === undefined || value === null) return true;
    if (Array.isArray(value)) {
      return value.includes(record[key]);
    }
    return record[key] === value;
  });
}

async function fetchResourceList<T extends RaRecord>(resource: string): Promise<T[]> {
  const { json } = await httpClient(`${API_URL}/${resource}`);
  return getListData<T>(json);
}

export const dataProvider = {
  ...baseProvider,

  getList: async <T extends RaRecord>(
    resource: string,
    params: GetListParams,
  ): Promise<GetListResult<T>> => {
    // Offices support server-side filtering by cityId.
    let url = `${API_URL}/${resource}`;
    if (resource === "offices" && params.filter?.cityId) {
      url = `${API_URL}/offices?cityId=${params.filter.cityId}`;
    }

    const { json } = await httpClient(url);
    let data = getListData<T>(json);

    if (params.filter && Object.keys(params.filter).length > 0) {
      // When server-side filtering was applied (offices), skip client-side cityId filter.
      const clientFilter =
        resource === "offices" && params.filter.cityId
          ? { ...params.filter, cityId: undefined }
          : params.filter;
      data = data.filter((record) => matchesFilter(record, clientFilter));
    }

    if (params.sort) {
      const { field, order } = params.sort;
      data.sort((a, b) => {
        const aVal = a[field];
        const bVal = b[field];
        if (aVal === bVal) return 0;
        if (aVal === null || aVal === undefined) return 1;
        if (bVal === null || bVal === undefined) return -1;
        const result = aVal < bVal ? -1 : 1;
        return order === "ASC" ? result : -result;
      });
    }

    const total = data.length;
    const page = params.pagination?.page ?? 1;
    const perPage = params.pagination?.perPage ?? 25;
    const start = (page - 1) * perPage;
    const paginated = data.slice(start, start + perPage);

    return { data: paginated, total };
  },

  getMany: async <T extends RaRecord>(
    resource: string,
    params: GetManyParams,
  ): Promise<GetManyResult<T>> => {
    const data = await fetchResourceList<T>(resource);
    return { data: data.filter((item) => params.ids.includes(item.id as Identifier)) };
  },

  getManyReference: async <T extends RaRecord>(
    resource: string,
    params: GetManyReferenceParams,
  ): Promise<GetManyReferenceResult<T>> => {
    const data = await fetchResourceList<T>(resource);
    const filtered = data.filter((item) => item[params.target] === params.id);
    return { data: filtered, total: filtered.length };
  },
};
