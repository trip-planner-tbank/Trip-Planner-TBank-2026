import { useEffect, useState } from "react";
import {
  Box,
  Link,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import {
  Show,
  SimpleShowLayout,
  Tab,
  TabbedShowLayout,
  TextField,
  useRecordContext,
} from "react-admin";

import { httpClient } from "../../shared/api/httpClient";
import { API_URL } from "../../shared/config/env";
import type { HotelDetails, Place } from "../../shared/types";
import { BookingForm } from "./BookingForm";
import { ShowWishlistAction } from "../wishlist/ShowWishlistAction";

function HotelDetailsPanel() {
  const record = useRecordContext<Place>();
  const [details, setDetails] = useState<HotelDetails | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!record) return;
    setLoading(true);
    httpClient(`${API_URL}/places/${record.id}/hotel-details`)
      .then(({ json }) => {
        setDetails(json as HotelDetails);
      })
      .catch(() => {
        setDetails(null);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [record]);

  if (loading) return <Typography>Loading hotel details…</Typography>;
  if (!details) return null;

  return (
    <Box sx={{ mt: 2 }}>
      <Typography variant="subtitle1">Hotel details</Typography>
      <Typography>Star rating: {details.starRating}</Typography>
      <Typography>Rooms: {details.roomCount}</Typography>
      {details.phone && <Typography>Phone: {details.phone}</Typography>}
      {details.website && (
        <Typography>
          Website:{" "}
          <Link href={details.website} target="_blank" rel="noopener">
            {details.website}
          </Link>
        </Typography>
      )}
    </Box>
  );
}

function NearbyPlacesTab() {
  const record = useRecordContext<Place>();
  const [places, setPlaces] = useState<Place[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!record) return;
    setLoading(true);
    httpClient(`${API_URL}/places/${record.id}/nearby-places`)
      .then(({ json }) => {
        setPlaces((json as Place[]) ?? []);
      })
      .catch(() => {
        setPlaces([]);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [record]);

  if (loading) return <Typography>Loading nearby places…</Typography>;
  if (places.length === 0) return <Typography>No nearby places found.</Typography>;

  return (
    <TableContainer>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell>Address</TableCell>
            <TableCell>Distance (km)</TableCell>
            <TableCell>Rating</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {places.map((place) => (
            <TableRow key={place.id}>
              <TableCell>{place.name}</TableCell>
              <TableCell>{place.address}</TableCell>
              <TableCell>{place.distanceKm ?? "—"}</TableCell>
              <TableCell>{place.avgRating}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}

export function HotelShow() {
  return (
    <Show resource="places" actions={<ShowWishlistAction />}>
      <TabbedShowLayout>
        <Tab label="Details">
          <SimpleShowLayout>
            <TextField source="name" />
            <TextField source="address" />
            <TextField source="description" emptyText="—" />
            <TextField source="avgRating" />
            <TextField source="isActive" />
          </SimpleShowLayout>
          <HotelDetailsPanel />
        </Tab>
        <Tab label="Nearby places">
          <NearbyPlacesTab />
        </Tab>
        <Tab label="Book">
          <BookingForm />
        </Tab>
      </TabbedShowLayout>
    </Show>
  );
}
