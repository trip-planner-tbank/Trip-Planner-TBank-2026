import type { ResourceProps } from "react-admin";

import { PlaceCreate } from "./PlaceCreate";
import { PlaceList } from "./PlaceList";
import { PlaceShow } from "./PlaceShow";

export const placesResource: ResourceProps = {
  name: "places",
  list: PlaceList,
  show: PlaceShow,
  create: PlaceCreate,
  recordRepresentation: "name",
};
