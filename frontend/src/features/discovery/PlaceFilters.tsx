import { FormControl, InputLabel, MenuItem, Select, Stack } from "@mui/material";
import type React from "react";

import { PlaceType } from "../../shared/types";

interface PlaceFiltersProps {
  placeTypes: PlaceType[];
  selectedPlaceTypeId: number | null;
  onPlaceTypeChange: (placeTypeId: number | null) => void;
  selectedRadiusKm: number | null;
  onRadiusChange: (radiusKm: number | null) => void;
}

const RADIUS_OPTIONS: { value: number | null; label: string }[] = [
  { value: 0.5, label: "0.5 km" },
  { value: 1, label: "1 km" },
  { value: 2, label: "2 km" },
  { value: 5, label: "5 km" },
  { value: null, label: "Unlimited" },
];

const ALL_VALUE = "all";
const UNLIMITED_VALUE = "unlimited";

export function PlaceFilters({
  placeTypes,
  selectedPlaceTypeId,
  onPlaceTypeChange,
  selectedRadiusKm,
  onRadiusChange,
}: PlaceFiltersProps) {
  return (
    <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
      <FormControl fullWidth>
        <InputLabel>Place type</InputLabel>
        <Select
          value={selectedPlaceTypeId ?? ALL_VALUE}
          label="Place type"
          SelectDisplayProps={{
            "data-testid": "place-type-select",
          } as React.HTMLAttributes<HTMLDivElement>}
          onChange={(event) => {
            const value = event.target.value;
            onPlaceTypeChange(
              value === ALL_VALUE ? null : Number(value),
            );
          }}
        >
          <MenuItem value={ALL_VALUE}>All</MenuItem>
          {placeTypes.map((placeType) => (
            <MenuItem key={placeType.id} value={placeType.id}>
              {placeType.name}
            </MenuItem>
          ))}
        </Select>
      </FormControl>

      <FormControl fullWidth>
        <InputLabel>Radius</InputLabel>
        <Select
          value={selectedRadiusKm ?? UNLIMITED_VALUE}
          label="Radius"
          SelectDisplayProps={{
            "data-testid": "radius-select",
          } as React.HTMLAttributes<HTMLDivElement>}
          onChange={(event) => {
            const value = event.target.value;
            onRadiusChange(
              value === UNLIMITED_VALUE ? null : Number(value),
            );
          }}
        >
          {RADIUS_OPTIONS.map((option) => (
            <MenuItem
              key={option.label}
              value={option.value ?? UNLIMITED_VALUE}
            >
              {option.label}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </Stack>
  );
}
