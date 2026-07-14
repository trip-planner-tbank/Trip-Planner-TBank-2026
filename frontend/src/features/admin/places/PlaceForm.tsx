import {
  NumberInput,
  ReferenceInput,
  SelectInput,
  SimpleForm,
  TextInput,
  maxLength,
  maxValue,
  minValue,
  required,
  useGetList,
  usePermissions,
} from "react-admin";

export function PlaceForm() {
  const { permissions } = usePermissions();
  const { data: placeTypes = [], isPending } = useGetList("place-types", {
    pagination: { page: 1, perPage: 100 },
  });
  const choices = permissions === "ADMIN"
    ? placeTypes
    : placeTypes.filter((type) => type.code !== "HOTEL");

  return (
    <SimpleForm>
      <TextInput source="name" validate={[required(), maxLength(100)]} />
      <TextInput source="address" validate={[required(), maxLength(255)]} />
      <ReferenceInput source="cityId" reference="cities" label="City">
        <SelectInput optionText="name" validate={required()} />
      </ReferenceInput>
      <SelectInput
        source="placeTypeId"
        label="Place type"
        choices={choices}
        optionText="name"
        disabled={isPending}
        validate={required()}
      />
      <NumberInput
        source="latitude"
        helperText="Optional. Leave both coordinates empty to geocode the address automatically."
        validate={[minValue(-90), maxValue(90)]}
      />
      <NumberInput
        source="longitude"
        helperText="Optional. Latitude and longitude must be supplied together."
        validate={[minValue(-180), maxValue(180)]}
      />
      <TextInput
        source="description"
        multiline
        fullWidth
        validate={maxLength(1000)}
      />
    </SimpleForm>
  );
}
