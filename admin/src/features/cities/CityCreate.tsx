import {
  Create,
  SimpleForm,
  TextInput,
  maxLength,
  required,
} from "react-admin";

export function CityCreate() {
  return (
    <Create>
      <SimpleForm>
        <TextInput source="name" validate={[required(), maxLength(50)]} />
        <TextInput source="country" validate={[required(), maxLength(50)]} />
      </SimpleForm>
    </Create>
  );
}
