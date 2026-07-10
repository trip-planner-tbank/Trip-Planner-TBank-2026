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

function toRad(degrees: number): number {
  return (degrees * Math.PI) / 180;
}

function haversineKm(
  lat1: number,
  lon1: number,
  lat2: number,
  lon2: number,
): number {
  const R = 6371;
  const dLat = toRad(lat2 - lat1);
  const dLon = toRad(lon2 - lon1);
  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(toRad(lat1)) *
      Math.cos(toRad(lat2)) *
      Math.sin(dLon / 2) *
      Math.sin(dLon / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}

function applyClientSideFilters(
  places: Place[],
  origin: { latitude: number; longitude: number },
  placeTypeId: number | null,
  maxDistanceKm: number | null,
): Place[] {
  const withDistance = places.map((place) => ({
    ...place,
    distanceKm: haversineKm(
      origin.latitude,
      origin.longitude,
      place.latitude,
      place.longitude,
    ),
  }));

  let filtered = withDistance;
  if (placeTypeId !== null) {
    filtered = filtered.filter((place) => place.placeTypeId === placeTypeId);
  }
  if (maxDistanceKm !== null) {
    filtered = filtered.filter(
      (place) => (place.distanceKm ?? Infinity) <= maxDistanceKm,
    );
  }
  return sortPlacesByDistance(filtered);
}

export async function fetchOfficeNearbyPlaces(
  office: Office,
  placeTypeId: number | null,
  maxDistanceKm: number | null,
): Promise<Place[]> {
  const { json } = await httpClient(`${API_URL}/places`);
  const places = json as Place[];
  return applyClientSideFilters(places, office, placeTypeId, maxDistanceKm);
}

export async function fetchPlaceNearbyPlaces(
  placeId: number,
  placeTypeId: number | null,
  maxDistanceKm: number | null,
): Promise<Place[]> {
  // Fetch the origin place and all places, then compute distances client-side.
  const [{ json: originJson }, { json: allJson }] = await Promise.all([
    httpClient(`${API_URL}/places/${placeId}`),
    httpClient(`${API_URL}/places`),
  ]);
  const origin = originJson as Place;
  const places = allJson as Place[];
  return applyClientSideFilters(places, origin, placeTypeId, maxDistanceKm);
}

function sortPlacesByDistance(places: Place[]): Place[] {
  return [...places].sort(
    (a, b) => (a.distanceKm ?? Infinity) - (b.distanceKm ?? Infinity),
  );
}
