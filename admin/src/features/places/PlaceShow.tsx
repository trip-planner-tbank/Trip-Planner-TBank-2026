import {
  BooleanField,
  DateField,
  NumberField,
  ReferenceField,
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
        <ReferenceField
          source="cityId"
          reference="cities"
          label="City"
          link="show"
        >
          <TextField source="name" />
        </ReferenceField>
        <ReferenceField
          source="placeTypeId"
          reference="place-types"
          label="Type"
        >
          <TextField source="name" />
        </ReferenceField>
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
