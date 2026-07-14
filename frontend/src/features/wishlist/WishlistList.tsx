import { useEffect, useState } from "react";
import {
  Datagrid,
  DeleteButton,
  FunctionField,
  List,
  TextField,
} from "react-admin";

import { httpClient } from "../../shared/api/httpClient";
import { API_URL } from "../../shared/config/env";
import type { PlaceType, WishlistEntry } from "../../shared/types";

export function WishlistList() {
  const [placeTypes, setPlaceTypes] = useState<Record<number, string>>({});

  useEffect(() => {
    httpClient(`${API_URL}/place-types`)
      .then(({ json }) => {
        const types = (json as PlaceType[]).reduce<Record<number, string>>(
          (acc, placeType) => {
            acc[placeType.id] = placeType.name;
            return acc;
          },
          {},
        );
        setPlaceTypes(types);
      })
      .catch(() => {
        // Place type labels are optional enhancement; ignore failures.
      });
  }, []);

  return (
    <List>
      <Datagrid rowClick={false}>
        <TextField source="place.name" label="Place" />
        <FunctionField<WishlistEntry>
          label="Type"
          render={(record) =>
            placeTypes[record.place.placeTypeId] ?? record.place.placeTypeId
          }
        />
        <TextField source="place.avgRating" label="Rating" />
        <DeleteButton mutationMode="pessimistic" />
      </Datagrid>
    </List>
  );
}
