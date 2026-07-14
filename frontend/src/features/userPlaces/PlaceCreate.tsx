import { useMemo } from "react";
import {
  AutocompleteInput,
  Create,
  NumberInput,
  ReferenceInput,
  SimpleForm,
  TextInput,
  useGetList,
  maxLength,
  maxValue,
  minValue,
  required,
} from "react-admin";

import type { PlaceType } from "../../shared/types";

export function PlaceCreate() {
  const { data: placeTypes, isLoading } = useGetList<PlaceType>("place-types", {
    pagination: { page: 1, perPage: 100 },
    sort: { field: "name", order: "ASC" },
  });

  const nonHotelTypes = useMemo(
    () => placeTypes?.filter((type) => type.code !== "HOTEL") ?? [],
    [placeTypes],
  );

  return (
    <Create>
      <SimpleForm>
        <TextInput source="name" validate={[required(), maxLength(100)]} />
        <TextInput source="address" validate={[required(), maxLength(255)]} />
        <ReferenceInput source="cityId" reference="cities" label="City">
          <AutocompleteInput optionText="name" validate={required()} />
        </ReferenceInput>
        <AutocompleteInput
          source="placeTypeId"
          label="Type"
          optionText="name"
          choices={nonHotelTypes}
          isLoading={isLoading}
          validate={required()}
        />
        <NumberInput
          source="latitude"
          validate={[required(), minValue(-90), maxValue(90)]}
        />
        <NumberInput
          source="longitude"
          validate={[required(), minValue(-180), maxValue(180)]}
        />
        <TextInput
          source="description"
          multiline
          fullWidth
          validate={maxLength(1000)}
        />
      </SimpleForm>
    </Create>
  );
}
