import {
  Datagrid,
  DateField,
  List,
  ReferenceField,
  TextField,
} from "react-admin";

export function BookingList() {
  return (
    <List>
      <Datagrid rowClick="edit">
        <ReferenceField source="placeId" reference="places" link={false}>
          <TextField source="name" />
        </ReferenceField>
        <DateField source="checkIn" />
        <DateField source="checkOut" />
        <TextField source="status" />
      </Datagrid>
    </List>
  );
}
