import { useCallback, useEffect, useState } from "react";
import { Button, ButtonProps, CircularProgress } from "@mui/material";
import FavoriteIcon from "@mui/icons-material/Favorite";
import FavoriteBorderIcon from "@mui/icons-material/FavoriteBorder";

import { httpClient } from "../../shared/api/httpClient";
import { API_URL } from "../../shared/config/env";
import type { WishlistEntry } from "../../shared/types";

interface AddToWishlistButtonProps extends Omit<ButtonProps, "onClick" | "disabled" | "startIcon"> {
  placeId: number;
}

export function AddToWishlistButton({ placeId, ...buttonProps }: AddToWishlistButtonProps) {
  const [entry, setEntry] = useState<WishlistEntry | null>(null);
  const [loading, setLoading] = useState(false);
  const [busy, setBusy] = useState(false);

  const checkWishlist = useCallback(async () => {
    setLoading(true);
    try {
      const { json } = await httpClient(`${API_URL}/wishlists`);
      const entries = json as WishlistEntry[];
      const found = entries.find((item) => item.placeId === placeId) ?? null;
      setEntry(found);
    } catch (error) {
      console.error("Failed to load wishlist", error);
    } finally {
      setLoading(false);
    }
  }, [placeId]);

  useEffect(() => {
    checkWishlist();
  }, [checkWishlist]);

  const handleToggle = async () => {
    setBusy(true);
    try {
      if (entry) {
        await httpClient(`${API_URL}/wishlists/${entry.id}`, {
          method: "DELETE",
        });
        setEntry(null);
      } else {
        const { json } = await httpClient(`${API_URL}/wishlists`, {
          method: "POST",
          body: JSON.stringify({ placeId }),
        });
        setEntry(json as WishlistEntry);
      }
    } catch (error) {
      console.error("Failed to update wishlist", error);
    } finally {
      setBusy(false);
    }
  };

  const inWishlist = entry !== null;

  return (
    <Button
      {...buttonProps}
      variant={buttonProps.variant ?? "outlined"}
      color={inWishlist ? "error" : "primary"}
      startIcon={
        loading || busy ? (
          <CircularProgress size={18} />
        ) : inWishlist ? (
          <FavoriteIcon />
        ) : (
          <FavoriteBorderIcon />
        )
      }
      onClick={handleToggle}
      disabled={loading || busy}
    >
      {inWishlist ? "Remove from wishlist" : "Add to wishlist"}
    </Button>
  );
}
