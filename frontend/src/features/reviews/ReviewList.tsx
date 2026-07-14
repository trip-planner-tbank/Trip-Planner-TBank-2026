import {
  Datagrid,
  DateField,
  EditButton,
  List,
  ReferenceField,
  TextField,
} from "react-admin";

export function ReviewList() {
  return (
    <List>
      <Datagrid rowClick={false} bulkActionButtons={false}>
        <ReferenceField source="placeId" reference="places" link="show">
          <TextField source="name" />
        </ReferenceField>
        <TextField source="rating" />
        <TextField source="comment" />
        <DateField source="createdAt" showTime />
        <EditButton />
      </Datagrid>
    </List>
  );
}
