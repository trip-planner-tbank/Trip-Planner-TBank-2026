import {
  Datagrid,
  DateField,
  EditButton,
  List,
  ShowButton,
  TextField,
  usePermissions,
} from "react-admin";

export function CityList() {
  const { permissions } = usePermissions();
  return (
    <List>
      <Datagrid rowClick="show">
        <TextField source="id" />
        <TextField source="name" />
        <TextField source="country" />
        <DateField source="createdAt" showTime />
        <ShowButton />
        {permissions === "ADMIN" && <EditButton />}
      </Datagrid>
    </List>
  );
}
