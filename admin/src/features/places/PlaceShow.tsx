import {
  BooleanField,
  DateField,
  NumberField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

export function PlaceShow() {
  return (
    <Show>
      <SimpleShowLayout>
        <TextField source="id" />
        <TextField source="name" />
        <TextField source="address" />
        <NumberField source="cityId" label="City ID" />
        <NumberField source="placeTypeId" label="Type ID" />
        <NumberField source="createdBy" label="Created by" />
        <TextField source="description" />
        <NumberField source="latitude" />
        <NumberField source="longitude" />
        <BooleanField source="isActive" />
        <NumberField source="avgRating" />
        <DateField source="createdAt" showTime />
      </SimpleShowLayout>
    </Show>
  );
}
