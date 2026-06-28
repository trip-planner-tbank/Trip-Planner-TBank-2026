import {
  Create,
  DateInput,
  ReferenceInput,
  SelectInput,
  SimpleForm,
  required,
} from "react-admin";

export function BookingCreate() {
  return (
    <Create>
      <SimpleForm>
        <ReferenceInput source="placeId" reference="places">
          <SelectInput optionText="name" validate={required()} />
        </ReferenceInput>
        <DateInput source="checkIn" validate={required()} />
        <DateInput source="checkOut" validate={required()} />
      </SimpleForm>
    </Create>
  );
}
