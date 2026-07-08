import { Create } from "react-admin";

import { PlaceForm } from "./PlaceForm";

export function PlaceCreate() {
  return (
    <Create>
      <PlaceForm />
    </Create>
  );
}
