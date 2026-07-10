import { Typography } from "@mui/material";
import { useSearchParams } from "react-router-dom";

import { MyReviewForm } from "./MyReviewForm";

export function ReviewCreate() {
  const [searchParams] = useSearchParams();
  const placeId = Number(searchParams.get("placeId"));

  if (!placeId) {
    return (
      <Typography>
        Select a place to review.
      </Typography>
    );
  }

  return <MyReviewForm placeId={placeId} />;
}
