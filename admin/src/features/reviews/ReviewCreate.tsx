import {
  AutocompleteInput,
  Create,
  NumberInput,
  ReferenceInput,
  SimpleForm,
  TextInput,
  required,
} from "react-admin";

export function ReviewCreate() {
  return (
    <Create>
      <SimpleForm>
        <ReferenceInput source="placeId" reference="places">
          <AutocompleteInput optionText="name" validate={required()} />
        </ReferenceInput>
        <NumberInput
          source="rating"
          min={1}
          max={5}
          validate={required()}
        />
        <TextInput source="comment" multiline fullWidth />
      </SimpleForm>
    </Create>
  );
}
