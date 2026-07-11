import {
  BooleanField,
  Datagrid,
  DeleteButton,
  EditButton,
  List,
  NumberField,
  ReferenceField,
  ShowButton,
  TextField,
  FilterButton,
  TopToolbar,
  ReferenceInput,
  SelectInput,
  NumberInput,
  usePermissions,
  CreateButton,
} from "react-admin";

const placeFilters = [
  <ReferenceInput key="cityId" source="cityId" reference="cities" label="City">
    <SelectInput optionText="name" />
  </ReferenceInput>,
  <ReferenceInput key="placeTypeId" source="placeTypeId" reference="place-types" label="Type">
    <SelectInput optionText="name" />
  </ReferenceInput>,
  <ReferenceInput key="officeId" source="officeId" reference="offices" label="Distance from office">
    <SelectInput optionText="name" />
  </ReferenceInput>,
  <ReferenceInput key="referencePlaceId" source="referencePlaceId" reference="places" label="Distance from place/hotel">
    <SelectInput optionText="name" />
  </ReferenceInput>,
  <NumberInput key="maxDistanceKm" source="maxDistanceKm" label="Maximum distance (km)" min={0} />,
];

export function PlaceList() {
  const { permissions } = usePermissions();
  const isAdmin = permissions === "ADMIN";
  return (
    <List
      filters={placeFilters}
      actions={
        <TopToolbar>
          <FilterButton />
          <CreateButton />
        </TopToolbar>
      }
    >
      <Datagrid rowClick="show">
        <TextField source="id" />
        <TextField source="name" />
        <TextField source="address" />
        <ReferenceField
          source="placeTypeId"
          reference="place-types"
          label="Type"
        >
          <TextField source="name" />
        </ReferenceField>
        <ReferenceField
          source="cityId"
          reference="cities"
          label="City"
          link="show"
        >
          <TextField source="name" />
        </ReferenceField>
        <NumberField source="avgRating" />
        <NumberField source="distanceKm" label="Distance (km)" options={{ maximumFractionDigits: 2 }} />
        <BooleanField source="isActive" />
        <ShowButton />
        {isAdmin && <EditButton />}
        {isAdmin && <DeleteButton />}
      </Datagrid>
    </List>
  );
}
