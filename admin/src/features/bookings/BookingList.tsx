import {
  ChipField,
  Datagrid,
  DateField,
  FilterButton,
  List,
  ReferenceField,
  SearchInput,
  SelectInput,
  TextField,
  TopToolbar,
} from "react-admin";

import type { BookingStatus } from "../../shared/types";

const bookingStatusChoices: { id: BookingStatus; name: string }[] = [
  { id: "PENDING", name: "Pending" },
  { id: "CONFIRMED", name: "Confirmed" },
  { id: "CANCELLED", name: "Cancelled" },
];

const bookingFilters = [
  <SearchInput key="search" source="q" alwaysOn />,
  <SelectInput
    key="status"
    source="status"
    choices={bookingStatusChoices}
    optionValue="id"
  />,
  <SearchInput key="userId" source="userId" />,
];

export function BookingList() {
  return (
    <List
      filters={bookingFilters}
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
        <DateField source="checkIn" />
        <DateField source="checkOut" />
        <ChipField source="status" />
      </Datagrid>
    </List>
  );
}
