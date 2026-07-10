import {
  Datagrid,
  List,
  TextField,
  TextInput,
} from "react-admin";

const hotelFilters = [
  <TextInput key="cityId" source="cityId" label="City ID" alwaysOn />,
];

export function HotelList() {
  return (
    <List
      resource="places"
      filters={hotelFilters}
      filterDefaultValues={{ placeTypeId: 1 }}
      title="Hotels"
    >
      <Datagrid rowClick="show">
        <TextField source="name" />
        <TextField source="address" />
        <TextField source="avgRating" />
      </Datagrid>
    </List>
  );
}
