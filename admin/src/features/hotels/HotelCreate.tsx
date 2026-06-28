import {
  Create,
  NumberInput,
  SimpleForm,
  TextInput,
  required,
} from "react-admin";

export function HotelCreate() {
  return (
    <Create>
      <SimpleForm>
        <TextInput source="name" validate={required()} />
        <TextInput source="address" validate={required()} />
        <NumberInput source="cityId" validate={required()} />
        <NumberInput source="latitude" validate={required()} />
        <NumberInput source="longitude" validate={required()} />
        <TextInput source="description" multiline fullWidth />
        <NumberInput
          source="starRating"
          min={1}
          max={5}
          validate={required()}
        />
        <TextInput source="phone" />
        <TextInput source="website" />
        <NumberInput source="roomCount" validate={required()} />
      </SimpleForm>
    </Create>
  );
}
