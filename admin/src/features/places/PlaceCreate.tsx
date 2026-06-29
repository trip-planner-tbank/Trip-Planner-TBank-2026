import {
  BooleanInput,
  Create,
  NumberInput,
  SimpleForm,
  TextInput,
  required,
} from "react-admin";

export function PlaceCreate() {
  return (
    <Create>
      <SimpleForm>
        <TextInput source="name" validate={required()} />
        <TextInput source="address" validate={required()} />
        <NumberInput source="cityId" validate={required()} />
        <NumberInput source="placeTypeId" validate={required()} />
        <NumberInput source="latitude" validate={required()} />
        <NumberInput source="longitude" validate={required()} />
        <TextInput source="description" multiline fullWidth />
        <BooleanInput source="isActive" defaultValue={true} />
      </SimpleForm>
    </Create>
  );
}
