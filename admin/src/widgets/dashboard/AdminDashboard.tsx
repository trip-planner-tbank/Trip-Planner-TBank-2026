import {
  Card,
  CardContent,
  Grid,
  Typography,
} from "@mui/material";
import { useEffect, useState } from "react";

import { httpClient } from "../../shared/api/httpClient";
import { API_URL } from "../../shared/config/env";

export function AdminDashboard() {
  const [counts, setCounts] = useState({
    bookings: 0,
    reviews: 0,
    hotels: 0,
  });

  useEffect(() => {
    async function load() {
      try {
        const [bookingsRes, reviewsRes, placesRes] = await Promise.all([
          httpClient(`${API_URL}/bookings`),
          httpClient(`${API_URL}/reviews`),
          httpClient(`${API_URL}/places`),
        ]);

        const bookings = Array.isArray((bookingsRes.json as any).content)
          ? ((bookingsRes.json as any).content as unknown[])
          : Array.isArray(bookingsRes.json)
            ? (bookingsRes.json as unknown[])
            : [];
        const reviews = Array.isArray((reviewsRes.json as any).content)
          ? ((reviewsRes.json as any).content as unknown[])
          : Array.isArray(reviewsRes.json)
            ? (reviewsRes.json as unknown[])
            : [];
        const places = Array.isArray((placesRes.json as any).content)
          ? ((placesRes.json as any).content as { placeTypeId: number }[])
          : Array.isArray(placesRes.json)
            ? (placesRes.json as { placeTypeId: number }[])
            : [];

        setCounts({
          bookings: bookings.length,
          reviews: reviews.length,
          hotels: places.filter((p) => p.placeTypeId === 1).length,
        });
      } catch {
        // ignore
      }
    }
    void load();
  }, []);

  return (
    <Card>
      <CardContent>
        <Typography component="h1" variant="h5" gutterBottom>
          Trip Planner Admin
        </Typography>
        <Typography color="text.secondary" gutterBottom>
          Manage bookings, reviews, and hotels from one workspace.
        </Typography>

        <Grid container spacing={2} sx={{ mt: 2 }}>
          <Grid size={{ xs: 12, md: 4 }}>
            <Card variant="outlined">
              <CardContent>
                <Typography color="text.secondary">Bookings</Typography>
                <Typography variant="h4">{counts.bookings}</Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid size={{ xs: 12, md: 4 }}>
            <Card variant="outlined">
              <CardContent>
                <Typography color="text.secondary">Reviews</Typography>
                <Typography variant="h4">{counts.reviews}</Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid size={{ xs: 12, md: 4 }}>
            <Card variant="outlined">
              <CardContent>
                <Typography color="text.secondary">Hotels</Typography>
                <Typography variant="h4">{counts.hotels}</Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
}
