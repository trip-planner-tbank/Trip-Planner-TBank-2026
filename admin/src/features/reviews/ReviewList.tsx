import {
  Datagrid,
  DateField,
  EditButton,
  FilterButton,
  List,
  ReferenceField,
  ReferenceInput,
  SearchInput,
  SelectInput,
  ShowButton,
  TextField,
  TopToolbar,
} from "react-admin";

const reviewFilters = [
  <SearchInput key="search" source="q" alwaysOn />,
  <ReferenceInput key="placeId" source="placeId" reference="places">
    <SelectInput optionText="name" />
  </ReferenceInput>,
  <SearchInput key="userId" source="userId" />,
];

export function ReviewList() {
  return (
    <List
      filters={reviewFilters}
      actions={
        <TopToolbar>
          <FilterButton />
        </TopToolbar>
      }
    >
      <Datagrid rowClick="show">
        <TextField source="id" />
        <TextField source="userId" />
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
