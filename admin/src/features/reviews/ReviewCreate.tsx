import {
  Create,
  NumberInput,
  ReferenceInput,
  SelectInput,
  SimpleForm,
  TextInput,
  required,
} from "react-admin";

export function ReviewCreate() {
  return (
    <Create>
      <SimpleForm>
        <ReferenceInput source="placeId" reference="places">
          <SelectInput optionText="name" validate={required()} />
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
