export type Role = "ADMIN" | "USER";

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
}

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
  distanceKm?: number;
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

export type BookingStatus = "PENDING" | "CONFIRMED" | "CANCELLED";

export interface Booking {
  id: number;
  userId: number;
  placeId: number;
  checkIn: string;
  checkOut: string;
  status: BookingStatus;
  createdAt: string;
  updatedAt: string;
}

export interface Review {
  id: number;
  userId: number;
  placeId: number;
  rating: number;
  comment?: string;
  createdAt: string;
}

export interface WishlistEntry {
  id: number;
  userId: number;
  placeId: number;
  addedAt: string;
  place: PlaceSummary;
}

export interface PlaceSummary {
  id: number;
  cityId: number;
  placeTypeId: number;
  name: string;
  address: string;
  latitude: number;
  longitude: number;
  avgRating: number;
  isActive: boolean;
}

export interface CreatePlaceRequest {
  cityId: number;
  placeTypeId: number;
  name: string;
  address: string;
  latitude: number;
  longitude: number;
  description?: string;
}

export interface CreateBookingRequest {
  placeId: number;
  checkIn: string;
  checkOut: string;
}

export interface CreateReviewRequest {
  placeId: number;
  rating: number;
  comment?: string;
}

export interface UpdateReviewRequest {
  rating?: number;
  comment?: string;
}

export interface CreateWishlistRequest {
  placeId: number;
}
