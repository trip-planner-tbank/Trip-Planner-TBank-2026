import { Create } from "react-admin";

import { OfficeForm } from "./OfficeForm";

export function OfficeCreate() {
  return (
    <Create>
      <OfficeForm />
    </Create>
  );
}
