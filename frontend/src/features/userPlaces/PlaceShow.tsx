import {
  Datagrid,
  DateField,
  NumberField,
  ReferenceField,
  ReferenceManyField,
  Show,
  SimpleShowLayout,
  TextField,
  useRecordContext,
} from "react-admin";

import { ShowWishlistAction } from "../wishlist/ShowWishlistAction";
import { MyReviewForm } from "../reviews/MyReviewForm";
import type { Place } from "../../shared/types";

function PlaceReviewForm() {
  const record = useRecordContext<Place>();
  if (!record) return null;
  return <MyReviewForm placeId={record.id} />;
}

export function PlaceShow() {
  return (
    <Show actions={<ShowWishlistAction />}>
      <SimpleShowLayout>
        <TextField source="name" />
        <TextField source="address" />
        <ReferenceField source="cityId" reference="cities" label="City">
          <TextField source="name" />
        </ReferenceField>
        <ReferenceField
          source="placeTypeId"
          reference="place-types"
          label="Type"
        >
          <TextField source="name" />
        </ReferenceField>
        <NumberField source="latitude" />
        <NumberField source="longitude" />
        <TextField source="description" />
        <NumberField source="avgRating" />
        <DateField source="createdAt" showTime />

        <ReferenceManyField
          reference="reviews"
          target="placeId"
          label="Reviews"
        >
          <Datagrid bulkActionButtons={false}>
            <TextField source="userId" label="User" />
            <NumberField source="rating" />
            <TextField source="comment" />
            <DateField source="createdAt" showTime />
          </Datagrid>
        </ReferenceManyField>

        <PlaceReviewForm />
      </SimpleShowLayout>
    </Show>
  );
}
