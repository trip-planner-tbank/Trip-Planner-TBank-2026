import {
  DateField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

export function CityShow() {
  return (
    <Show>
      <SimpleShowLayout>
        <TextField source="id" />
        <TextField source="name" />
        <TextField source="country" />
        <DateField source="createdAt" showTime />
      </SimpleShowLayout>
    </Show>
  );
}
