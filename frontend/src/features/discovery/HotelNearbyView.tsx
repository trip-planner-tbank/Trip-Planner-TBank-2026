import { useEffect, useMemo, useState } from "react";
import { Box, Button, Grid, Paper, Stack, Typography } from "@mui/material";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import HotelIcon from "@mui/icons-material/Hotel";

import { Place, PlaceType } from "../../shared/types";
import { fetchPlaceNearbyPlaces } from "./discoveryApi";
import { PlaceFilters } from "./PlaceFilters";
import { NearbyPlaceCard } from "./NearbyPlaceCard";

interface HotelNearbyViewProps {
  hotel: Place;
  placeTypes: PlaceType[];
  onBack: () => void;
}

export function HotelNearbyView({
  hotel,
  placeTypes,
  onBack,
}: HotelNearbyViewProps) {
  const [selectedPlaceTypeId, setSelectedPlaceTypeId] = useState<number | null>(
    null,
  );
  const [selectedRadiusKm, setSelectedRadiusKm] = useState<number | null>(null);
  const [places, setPlaces] = useState<Place[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const placeTypeById = useMemo(() => {
    const map = new Map<number, PlaceType>();
    placeTypes.forEach((placeType) => map.set(placeType.id, placeType));
    return map;
  }, [placeTypes]);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError(null);

    fetchPlaceNearbyPlaces(hotel.id, selectedPlaceTypeId, selectedRadiusKm)
      .then((results) => {
        if (!cancelled) setPlaces(results);
      })
      .catch((err: unknown) => {
        if (!cancelled) {
          setError(
            err instanceof Error ? err.message : "Failed to load nearby places",
          );
        }
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [hotel.id, selectedPlaceTypeId, selectedRadiusKm]);

  return (
    <Box>
      <Button startIcon={<ArrowBackIcon />} onClick={onBack} sx={{ mb: 2 }}>
        Back to discovery
      </Button>

      <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 2 }}>
        <HotelIcon color="primary" fontSize="large" />
        <Typography variant="h5">Places near {hotel.name}</Typography>
      </Stack>

      <Paper variant="outlined" sx={{ p: 3, borderRadius: 2, mb: 3 }}>
        <Grid container spacing={2}>
          <Grid size={{ xs: 12 }}>
            <PlaceFilters
              placeTypes={placeTypes}
              selectedPlaceTypeId={selectedPlaceTypeId}
              onPlaceTypeChange={setSelectedPlaceTypeId}
              selectedRadiusKm={selectedRadiusKm}
              onRadiusChange={setSelectedRadiusKm}
            />
          </Grid>
        </Grid>
      </Paper>

      {loading && <Typography sx={{ mt: 2 }}>Loading…</Typography>}
      {error && (
        <Typography color="error" sx={{ mt: 2 }}>
          {error}
        </Typography>
      )}

      {!loading && places.length === 0 && (
        <Typography color="text.secondary">
          No nearby places found for the selected filters.
        </Typography>
      )}

      <Stack spacing={2}>
        {places.map((place) => {
          const placeType = placeTypeById.get(place.placeTypeId);
          return (
            <NearbyPlaceCard
              key={place.id}
              place={place}
              placeTypeName={placeType?.name}
              onDetails={() => {}}
            />
          );
        })}
      </Stack>
    </Box>
  );
}
