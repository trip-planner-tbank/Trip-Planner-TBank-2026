import {
  Datagrid,
  List,
  TextField,
} from "react-admin";

export function PlaceTypeList() {
  return (
    <List>
      <Datagrid bulkActionButtons={false}>
        <TextField source="id" />
        <TextField source="name" />
        <TextField source="code" />
      </Datagrid>
    </List>
  );
}
