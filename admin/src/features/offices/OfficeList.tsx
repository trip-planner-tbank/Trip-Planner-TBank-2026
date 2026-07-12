import {
  Datagrid,
  DateField,
  EditButton,
  List,
  NumberField,
  ReferenceField,
  ShowButton,
  TextField,
  usePermissions,
} from "react-admin";

export function OfficeList() {
  const { permissions } = usePermissions();
  return (
    <List>
      <Datagrid rowClick="show">
        <TextField source="id" />
        <TextField source="name" />
        <TextField source="address" />
        <ReferenceField
          source="cityId"
          reference="cities"
          label="City"
          link="show"
        >
          <TextField source="name" />
        </ReferenceField>
        <NumberField source="latitude" />
        <NumberField source="longitude" />
        <DateField source="createdAt" showTime />
        <ShowButton />
        {permissions === "ADMIN" && <EditButton />}
      </Datagrid>
    </List>
  );
}
