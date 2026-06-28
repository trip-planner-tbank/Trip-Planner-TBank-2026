import {
  Datagrid,
  DateField,
  FilterButton,
  List,
  ReferenceField,
  SearchInput,
  TextField,
  TopToolbar,
} from "react-admin";

const reviewFilters = [
  <SearchInput key="search" source="q" alwaysOn />,
  <SearchInput key="placeId" source="placeId" />,
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
      </Datagrid>
    </List>
  );
}
