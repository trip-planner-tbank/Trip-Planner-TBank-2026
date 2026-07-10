import {
  Button,
  Card,
  CardActions,
  CardContent,
  Stack,
  Typography,
} from "@mui/material";

import { Place } from "../../shared/types";
import { AddToWishlistButton } from "../wishlist/AddToWishlistButton";

interface NearbyPlaceCardProps {
  place: Place;
  placeTypeName?: string;
  onDetails: () => void;
}

export function NearbyPlaceCard({
  place,
  placeTypeName,
  onDetails,
}: NearbyPlaceCardProps) {
  return (
    <Card variant="outlined">
      <CardContent>
        <Typography variant="h6" component="h3">
          {place.name}
        </Typography>
        {placeTypeName && (
          <Typography variant="body2" color="text.secondary">
            {placeTypeName}
          </Typography>
        )}
        <Typography variant="body2">{place.address}</Typography>
        <Stack direction="row" spacing={2} sx={{ mt: 1 }}>
          {place.distanceKm !== undefined && (
            <Typography variant="body2">
              Distance: {place.distanceKm.toFixed(2)} km
            </Typography>
          )}
          <Typography variant="body2">
            Rating: {place.avgRating.toFixed(1)}
          </Typography>
        </Stack>
      </CardContent>
      <CardActions>
        <Button size="small" onClick={onDetails}>
          Details
        </Button>
        <AddToWishlistButton placeId={place.id} size="small" />
      </CardActions>
    </Card>
  );
}
