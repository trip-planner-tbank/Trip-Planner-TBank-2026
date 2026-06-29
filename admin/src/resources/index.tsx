import BookingIcon from "@mui/icons-material/Bookmark";
import HotelIcon from "@mui/icons-material/Hotel";
import PlaceIcon from "@mui/icons-material/Place";
import RateReviewIcon from "@mui/icons-material/RateReview";
import type { ResourceProps } from "react-admin";

import {
  BookingCreate,
  BookingList,
  BookingShow,
  BookingStatusEdit,
} from "../features/bookings";
import {
  HotelCreate,
  HotelDetailsEdit,
  HotelList,
  HotelShow,
} from "../features/hotels";
import { PlaceCreate, PlaceList, PlaceShow } from "../features/places";
import { ReviewCreate, ReviewList, ReviewShow } from "../features/reviews";

export const resources: ResourceProps[] = [
  {
    name: "bookings",
    list: BookingList,
    show: BookingShow,
    create: BookingCreate,
    edit: BookingStatusEdit,
    icon: BookingIcon,
    recordRepresentation: "id",
  },
  {
    name: "reviews",
    list: ReviewList,
    show: ReviewShow,
    create: ReviewCreate,
    icon: RateReviewIcon,
    recordRepresentation: "id",
  },
  {
    name: "hotels",
    list: HotelList,
    show: HotelShow,
    create: HotelCreate,
    edit: HotelDetailsEdit,
    icon: HotelIcon,
    recordRepresentation: "name",
  },
  {
    name: "places",
    list: PlaceList,
    show: PlaceShow,
    create: PlaceCreate,
    icon: PlaceIcon,
    recordRepresentation: "name",
  },
];
