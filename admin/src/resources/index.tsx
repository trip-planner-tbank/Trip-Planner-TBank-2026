import BusinessIcon from "@mui/icons-material/Business";
import CategoryIcon from "@mui/icons-material/Category";
import HotelIcon from "@mui/icons-material/Hotel";
import LocationCityIcon from "@mui/icons-material/LocationCity";
import PlaceIcon from "@mui/icons-material/Place";
import RateReviewIcon from "@mui/icons-material/RateReview";
import FavoriteIcon from "@mui/icons-material/Favorite";
import type { ResourceProps } from "react-admin";

import { CityCreate, CityEdit, CityList, CityShow } from "../features/cities";
import {
  HotelCreate,
  HotelDetailsEdit,
  HotelList,
  HotelShow,
} from "../features/hotels";
import {
  OfficeCreate,
  OfficeEdit,
  OfficeList,
  OfficeShow,
} from "../features/offices";
import {
  PlaceCreate,
  PlaceEdit,
  PlaceList,
  PlaceShow,
} from "../features/places";
import { PlaceTypeList } from "../features/placeTypes";
import {
  ReviewCreate,
  ReviewEdit,
  ReviewList,
  ReviewShow,
} from "../features/reviews";
import { WishlistCreate, WishlistList } from "../features/wishlist";

export const resources: ResourceProps[] = [
  {
    name: "wishlists",
    options: { label: "My wishlist" },
    list: WishlistList,
    create: WishlistCreate,
    icon: FavoriteIcon,
    recordRepresentation: "place.name",
  },
  {
    name: "cities",
    list: CityList,
    show: CityShow,
    create: CityCreate,
    edit: CityEdit,
    icon: LocationCityIcon,
    recordRepresentation: "name",
  },
  {
    name: "offices",
    list: OfficeList,
    show: OfficeShow,
    create: OfficeCreate,
    edit: OfficeEdit,
    icon: BusinessIcon,
    recordRepresentation: "name",
  },
  {
    name: "place-types",
    list: PlaceTypeList,
    icon: CategoryIcon,
    recordRepresentation: "name",
  },
  {
    name: "reviews",
    list: ReviewList,
    show: ReviewShow,
    create: ReviewCreate,
    edit: ReviewEdit,
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
    edit: PlaceEdit,
    icon: PlaceIcon,
    recordRepresentation: "name",
  },
];
