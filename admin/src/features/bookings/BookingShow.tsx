import {
  DateField,
  ReferenceField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

export function BookingShow() {
  return (
    <Show>
      <SimpleShowLayout>
        <TextField source="id" />
        <TextField source="userId" />
        <ReferenceField source="placeId" reference="places" link="show">
          <TextField source="name" />
        </ReferenceField>
        <DateField source="checkIn" />
        <DateField source="checkOut" />
        <TextField source="status" />
        <DateField source="createdAt" showTime />
        <DateField source="updatedAt" showTime />
      </SimpleShowLayout>
    </Show>
  );
}
