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

export function OfficeForm() {
  return (
    <SimpleForm>
      <ReferenceInput source="cityId" reference="cities" label="City">
        <AutocompleteInput optionText="name" validate={required()} />
      </ReferenceInput>
      <TextInput source="name" validate={[required(), maxLength(100)]} />
      <TextInput source="address" validate={[required(), maxLength(255)]} />
      <NumberInput
        source="latitude"
        validate={[required(), minValue(-90), maxValue(90)]}
      />
      <NumberInput
        source="longitude"
        validate={[required(), minValue(-180), maxValue(180)]}
      />
    </SimpleForm>
  );
}
