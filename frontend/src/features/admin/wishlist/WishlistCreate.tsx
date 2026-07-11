import {
  AutocompleteInput,
  Create,
  ReferenceInput,
  SimpleForm,
  required,
} from "react-admin";

export function WishlistCreate() {
  return (
    <Create title="Add place to my wishlist">
      <SimpleForm>
        <ReferenceInput source="placeId" reference="places" label="Place">
          <AutocompleteInput optionText="name" validate={required()} />
        </ReferenceInput>
      </SimpleForm>
    </Create>
  );
}
