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
    cities: 0,
    offices: 0,
    places: 0,
    reviews: 0,
    hotels: 0,
  });

  useEffect(() => {
    async function load() {
      try {
        const [citiesRes, officesRes, reviewsRes, placesRes] = await Promise.all([
          httpClient(`${API_URL}/cities`),
          httpClient(`${API_URL}/offices`),
          httpClient(`${API_URL}/reviews`),
          httpClient(`${API_URL}/places`),
        ]);

        const cities = Array.isArray(citiesRes.json)
          ? (citiesRes.json as unknown[])
          : [];
        const offices = Array.isArray(officesRes.json)
          ? (officesRes.json as unknown[])
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
          cities: cities.length,
          offices: offices.length,
          places: places.length,
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
          Manage cities, offices, places, hotels, and reviews from one workspace.
        </Typography>

        <Grid container spacing={2} sx={{ mt: 2 }}>
          <Grid size={{ xs: 12, md: 4 }}>
            <Card variant="outlined">
              <CardContent>
                <Typography color="text.secondary">Cities</Typography>
                <Typography variant="h4">{counts.cities}</Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid size={{ xs: 12, md: 4 }}>
            <Card variant="outlined">
              <CardContent>
                <Typography color="text.secondary">Offices</Typography>
                <Typography variant="h4">{counts.offices}</Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid size={{ xs: 12, md: 4 }}>
            <Card variant="outlined">
              <CardContent>
                <Typography color="text.secondary">Places</Typography>
                <Typography variant="h4">{counts.places}</Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid size={{ xs: 12, md: 6 }}>
            <Card variant="outlined">
              <CardContent>
                <Typography color="text.secondary">Reviews</Typography>
                <Typography variant="h4">{counts.reviews}</Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid size={{ xs: 12, md: 6 }}>
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
