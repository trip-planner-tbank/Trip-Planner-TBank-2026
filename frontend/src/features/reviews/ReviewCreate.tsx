import { Typography } from "@mui/material";
import { useSearchParams } from "react-router-dom";
import {
  AutocompleteInput,
  Create,
  NumberInput,
  ReferenceInput,
  SimpleForm,
  TextInput,
  required,
} from "react-admin";

import { MyReviewForm } from "./MyReviewForm";

export function ReviewCreate() {
  const [searchParams] = useSearchParams();
  const placeId = Number(searchParams.get("placeId"));

  if (placeId) {
    return <MyReviewForm placeId={placeId} />;
  }

  return (
    <Create>
      <SimpleForm>
        <ReferenceInput source="placeId" reference="places" label="Place">
          <AutocompleteInput optionText="name" validate={required()} />
        </ReferenceInput>
        <NumberInput
          source="rating"
          label="Rating"
          min={1}
          max={5}
          validate={required()}
        />
        <TextInput source="comment" label="Comment" multiline fullWidth />
      </SimpleForm>
    </Create>
  );
}
