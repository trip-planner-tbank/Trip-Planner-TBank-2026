import {
  Datagrid,
  DateField,
  DeleteButton,
  List,
  NumberField,
  TextField,
} from "react-admin";

export function WishlistList() {
  return (
    <List pagination={false} title="My wishlist">
      <Datagrid bulkActionButtons={false}>
        <TextField source="place.name" label="Place" />
        <TextField source="place.address" label="Address" />
        <NumberField source="place.avgRating" label="Rating" />
        <DateField source="addedAt" label="Added" showTime />
        <DeleteButton mutationMode="pessimistic" />
      </Datagrid>
    </List>
  );
}
