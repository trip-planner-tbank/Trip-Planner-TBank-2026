import { useEffect, useMemo, useState } from "react";
import { Box, Button, Stack, Typography } from "@mui/material";

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
          setError(err instanceof Error ? err.message : "Failed to load nearby places");
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
      <Button onClick={onBack} sx={{ mb: 2 }}>
        Back to discovery
      </Button>
      <Typography variant="h5" gutterBottom>
        Places near {hotel.name}
      </Typography>
      <PlaceFilters
        placeTypes={placeTypes}
        selectedPlaceTypeId={selectedPlaceTypeId}
        onPlaceTypeChange={setSelectedPlaceTypeId}
        selectedRadiusKm={selectedRadiusKm}
        onRadiusChange={setSelectedRadiusKm}
      />
      {loading && <Typography sx={{ mt: 2 }}>Loading...</Typography>}
      {error && (
        <Typography color="error" sx={{ mt: 2 }}>
          {error}
        </Typography>
      )}
      <Stack spacing={2} sx={{ mt: 2 }}>
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
