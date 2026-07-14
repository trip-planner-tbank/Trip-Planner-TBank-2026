import { useEffect, useMemo, useState } from "react";
import { Box, Grid, Paper, Stack, Typography } from "@mui/material";
import ExploreIcon from "@mui/icons-material/Explore";

import { City, Office, Place, PlaceType } from "../../shared/types";
import {
  fetchCities,
  fetchOffices,
  fetchOfficeNearbyPlaces,
  fetchPlaceTypes,
} from "./discoveryApi";
import { CitySelector } from "./CitySelector";
import { OfficeSelector } from "./OfficeSelector";
import { PlaceFilters } from "./PlaceFilters";
import { NearbyPlaceCard } from "./NearbyPlaceCard";
import { HotelNearbyView } from "./HotelNearbyView";

export function DiscoveryDashboard() {
  const [cities, setCities] = useState<City[]>([]);
  const [selectedCity, setSelectedCity] = useState<City | null>(null);
  const [offices, setOffices] = useState<Office[]>([]);
  const [selectedOffice, setSelectedOffice] = useState<Office | null>(null);
  const [placeTypes, setPlaceTypes] = useState<PlaceType[]>([]);
  const [selectedPlaceTypeId, setSelectedPlaceTypeId] = useState<number | null>(
    null,
  );
  const [selectedRadiusKm, setSelectedRadiusKm] = useState<number | null>(null);
  const [nearbyPlaces, setNearbyPlaces] = useState<Place[]>([]);
  const [loadingCities, setLoadingCities] = useState(false);
  const [loadingOffices, setLoadingOffices] = useState(false);
  const [loadingPlaces, setLoadingPlaces] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [viewHotel, setViewHotel] = useState<Place | null>(null);

  const placeTypeById = useMemo(() => {
    const map = new Map<number, PlaceType>();
    placeTypes.forEach((placeType) => map.set(placeType.id, placeType));
    return map;
  }, [placeTypes]);

  useEffect(() => {
    let cancelled = false;
    setLoadingCities(true);
    fetchCities()
      .then((results) => {
        if (!cancelled) setCities(results);
      })
      .catch((err: unknown) => {
        if (!cancelled) {
          setError(err instanceof Error ? err.message : "Failed to load cities");
        }
      })
      .finally(() => {
        if (!cancelled) setLoadingCities(false);
      });
    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    let cancelled = false;
    fetchPlaceTypes()
      .then((results) => {
        if (!cancelled) setPlaceTypes(results);
      })
      .catch((err: unknown) => {
        if (!cancelled) {
          setError(
            err instanceof Error ? err.message : "Failed to load place types",
          );
        }
      });
    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    if (!selectedCity) {
      setOffices([]);
      setSelectedOffice(null);
      return;
    }

    let cancelled = false;
    setLoadingOffices(true);
    fetchOffices(selectedCity.id)
      .then((results) => {
        if (!cancelled) setOffices(results);
      })
      .catch((err: unknown) => {
        if (!cancelled) {
          setError(
            err instanceof Error ? err.message : "Failed to load offices",
          );
        }
      })
      .finally(() => {
        if (!cancelled) setLoadingOffices(false);
      });

    return () => {
      cancelled = true;
    };
  }, [selectedCity]);

  useEffect(() => {
    if (!selectedOffice) {
      setNearbyPlaces([]);
      return;
    }

    let cancelled = false;
    setLoadingPlaces(true);
    setError(null);

    fetchOfficeNearbyPlaces(
      selectedOffice,
      selectedPlaceTypeId,
      selectedRadiusKm,
    )
      .then((results) => {
        if (!cancelled) setNearbyPlaces(results);
      })
      .catch((err: unknown) => {
        if (!cancelled) {
          setError(
            err instanceof Error ? err.message : "Failed to load nearby places",
          );
        }
      })
      .finally(() => {
        if (!cancelled) setLoadingPlaces(false);
      });

    return () => {
      cancelled = true;
    };
  }, [selectedOffice, selectedPlaceTypeId, selectedRadiusKm]);

  if (viewHotel) {
    return (
      <HotelNearbyView
        hotel={viewHotel}
        placeTypes={placeTypes}
        onBack={() => setViewHotel(null)}
      />
    );
  }

  const handleCitySelect = (city: City | null) => {
    setSelectedCity(city);
    setSelectedOffice(null);
    setNearbyPlaces([]);
  };

  const handleDetails = (place: Place) => {
    const placeType = placeTypeById.get(place.placeTypeId);
    if (placeType?.code === "HOTEL") {
      setViewHotel(place);
    }
  };

  return (
    <Box>
      <Stack direction="row" spacing={1} alignItems="center" sx={{ mb: 2 }}>
        <ExploreIcon color="primary" fontSize="large" />
        <Typography variant="h4">Discovery</Typography>
      </Stack>

      {error && (
        <Typography color="error" sx={{ mb: 2 }}>
          {error}
        </Typography>
      )}

      <Paper variant="outlined" sx={{ p: 3, borderRadius: 2, mb: 3 }}>
        <Grid container spacing={2}>
          <Grid size={{ xs: 12, md: 6 }}>
            <CitySelector
              cities={cities}
              selectedCity={selectedCity}
              onSelect={handleCitySelect}
              loading={loadingCities}
            />
          </Grid>
          <Grid size={{ xs: 12, md: 6 }}>
            <OfficeSelector
              offices={offices}
              selectedOffice={selectedOffice}
              onSelect={setSelectedOffice}
              disabled={!selectedCity || offices.length === 0}
              loading={loadingOffices}
            />
          </Grid>
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

      {selectedOffice && (
        <Typography variant="h6" gutterBottom>
          Places near {selectedOffice.name}
        </Typography>
      )}

      {loadingPlaces && <Typography>Loading nearby places…</Typography>}

      {!loadingPlaces && selectedOffice && nearbyPlaces.length === 0 && (
        <Typography color="text.secondary">
          No places found for the selected filters.
        </Typography>
      )}

      <Stack spacing={2}>
        {nearbyPlaces.map((place) => {
          const placeType = placeTypeById.get(place.placeTypeId);
          return (
            <NearbyPlaceCard
              key={place.id}
              place={place}
              placeTypeName={placeType?.name}
              onDetails={() => handleDetails(place)}
            />
          );
        })}
      </Stack>
    </Box>
  );
}
