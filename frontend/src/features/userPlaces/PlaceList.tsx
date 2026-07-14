import type { ReactElement } from "react";
import {
  Datagrid,
  List,
  NumberField,
  ReferenceField,
  ReferenceInput,
  SelectInput,
  ShowButton,
  TextField,
} from "react-admin";

const placeFilters: ReactElement[] = [
  <ReferenceInput key="cityId" source="cityId" reference="cities" label="City">
    <SelectInput optionText="name" />
  </ReferenceInput>,
  <ReferenceInput
    key="placeTypeId"
    source="placeTypeId"
    reference="place-types"
    label="Type"
  >
    <SelectInput optionText="name" />
  </ReferenceInput>,
];

export function PlaceList() {
  return (
    <List filters={placeFilters}>
      <Datagrid rowClick="show" bulkActionButtons={false}>
        <TextField source="name" />
        <ReferenceField
          source="placeTypeId"
          reference="place-types"
          label="Type"
        >
          <TextField source="name" />
        </ReferenceField>
        <ReferenceField source="cityId" reference="cities" label="City">
          <TextField source="name" />
        </ReferenceField>
        <NumberField source="avgRating" />
        <ShowButton />
      </Datagrid>
    </List>
  );
}
