INSERT INTO place_types (name, code) VALUES
    ('Hotel', 'HOTEL'),
    ('Cafe', 'CAFE'),
    ('Restaurant', 'RESTAURANT'),
    ('Park', 'PARK'),
    ('Museum', 'MUSEUM'),
    ('Attraction', 'ATTRACTION'),
    ('Teambuilding', 'TEAMBUILDING'),
    ('Walk', 'WALK')
ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name;
