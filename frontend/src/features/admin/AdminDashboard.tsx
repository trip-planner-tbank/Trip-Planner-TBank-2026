import {
  Card,
  CardContent,
  Grid,
  Stack,
  Typography,
} from "@mui/material";
import { useEffect, useState } from "react";
import LocationCityIcon from "@mui/icons-material/LocationCity";
import BusinessIcon from "@mui/icons-material/Business";
import PlaceIcon from "@mui/icons-material/Place";
import RateReviewIcon from "@mui/icons-material/RateReview";
import HotelIcon from "@mui/icons-material/Hotel";

import { httpClient } from "../../shared/api/httpClient";
import { API_URL } from "../../shared/config/env";

const statConfig = [
  { key: "cities", label: "Cities", icon: LocationCityIcon, color: "#3f51b5" },
  { key: "offices", label: "Offices", icon: BusinessIcon, color: "#009688" },
  { key: "places", label: "Places", icon: PlaceIcon, color: "#ff9800" },
  { key: "reviews", label: "Reviews", icon: RateReviewIcon, color: "#e91e63" },
  { key: "hotels", label: "Hotels", icon: HotelIcon, color: "#795548" },
];

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
        const [citiesRes, officesRes, reviewsRes, placesRes, typesRes] =
          await Promise.all([
            httpClient(`${API_URL}/cities`),
            httpClient(`${API_URL}/offices`),
            httpClient(`${API_URL}/reviews`),
            httpClient(`${API_URL}/places`),
            httpClient(`${API_URL}/place-types`),
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
        const hotelType = Array.isArray(typesRes.json)
          ? (typesRes.json as { id: number; code: string }[]).find(
              (type) => type.code === "HOTEL",
            )
          : undefined;

        setCounts({
          cities: cities.length,
          offices: offices.length,
          places: places.length,
          reviews: reviews.length,
          hotels: hotelType
            ? places.filter((place) => place.placeTypeId === hotelType.id).length
            : 0,
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
          {statConfig.map((stat) => {
            const Icon = stat.icon;
            const value = counts[stat.key as keyof typeof counts];
            return (
              <Grid key={stat.key} size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
                <Card variant="outlined" sx={{ borderRadius: 2, height: "100%" }}>
                  <CardContent>
                    <Stack
                      direction="row"
                      spacing={2}
                      alignItems="center"
                    >
                      <Icon sx={{ fontSize: 40, color: stat.color }} />
                      <Stack>
                        <Typography color="text.secondary" variant="body2">
                          {stat.label}
                        </Typography>
                        <Typography variant="h4">{value}</Typography>
                      </Stack>
                    </Stack>
                  </CardContent>
                </Card>
              </Grid>
            );
          })}
        </Grid>
      </CardContent>
    </Card>
  );
}
