CREATE TABLE grid
(
    id        SERIAL PRIMARY KEY, -- Unique identifier for each grid
    x_size    INTEGER NOT NULL,   -- Grid's width in cells
    y_size    INTEGER NOT NULL,   -- Grid's height in cells
    obstacles TEXT[] DEFAULT '{}' -- Array of x, y coordinates for obstacles e.g {'x,y', '1,1'...}
);

CREATE TABLE probe
(
    id           SERIAL PRIMARY KEY,    -- Unique identifier for each row
    probe_type   VARCHAR(255) NOT NULL, -- Identifier for each probe as in previous table
    grid_id      INTEGER      NOT NULL, -- Foreign key to connect to grid table
    x_coordinate INTEGER      NOT NULL, -- X-coordinate of the probe within the grid
    y_coordinate INTEGER      NOT NULL, -- Y-coordinate of the probe within the grid
    direction    VARCHAR(50)  NOT NULL,-- Current direction of the probe (e.g., 'North', 'South', 'East', 'West')
    CONSTRAINT fk_probe_grid FOREIGN KEY (grid_id) REFERENCES grid (id)
);

CREATE TABLE probe_visited_position
(
    id                SERIAL PRIMARY KEY,      -- Unique identifier for each row (auto-incremented)
    probe_id          INTEGER      NOT NULL,   -- Unique identifier for each probe
    username          VARCHAR(50)  NOT NULL,   -- Username associated with this probe movement
    x_coordinate      INTEGER      NOT NULL,   -- X-coordinate of the probe
    y_coordinate      INTEGER      NOT NULL,   -- Y-coordinate of the probe
    direction         VARCHAR(50)  NOT NULL,-- Current direction of the probe (e.g., 'North', 'South', 'East', 'West')
    command_executed  VARCHAR(255) NOT NULL,   -- Command issued to move to this position
    timestamp_visited TIMESTAMP DEFAULT NOW(), -- Time when the position is visited
    CONSTRAINT fk_probe_id FOREIGN KEY (probe_id) REFERENCES probe (id)
);