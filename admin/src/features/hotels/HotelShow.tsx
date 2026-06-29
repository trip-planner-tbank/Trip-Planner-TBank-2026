import {
  BooleanField,
  NumberField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

export function HotelShow() {
  return (
    <Show>
      <SimpleShowLayout>
        <TextField source="id" />
        <TextField source="name" />
        <TextField source="address" />
        <TextField source="description" />
        <NumberField source="latitude" />
        <NumberField source="longitude" />
        <BooleanField source="isActive" />
        <NumberField source="avgRating" />

        <TextField source="starRating" label="Star rating" />
        <TextField source="phone" />
        <TextField source="website" />
        <NumberField source="roomCount" label="Room count" />
      </SimpleShowLayout>
    </Show>
  );
}
