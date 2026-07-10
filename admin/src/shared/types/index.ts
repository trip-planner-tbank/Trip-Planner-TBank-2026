export interface City {
  id: number;
  name: string;
  country: string;
  createdAt: string;
}

export interface Office {
  id: number;
  cityId: number;
  name: string;
  address: string;
  latitude: number;
  longitude: number;
  createdAt: string;
}

export interface PlaceType {
  id: number;
  name: string;
  code: string;
}

export interface Review {
  id: number;
  userId: number;
  placeId: number;
  rating: number;
  comment?: string;
  createdAt: string;
}

export interface HotelDetails {
  id: number;
  placeId: number;
  starRating: number;
  phone?: string;
  website?: string;
  roomCount: number;
}

export interface Place {
  id: number;
  cityId: number;
  placeTypeId: number;
  createdBy: number;
  name: string;
  address: string;
  latitude: number;
  longitude: number;
  description?: string;
  isActive: boolean;
  avgRating: number;
  createdAt: string;
  distanceKm?: number;
}

export interface WishlistEntry {
  id: number;
  userId: number;
  placeId: number;
  addedAt: string;
  place: Pick<
    Place,
    | "id"
    | "cityId"
    | "placeTypeId"
    | "name"
    | "address"
    | "latitude"
    | "longitude"
    | "avgRating"
    | "isActive"
  >;
}
