import {
  Edit,
  NumberInput,
  SimpleForm,
  TextInput,
} from "react-admin";

export function HotelDetailsEdit() {
  return (
    <Edit>
      <SimpleForm>
        <NumberInput source="starRating" label="Star rating" min={1} max={5} />
        <TextInput source="phone" />
        <TextInput source="website" />
        <NumberInput source="roomCount" label="Room count" min={1} />
      </SimpleForm>
    </Edit>
  );
}
