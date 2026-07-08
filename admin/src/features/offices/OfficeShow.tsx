import {
  DateField,
  NumberField,
  ReferenceField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

export function OfficeShow() {
  return (
    <Show>
      <SimpleShowLayout>
        <TextField source="id" />
        <ReferenceField
          source="cityId"
          reference="cities"
          label="City"
          link="show"
        >
          <TextField source="name" />
        </ReferenceField>
        <TextField source="name" />
        <TextField source="address" />
        <NumberField source="latitude" />
        <NumberField source="longitude" />
        <DateField source="createdAt" showTime />
      </SimpleShowLayout>
    </Show>
  );
}
