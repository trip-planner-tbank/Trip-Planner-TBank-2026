import type { ResourceProps } from "react-admin";
import RateReviewIcon from "@mui/icons-material/RateReview";

import { ReviewCreate } from "./ReviewCreate";
import { ReviewEdit } from "./ReviewEdit";
import { ReviewList } from "./ReviewList";

export { MyReviewForm } from "./MyReviewForm";
export { ReviewCreate } from "./ReviewCreate";
export { ReviewEdit } from "./ReviewEdit";
export { ReviewList } from "./ReviewList";

export const reviewsResource: ResourceProps = {
  name: "reviews",
  list: ReviewList,
  create: ReviewCreate,
  edit: ReviewEdit,
  icon: RateReviewIcon,
};
