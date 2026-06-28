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
}
