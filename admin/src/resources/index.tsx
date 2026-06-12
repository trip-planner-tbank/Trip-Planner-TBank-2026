import BusinessIcon from "@mui/icons-material/Business";
import HotelIcon from "@mui/icons-material/Hotel";
import LocationCityIcon from "@mui/icons-material/LocationCity";
import RateReviewIcon from "@mui/icons-material/RateReview";
import type { ResourceProps } from "react-admin";
import { EditGuesser, ListGuesser, ShowGuesser } from "react-admin";

export const resources: ResourceProps[] = [
  {
    name: "cities",
    list: ListGuesser,
    edit: EditGuesser,
    show: ShowGuesser,
    icon: LocationCityIcon,
    recordRepresentation: "name",
  },
  {
    name: "offices",
    list: ListGuesser,
    edit: EditGuesser,
    show: ShowGuesser,
    icon: BusinessIcon,
    recordRepresentation: "name",
  },
  {
    name: "hotels",
    list: ListGuesser,
    edit: EditGuesser,
    show: ShowGuesser,
    icon: HotelIcon,
    recordRepresentation: "name",
  },
  {
    name: "reviews",
    list: ListGuesser,
    show: ShowGuesser,
    icon: RateReviewIcon,
  },
];
