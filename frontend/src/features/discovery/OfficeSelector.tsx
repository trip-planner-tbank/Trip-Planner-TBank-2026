import { FormControl, InputLabel, MenuItem, Select } from "@mui/material";
import type React from "react";

import { Office } from "../../shared/types";

interface OfficeSelectorProps {
  offices: Office[];
  selectedOffice: Office | null;
  onSelect: (office: Office | null) => void;
  disabled?: boolean;
  loading?: boolean;
}

export function OfficeSelector({
  offices,
  selectedOffice,
  onSelect,
  disabled,
  loading,
}: OfficeSelectorProps) {
  return (
    <FormControl fullWidth disabled={disabled || loading}>
      <InputLabel>Office</InputLabel>
      <Select
        value={selectedOffice?.id ?? ""}
        label="Office"
        SelectDisplayProps={{
          "data-testid": "office-select",
        } as React.HTMLAttributes<HTMLDivElement>}
        onChange={(event) => {
          const id = Number(event.target.value);
          onSelect(offices.find((office) => office.id === id) ?? null);
        }}
      >
        {offices.map((office) => (
          <MenuItem key={office.id} value={office.id}>
            {office.name} — {office.address}
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  );
}
