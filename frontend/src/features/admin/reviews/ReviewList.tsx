import {
  Datagrid,
  DateField,
  EditButton,
  FilterButton,
  List,
  ReferenceField,
  ReferenceInput,
  SelectInput,
  ShowButton,
  TextField,
  TopToolbar,
  CreateButton,
} from "react-admin";

const reviewFilters = [
  <ReferenceInput key="placeId" source="placeId" reference="places">
    <SelectInput optionText="name" />
  </ReferenceInput>,
  <ReferenceInput key="cityId" source="cityId" reference="cities">
    <SelectInput optionText="name" />
  </ReferenceInput>,
];

export function ReviewList() {
  return (
    <List
      filters={reviewFilters}
      actions={
        <TopToolbar>
          <FilterButton />
          <CreateButton />
        </TopToolbar>
      }
    >
      <Datagrid rowClick="show">
        <TextField source="id" />
        <TextField source="userId" label="User ID" />
        <ReferenceField source="placeId" reference="places" link="show">
          <TextField source="name" />
        </ReferenceField>
        <TextField source="rating" />
        <DateField source="createdAt" showTime />
        <ShowButton />
        <EditButton />
      </Datagrid>
    </List>
  );
}
