import type { ResourceProps } from "react-admin";

import { HotelList } from "./HotelList";
import { HotelShow } from "./HotelShow";

export const hotelsResource: ResourceProps = {
  name: "hotels",
  list: HotelList,
  show: HotelShow,
  recordRepresentation: "name",
};
