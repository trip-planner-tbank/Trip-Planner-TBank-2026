import { BookingList } from "./BookingList";
import { BookingEdit } from "./BookingEdit";

export const bookingsResource = {
  name: "bookings",
  list: BookingList,
  edit: BookingEdit,
};

export { BookingList, BookingEdit };
