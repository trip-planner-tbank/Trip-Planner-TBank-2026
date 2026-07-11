import {
  Button,
  Card,
  CardActions,
  CardContent,
  Chip,
  Stack,
  Typography,
} from "@mui/material";
import LocationOnIcon from "@mui/icons-material/LocationOn";
import StarIcon from "@mui/icons-material/Star";

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
    <Card variant="outlined" sx={{ borderRadius: 2 }}>
      <CardContent>
        <Stack
          direction={{ xs: "column", sm: "row" }}
          justifyContent="space-between"
          alignItems={{ xs: "flex-start", sm: "center" }}
          spacing={1}
          sx={{ mb: 1 }}
        >
          <Typography variant="h6" component="h3">
            {place.name}
          </Typography>
          <Stack direction="row" spacing={1}>
            {placeTypeName && (
              <Chip label={placeTypeName} size="small" variant="outlined" />
            )}
            <Chip
              icon={<StarIcon fontSize="small" />}
              label={place.avgRating.toFixed(1)}
              size="small"
              color="warning"
            />
            {place.distanceKm !== undefined && (
              <Chip
                icon={<LocationOnIcon fontSize="small" />}
                label={`${place.distanceKm.toFixed(2)} km`}
                size="small"
                color="info"
              />
            )}
          </Stack>
        </Stack>

        <Typography variant="body2" color="text.secondary">
          {place.address}
        </Typography>
        {place.description && (
          <Typography
            variant="body2"
            color="text.secondary"
            sx={{ mt: 1 }}
            noWrap
          >
            {place.description}
          </Typography>
        )}
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
