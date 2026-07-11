import { Edit, NumberInput, SimpleForm, TextInput } from "react-admin";

export function ReviewEdit() {
  return (
    <Edit>
      <SimpleForm>
        <NumberInput source="rating" label="Rating" min={1} max={5} />
        <TextInput
          source="comment"
          label="Comment"
          multiline
          fullWidth
        />
      </SimpleForm>
    </Edit>
  );
}
