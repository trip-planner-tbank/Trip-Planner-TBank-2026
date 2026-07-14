import { Autocomplete, TextField } from "@mui/material";

import { City } from "../../shared/types";

interface CitySelectorProps {
  cities: City[];
  selectedCity: City | null;
  onSelect: (city: City | null) => void;
  loading?: boolean;
}

export function CitySelector({
  cities,
  selectedCity,
  onSelect,
  loading,
}: CitySelectorProps) {
  return (
    <Autocomplete<City, false, false, false>
      options={cities}
      value={selectedCity}
      onChange={(_event, value) => onSelect(value)}
      getOptionLabel={(city) => `${city.name}, ${city.country}`}
      isOptionEqualToValue={(option, value) => option.id === value.id}
      loading={loading}
      renderInput={(params) => (
        <TextField {...params} label="City" placeholder="Select a city" />
      )}
    />
  );
}
