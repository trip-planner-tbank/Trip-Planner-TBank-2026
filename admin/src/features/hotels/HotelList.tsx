import {
  BooleanField,
  Datagrid,
  List,
  NumberField,
  ReferenceField,
  TextField,
} from "react-admin";

export function HotelList() {
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
        <NumberField source="avgRating" />
        <BooleanField source="isActive" />
        <TextField source="hasDetails" label="Has details" />
      </Datagrid>
    </List>
  );
}
