import {
  BooleanField,
  Datagrid,
  List,
  NumberField,
  TextField,
} from "react-admin";

export function HotelList() {
  return (
    <List>
      <Datagrid rowClick="show">
        <TextField source="id" />
        <TextField source="name" />
        <TextField source="address" />
        <NumberField source="avgRating" />
        <BooleanField source="isActive" />
        <TextField source="hasDetails" label="Has details" />
      </Datagrid>
    </List>
  );
}
