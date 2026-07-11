import type { ResourceProps } from "react-admin";
import HotelIcon from "@mui/icons-material/Hotel";

import { HotelList } from "./HotelList";
import { HotelShow } from "./HotelShow";

export const hotelsResource: ResourceProps = {
  name: "hotels",
  list: HotelList,
  show: HotelShow,
  icon: HotelIcon,
  recordRepresentation: "name",
};
