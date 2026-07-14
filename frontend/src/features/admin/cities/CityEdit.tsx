import {
  Edit,
  SimpleForm,
  TextInput,
  maxLength,
  required,
} from "react-admin";

export function CityEdit() {
  return (
    <Edit>
      <SimpleForm>
        <TextInput source="name" validate={[required(), maxLength(50)]} />
        <TextInput source="country" validate={[required(), maxLength(50)]} />
      </SimpleForm>
    </Edit>
  );
}
