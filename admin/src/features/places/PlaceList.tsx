import {
  BooleanField,
  Datagrid,
  List,
  NumberField,
  TextField,
} from "react-admin";

export function PlaceList() {
  return (
    <List>
      <Datagrid rowClick="show">
        <TextField source="id" />
        <TextField source="name" />
        <TextField source="address" />
        <NumberField source="placeTypeId" label="Type ID" />
        <NumberField source="cityId" label="City ID" />
        <NumberField source="avgRating" />
        <BooleanField source="isActive" />
      </Datagrid>
    </List>
  );
}
