import { useRecordContext } from "react-admin";

import { AddToWishlistButton } from "./AddToWishlistButton";
import type { Place } from "../../shared/types";

export function ShowWishlistAction() {
  const record = useRecordContext<Place>();
  if (!record) return null;
  return <AddToWishlistButton placeId={record.id} />;
}
