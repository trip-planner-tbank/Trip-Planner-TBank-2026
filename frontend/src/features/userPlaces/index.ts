import type { ResourceProps } from "react-admin";
import PlaceIcon from "@mui/icons-material/Place";

import { PlaceCreate } from "./PlaceCreate";
import { PlaceList } from "./PlaceList";
import { PlaceShow } from "./PlaceShow";

export const placesResource: ResourceProps = {
  name: "places",
  list: PlaceList,
  show: PlaceShow,
  create: PlaceCreate,
  icon: PlaceIcon,
  recordRepresentation: "name",
};
