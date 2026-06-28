import {
  Edit,
  SelectInput,
  SimpleForm,
} from "react-admin";

import type { BookingStatus } from "../../shared/types";

const statusChoices: { id: BookingStatus; name: string }[] = [
  { id: "PENDING", name: "Pending" },
  { id: "CONFIRMED", name: "Confirmed" },
  { id: "CANCELLED", name: "Cancelled" },
];

export function BookingStatusEdit() {
  return (
    <Edit transform={(data) => ({ status: data.status })}>
      <SimpleForm>
        <SelectInput
          source="status"
          choices={statusChoices}
          optionValue="id"
          required
        />
      </SimpleForm>
    </Edit>
  );
}
