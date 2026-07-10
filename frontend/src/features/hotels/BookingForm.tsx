import { useState } from "react";
import { Box, Button, TextField, Typography } from "@mui/material";
import { useNotify, useRecordContext, useRedirect } from "react-admin";

import { httpClient } from "../../shared/api/httpClient";
import { API_URL } from "../../shared/config/env";
import type { CreateBookingRequest, Place } from "../../shared/types";

export function BookingForm() {
  const record = useRecordContext<Place>();
  const notify = useNotify();
  const redirect = useRedirect();

  const [checkIn, setCheckIn] = useState("");
  const [checkOut, setCheckOut] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  if (!record) {
    return <Typography>Loading…</Typography>;
  }

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setError(null);

    const checkInDate = new Date(checkIn);
    const checkOutDate = new Date(checkOut);

    if (Number.isNaN(checkInDate.getTime()) || Number.isNaN(checkOutDate.getTime())) {
      setError("Please select both check-in and check-out dates.");
      return;
    }

    if (checkOutDate <= checkInDate) {
      setError("Check-out date must be after check-in date.");
      return;
    }

    const payload: CreateBookingRequest = {
      placeId: record.id,
      checkIn: checkInDate.toISOString(),
      checkOut: checkOutDate.toISOString(),
    };

    setSubmitting(true);
    try {
      await httpClient(`${API_URL}/bookings`, {
        method: "POST",
        body: JSON.stringify(payload),
      });
      notify("Booking created successfully", { type: "success" });
      redirect("/bookings");
    } catch {
      notify("Failed to create booking", { type: "error" });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Box
      component="form"
      onSubmit={handleSubmit}
      sx={{ mt: 2, display: "flex", flexDirection: "column", gap: 2, maxWidth: 400 }}
    >
      <TextField
        label="Check-in"
        type="date"
        value={checkIn}
        onChange={(e) => setCheckIn(e.target.value)}
        slotProps={{ inputLabel: { shrink: true } }}
        required
      />
      <TextField
        label="Check-out"
        type="date"
        value={checkOut}
        onChange={(e) => setCheckOut(e.target.value)}
        slotProps={{ inputLabel: { shrink: true } }}
        required
      />
      {error && <Typography color="error">{error}</Typography>}
      <Button type="submit" variant="contained" disabled={submitting}>
        Book
      </Button>
    </Box>
  );
}
