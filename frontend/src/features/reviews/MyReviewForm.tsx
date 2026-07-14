import { Box, Typography } from "@mui/material";
import {
  Create,
  NumberInput,
  SimpleForm,
  TextInput,
  required,
} from "react-admin";

interface MyReviewFormProps {
  placeId: number;
}

function ReviewFormFields() {
  return (
    <SimpleForm>
      <NumberInput
        source="rating"
        label="Rating"
        min={1}
        max={5}
        validate={required()}
      />
      <TextInput
        source="comment"
        label="Comment (optional)"
        multiline
        fullWidth
      />
    </SimpleForm>
  );
}

export function MyReviewForm({ placeId }: MyReviewFormProps) {
  return (
    <Box>
      <Typography variant="h6" sx={{ mb: 2 }}>
        Write a review
      </Typography>
      <Create resource="reviews" record={{ placeId }} redirect={false}>
        <ReviewFormFields />
      </Create>
    </Box>
  );
}
