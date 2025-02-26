CREATE TABLE probe_visited_position
(
    id                SERIAL PRIMARY KEY,      -- Unique identifier for each row (auto-incremented)
    probe_id VARCHAR(255) UNIQUE NOT NULL,     -- Unique identifier for each probe
    username          VARCHAR(50) NOT NULL,    -- Username associated with this probe movement
    x_coordinate      INTEGER     NOT NULL,    -- X-coordinate of the probe
    y_coordinate      INTEGER     NOT NULL,    -- Y-coordinate of the probe
    command_executed  VARCHAR(255) NOT NULL,   -- Command issued to move to this position
    timestamp_visited TIMESTAMP DEFAULT NOW()  -- Time when the position is visited
);
