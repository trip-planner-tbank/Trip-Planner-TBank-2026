import {
  Datagrid,
  DateField,
  EditButton,
  List,
  NumberField,
  ReferenceField,
  ShowButton,
  TextField,
} from "react-admin";

export function OfficeList() {
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
        <EditButton />
      </Datagrid>
    </List>
  );
}
