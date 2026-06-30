import {
  Edit,
  NumberInput,
  SimpleForm,
  TextInput,
} from "react-admin";

export function ReviewEdit() {
  return (
    <Edit>
      <SimpleForm>
        <NumberInput source="rating" min={1} max={5} />
        <TextInput source="comment" multiline fullWidth />
      </SimpleForm>
    </Edit>
  );
}
