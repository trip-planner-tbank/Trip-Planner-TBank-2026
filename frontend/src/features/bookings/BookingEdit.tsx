import { DateInput, Edit, SimpleForm, TextInput } from "react-admin";

export function BookingEdit() {
  return (
    <Edit>
      <SimpleForm>
        <TextInput source="placeId" label="Place" disabled />
        <DateInput source="checkIn" />
        <DateInput source="checkOut" />
      </SimpleForm>
    </Edit>
  );
}
