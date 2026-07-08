import {
  DateField,
  ReferenceField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

export function ReviewShow() {
  return (
    <Show>
      <SimpleShowLayout>
        <TextField source="id" />
        <TextField source="userId" />
        <ReferenceField source="placeId" reference="places" link="show">
          <TextField source="name" />
        </ReferenceField>
        <TextField source="rating" />
        <TextField source="comment" />
        <DateField source="createdAt" showTime />
      </SimpleShowLayout>
    </Show>
  );
}
