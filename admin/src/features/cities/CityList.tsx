import {
  Datagrid,
  DateField,
  EditButton,
  List,
  ShowButton,
  TextField,
} from "react-admin";

export function CityList() {
  return (
    <List>
      <Datagrid rowClick="show">
        <TextField source="id" />
        <TextField source="name" />
        <TextField source="country" />
        <DateField source="createdAt" showTime />
        <ShowButton />
        <EditButton />
      </Datagrid>
    </List>
  );
}
