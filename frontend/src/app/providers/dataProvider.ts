import simpleRestProvider from "ra-data-simple-rest";
import type { DataProvider } from "react-admin";

import { httpClient } from "../../shared/api/httpClient";
import { API_URL } from "../../shared/config/env";
import type { HotelDetails, Place } from "../../shared/types";

const baseProvider = simpleRestProvider(API_URL, httpClient);

const ARRAY_LIST_RESOURCES = new Set([
  "cities",
  "offices",
  "place-types",
  "places",
  "reviews",
  "wishlists",
]);

let hotelPlaceTypeId: number | undefined;

async function getHotelPlaceTypeId() {
  if (hotelPlaceTypeId) return hotelPlaceTypeId;
  const { json } = await httpClient(`${API_URL}/place-types`);
  const hotelType = (json as { id: number; code: string }[]).find(
    (type) => type.code === "HOTEL",
  );
  if (!hotelType) throw new Error("HOTEL place type is not configured");
  hotelPlaceTypeId = hotelType.id;
  return hotelPlaceTypeId;
}

export const dataProvider: DataProvider = {
  ...baseProvider,

  getList: async (resource, params) => {
    if (resource === "hotels") {
      const typeId = await getHotelPlaceTypeId();
      const { json } = await httpClient(
        `${API_URL}/places?placeTypeId=${typeId}&page=0&size=100`,
      );
      const places = json as Place[];
      return {
        data: places.map((place) => ({ ...place, id: place.id })),
        total: places.length,
      };
    }

    const url = ARRAY_LIST_RESOURCES.has(resource)
      ? `${API_URL}/${resource}`
      : undefined;

    if (url) {
      const { page = 1, perPage = 25 } = params.pagination ?? {};
      const query = new URLSearchParams();
      if (resource !== "cities" && resource !== "offices"
          && resource !== "place-types" && resource !== "wishlists") {
        query.set("page", String(page - 1));
        query.set("size", String(perPage));
      }
      for (const [key, value] of Object.entries(params.filter ?? {})) {
        if (value !== undefined && value !== null && value !== "") {
          query.set(key, String(value));
        }
      }

      const { json } = await httpClient(`${url}?${query.toString()}`);

      if (Array.isArray(json)) {
        return { data: json, total: json.length };
      }

      const data = Array.isArray((json as any).content)
        ? (json as any).content
        : [];
      const total =
        typeof (json as any).totalElements === "number"
          ? (json as any).totalElements
          : data.length;

      return { data, total };
    }

    return baseProvider.getList(resource, params);
  },

  getOne: async (resource, params) => {
    if (resource === "hotels") {
      const [placeRes, detailsRes] = await Promise.all([
        httpClient(`${API_URL}/places/${params.id}`),
        httpClient(`${API_URL}/places/${params.id}/hotel-details`).catch(
          () => ({ json: null }),
        ),
      ]);
      const place = placeRes.json as Place;
      const details = detailsRes.json as HotelDetails | null;
      return {
        data: {
          ...place,
          starRating: details?.starRating,
          phone: details?.phone,
          website: details?.website,
          roomCount: details?.roomCount,
          hasDetails: !!details,
        },
      } as any;
    }
    if (resource === "wishlists") {
      const { json } = await httpClient(`${API_URL}/wishlists`);
      const entry = (json as any[]).find((item) => item.id == params.id);
      if (!entry) throw new Error("Wishlist entry not found");
      return { data: entry };
    }
    return baseProvider.getOne(resource, params);
  },

  getMany: async (resource, params) => {
    if (ARRAY_LIST_RESOURCES.has(resource)) {
      const { json } = await httpClient(`${API_URL}/${resource}`);
      const data = Array.isArray(json)
        ? json.filter((record: any) => params.ids.includes(record.id))
        : [];
      return { data };
    }

    return baseProvider.getMany(resource, params);
  },

  getManyReference: async (resource, params) => {
    if (ARRAY_LIST_RESOURCES.has(resource)) {
      const { json } = await httpClient(`${API_URL}/${resource}`);
      const data = Array.isArray(json)
        ? json.filter((record: any) => record[params.target] === params.id)
        : [];
      return { data, total: data.length };
    }

    return baseProvider.getManyReference(resource, params);
  },

  update: async (resource, params) => {
    if (resource === "places") {
      const {
        name,
        address,
        cityId,
        placeTypeId,
        latitude,
        longitude,
        description,
      } = params.data;

      const { json } = await httpClient(`${API_URL}/places/${params.id}`, {
        method: "PUT",
        body: JSON.stringify({
          name,
          address,
          cityId,
          placeTypeId,
          latitude,
          longitude,
          description,
        }),
      });
      return { data: json };
    }

    if (resource === "hotels") {
      const placeId = params.id;
      const { json } = await httpClient(
        `${API_URL}/places/${placeId}/hotel-details`,
        {
          method: "PUT",
          body: JSON.stringify({
            starRating: params.data.starRating,
            phone: params.data.phone,
            website: params.data.website,
            roomCount: params.data.roomCount,
          }),
        },
      );
      return {
        data: {
          ...(json as HotelDetails),
          id: placeId,
          hasDetails: true,
        },
      } as any;
    }

    if (resource === "reviews") {
      const { rating, comment } = params.data;
      const { json } = await httpClient(`${API_URL}/reviews/${params.id}`, {
        method: "PUT",
        body: JSON.stringify({ rating, comment }),
      });
      return { data: json };
    }

    return baseProvider.update(resource, params);
  },

  create: async (resource, params) => {
    if (resource === "hotels") {
      const typeId = await getHotelPlaceTypeId();
      const {
        name,
        address,
        cityId,
        latitude,
        longitude,
        description,
        starRating,
        phone,
        website,
        roomCount,
      } = params.data;

      const { json: placeJson } = await httpClient(`${API_URL}/places`, {
        method: "POST",
        body: JSON.stringify({
          name,
          address,
          cityId,
          placeTypeId: typeId,
          latitude,
          longitude,
          description,
        }),
      });

      const place = placeJson as Place;

      const { json: detailsJson } = await httpClient(
        `${API_URL}/places/${place.id}/hotel-details`,
        {
          method: "POST",
          body: JSON.stringify({ starRating, phone, website, roomCount }),
        },
      );

      return {
        data: {
          ...place,
          starRating: (detailsJson as HotelDetails).starRating,
          phone: (detailsJson as HotelDetails).phone,
          website: (detailsJson as HotelDetails).website,
          roomCount: (detailsJson as HotelDetails).roomCount,
          hasDetails: true,
        },
      } as any;
    }

    if (resource === "reviews") {
      const { json } = await httpClient(`${API_URL}/${resource}`, {
        method: "POST",
        body: JSON.stringify(params.data),
      });
      return { data: json };
    }

    if (resource === "wishlists") {
      const { json } = await httpClient(`${API_URL}/wishlists`, {
        method: "POST",
        body: JSON.stringify({ placeId: params.data.placeId }),
      });
      return { data: json };
    }

    if (resource === "places") {
      const {
        name,
        address,
        cityId,
        placeTypeId,
        latitude,
        longitude,
        description,
      } = params.data;
      const { json } = await httpClient(`${API_URL}/places`, {
        method: "POST",
        body: JSON.stringify({
          name,
          address,
          cityId,
          placeTypeId,
          latitude,
          longitude,
          description,
        }),
      });
      return { data: json };
    }

    return baseProvider.create(resource, params);
  },

  delete: async (resource, params) => {
    if (resource === "hotels") {
      throw new Error("Hotel deletion is not supported");
    }
    if (resource === "wishlists") {
      await httpClient(`${API_URL}/wishlists/${params.id}`, {
        method: "DELETE",
      });
      return { data: params.previousData ?? { id: params.id } } as any;
    }
    return baseProvider.delete(resource, params);
  },
};
