import {
  BooleanField,
  Datagrid,
  DeleteButton,
  EditButton,
  List,
  NumberField,
  ReferenceField,
  ShowButton,
  TextField,
} from "react-admin";

export function PlaceList() {
  return (
    <List>
      <Datagrid rowClick="show">
        <TextField source="id" />
        <TextField source="name" />
        <TextField source="address" />
        <ReferenceField
          source="placeTypeId"
          reference="place-types"
          label="Type"
        >
          <TextField source="name" />
        </ReferenceField>
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
        <ShowButton />
        <EditButton />
        <DeleteButton />
      </Datagrid>
    </List>
  );
}
