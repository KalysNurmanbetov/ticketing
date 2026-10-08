CREATE TABLE venues
(
    id   UUID PRIMARY KEY,
    name TEXT NOT NULL CHECK ( length(name) <= 200 )
);
CREATE UNIQUE INDEX venues_name_key ON venues (lower(venues.name));

