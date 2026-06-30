import {
  AutocompleteInput,
  NumberInput,
  ReferenceInput,
  SimpleForm,
  TextInput,
  maxLength,
  maxValue,
  minValue,
  required,
} from "react-admin";

export function PlaceForm() {
  return (
    <SimpleForm>
      <TextInput source="name" validate={[required(), maxLength(100)]} />
      <TextInput source="address" validate={[required(), maxLength(255)]} />
      <ReferenceInput source="cityId" reference="cities" label="City">
        <AutocompleteInput optionText="name" validate={required()} />
      </ReferenceInput>
      <ReferenceInput
        source="placeTypeId"
        reference="place-types"
        label="Place type"
      >
        <AutocompleteInput optionText="name" validate={required()} />
      </ReferenceInput>
      <NumberInput
        source="latitude"
        validate={[required(), minValue(-90), maxValue(90)]}
      />
      <NumberInput
        source="longitude"
        validate={[required(), minValue(-180), maxValue(180)]}
      />
      <TextInput
        source="description"
        multiline
        fullWidth
        validate={maxLength(1000)}
      />
    </SimpleForm>
  );
}
