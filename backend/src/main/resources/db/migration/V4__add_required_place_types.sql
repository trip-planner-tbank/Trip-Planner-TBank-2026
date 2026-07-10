INSERT INTO place_types (name, code) VALUES
    ('Attraction', 'ATTRACTION'),
    ('Teambuilding', 'TEAMBUILDING'),
    ('Walk', 'WALK')
ON CONFLICT (code) DO NOTHING;
