import type { ResourceProps } from "react-admin";

import { ReviewCreate } from "./ReviewCreate";
import { ReviewList } from "./ReviewList";

export { MyReviewForm } from "./MyReviewForm";
export { ReviewCreate } from "./ReviewCreate";
export { ReviewList } from "./ReviewList";

export const reviewsResource: ResourceProps = {
  name: "reviews",
  list: ReviewList,
  create: ReviewCreate,
};
