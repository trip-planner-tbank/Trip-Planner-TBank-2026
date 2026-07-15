CREATE INDEX IF NOT EXISTS idx_offices_city_id
    ON offices (city_id);

CREATE INDEX IF NOT EXISTS idx_places_city_active
    ON places (city_id, is_active);

CREATE INDEX IF NOT EXISTS idx_places_type_active
    ON places (place_type_id, is_active);

CREATE INDEX IF NOT EXISTS idx_places_city_type_active
    ON places (city_id, place_type_id, is_active);

CREATE INDEX IF NOT EXISTS idx_reviews_place_id
    ON reviews (place_id);

CREATE INDEX IF NOT EXISTS idx_reviews_user_id
    ON reviews (user_id);

CREATE INDEX IF NOT EXISTS idx_wishlists_user_added_at
    ON wishlists (user_id, added_at DESC);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id
    ON refresh_tokens (user_id);
