import {
  AutocompleteInput,
  Create,
  NumberInput,
  ReferenceInput,
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
        <ReferenceInput source="cityId" reference="cities" label="City">
          <AutocompleteInput optionText="name" validate={required()} />
        </ReferenceInput>
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
