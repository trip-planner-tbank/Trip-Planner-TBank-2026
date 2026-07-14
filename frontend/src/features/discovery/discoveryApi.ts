import { City, Office, Place, PlaceType } from "../../shared/types";
import { httpClient } from "../../shared/api/httpClient";
import { API_URL } from "../../shared/config/env";

export async function fetchCities(): Promise<City[]> {
  const { json } = await httpClient(`${API_URL}/cities`);
  return json as City[];
}

export async function fetchOffices(cityId: number): Promise<Office[]> {
  const { json } = await httpClient(`${API_URL}/offices?cityId=${cityId}`);
  return json as Office[];
}

export async function fetchPlaceTypes(): Promise<PlaceType[]> {
  const { json } = await httpClient(`${API_URL}/place-types`);
  return json as PlaceType[];
}

function buildQueryString(params: Record<string, string | number | null>) {
  const searchParams = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== null && value !== undefined && value !== "") {
      searchParams.append(key, String(value));
    }
  });
  const query = searchParams.toString();
  return query ? `?${query}` : "";
}

export async function fetchOfficeNearbyPlaces(
  office: Office,
  placeTypeId: number | null,
  maxDistanceKm: number | null,
): Promise<Place[]> {
  const query = buildQueryString({
    officeId: office.id,
    placeTypeId,
    maxDistanceKm,
  });
  const { json } = await httpClient(`${API_URL}/places${query}`);
  return json as Place[];
}

export async function fetchPlaceNearbyPlaces(
  placeId: number,
  placeTypeId: number | null,
  maxDistanceKm: number | null,
): Promise<Place[]> {
  const query = buildQueryString({
    placeTypeId,
    maxDistanceKm,
  });
  const { json } = await httpClient(`${API_URL}/places/${placeId}/nearby-places${query}`);
  return json as Place[];
}
