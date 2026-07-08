CREATE TABLE IF NOT EXISTS place_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE
);

INSERT INTO place_types (name, code) VALUES
    ('Hotel', 'HOTEL'),
    ('Cafe', 'CAFE'),
    ('Restaurant', 'RESTAURANT'),
    ('Park', 'PARK'),
    ('Museum', 'MUSEUM')
ON CONFLICT (code) DO NOTHING;

CREATE TABLE IF NOT EXISTS cities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    country VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS offices (
    id BIGSERIAL PRIMARY KEY,
    city_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS places (
    id BIGSERIAL PRIMARY KEY,
    city_id BIGINT NOT NULL,
    place_type_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    description VARCHAR(1000),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    avg_rating DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- Business logic tables.
CREATE TABLE IF NOT EXISTS hotel_details (
    id BIGSERIAL PRIMARY KEY,
    place_id BIGINT NOT NULL UNIQUE,
    star_rating INTEGER NOT NULL,
    phone VARCHAR(50),
    website VARCHAR(255),
    room_count INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS reviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    place_id BIGINT NOT NULL,
    rating INTEGER NOT NULL,
    comment VARCHAR(1000),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_reviews_user_place UNIQUE (user_id, place_id)
);

CREATE TABLE IF NOT EXISTS wishlists (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    place_id BIGINT NOT NULL,
    added_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_wishlists_user_place UNIQUE (user_id, place_id)
);
