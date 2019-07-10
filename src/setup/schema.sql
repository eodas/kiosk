-- Database Schema
-- Executive Order Corporation
-- Copyright - 1978, 2019: Executive Order Corporation, All Rights Reserved
--
-- This SQL script defines the tables, triggers and
-- views of the database. Sets the constraints, relationships
-- and rules of the database.
-- ---------------------------------------------------------------------------

PRAGMA foreign_keys=ON;

-- Reset:
-- Drops all the tables, triggers and views in the database
-- to prepare for their recreation.
-- ------------------------------------------------------------

DROP TABLE   IF EXISTS User;
DROP TABLE   IF EXISTS Vehicle;
DROP TABLE   IF EXISTS Permit;
DROP TABLE   IF EXISTS Auto;
DROP TABLE   IF EXISTS AutoMaker;
DROP TABLE   IF EXISTS Insurer;
DROP VIEW    IF EXISTS PPKPermit;
DROP TRIGGER IF EXISTS outstandingFines;
DROP TRIGGER IF EXISTS insertVehicle;
DROP TRIGGER IF EXISTS insertPermit;
DROP TRIGGER IF EXISTS insertPPKPermit;


-- Table Creation
-- ------------------------------------------------------------

CREATE TABLE Insurer (
    name    VARCHAR(256)    NOT NULL,
    PRIMARY KEY(name ASC)
);

CREATE TABLE AutoMaker (
    name    VARCHAR(256)    NOT NULL,
    PRIMARY KEY(name ASC)
);

CREATE TABLE Auto (
    make    VARCHAR(256)    NOT NULL,
    model   VARCHAR(256)    NOT NULL,
    PRIMARY KEY(make ASC, model ASC),
    FOREIGN KEY(make)
        REFERENCES AutoMaker(name)
);

CREATE TABLE User (
    uid     INT             NOT NULL,
    pin     INT             NOT NULL,
    sname   VARCHAR(256),
    fname   VARCHAR(256),
    fines   DECIMAL(5, 2)   NOT NULL,   -- from elsewhere and unpaid permits
    email   VARCHAR(256),
    lactive VARCHAR(256),               -- last active datetime
    PRIMARY KEY(uid ASC),
    CONSTRAINT uid_range
        CHECK (uid > 0 AND uid < 9999),
    CONSTRAINT pin_range
        CHECK (pin >= 0 AND pin <= 9999),
    CONSTRAINT nneg_fines
        CHECK (fines >= 0)
);

CREATE TABLE Vehicle (
    plate   VARCHAR(10)     NOT NULL,   -- maybe alphanumeric
    owner   INT          	NOT NULL,
    make    VARCHAR(256)    NOT NULL,
    model   VARCHAR(256)    NOT NULL,
    year    INT,                        -- car model year
    -- insurance
    insurer VARCHAR(256)    NOT NULL,
    policy  VARCHAR(256)    NOT NULL,   -- maybe alphanumeric
    expiry  VARCHAR(256)    NOT NULL,   -- insurance expiry date
    PRIMARY KEY(plate ASC),
    FOREIGN KEY(owner)
        REFERENCES User(uid)
        ON DELETE CASCADE,              -- user deleted, no vehicle
    FOREIGN KEY(make, model)
        REFERENCES Auto,
    FOREIGN KEY(insurer)
        REFERENCES Insurer(name),
    CONSTRAINT year_range               -- 1900 to now
        CHECK (year >= 1900 AND
               year <= CAST(STRFTIME('%Y', 'now') AS INT))
);

CREATE TABLE Permit (
    vehicle VARCHAR(10)     NOT NULL,
    start   VARCHAR(256)    NOT NULL,   -- start date of permit
    expiry  VARCHAR(256)    NOT NULL,   -- expiry date of permit
    issued  VARCHAR(256)    NOT NULL,   -- permit issued date and time
    PRIMARY KEY(vehicle ASC, start DESC),
    FOREIGN KEY(vehicle)
        REFERENCES Vehicle(plate)
        ON DELETE CASCADE               -- no vehicle, no permit
        ON UPDATE CASCADE,              -- changed license plate
    CONSTRAINT permit_range             -- ensure expiry after start
        CHECK (start < expiry)
    CONSTRAINT permit_issued            -- permit issued before or on start date
        CHECK (start >= DATE(issued))
);


-- Views
-- ------------------------------------------------------------

-- View for convenient inserting new permits into
-- the database. Aggregates the owner, vehicle,
-- permit start date, permit issued date-time and
-- permit duration (in days).
CREATE VIEW PPKPermit AS
    SELECT V.owner, B.vehicle, B.start, B.issued, B.days
    FROM (
        SELECT P.vehicle, P.start, P.issued,
            CAST((JULIANDAY(P.expiry) - JULIANDAY(P.start)) AS INT) AS days
        FROM Permit P
    ) AS B, Vehicle V
    WHERE B.vehicle = V.plate
    ORDER BY V.owner, B.vehicle, B.start;


-- Triggers
-- ------------------------------------------------------------

-- Conditional triggers to add fines to user when
-- user has outstanding fines. If the case, abort.
CREATE TRIGGER outstandingFines
    UPDATE ON User
    BEGIN
        SELECT CASE WHEN (OLD.fines < NEW.fines AND OLD.fines > 0)
        THEN RAISE(ABORT, "outstanding fines") END;
    END;

-- Trigger when insert a vehicle into Vehicle.
-- Check for and abort when insurance policy expiry date
-- is on or before today.
CREATE TRIGGER insertVehicle
    BEFORE INSERT ON Vehicle
    BEGIN
        SELECT CASE WHEN (NEW.expiry <= DATE('now'))
        THEN RAISE(ABORT, "vehicle insurance expired") END;
    END;

-- Trigger when insert of permit into Permit.
-- Check for and abort when there are outstanding fines,
-- overlapping permits, or the insurance policy expires
-- before permit expires.
CREATE TRIGGER insertPermit
    BEFORE INSERT ON Permit
    BEGIN
        SELECT CASE WHEN ((
            SELECT U.fines
            FROM User U, Vehicle V
            WHERE NEW.vehicle = V.plate AND V.owner = U.uid
        ) > 0)
        THEN RAISE(ABORT, "outstanding fines") END;
        SELECT CASE WHEN ((
            SELECT P.expiry
            FROM Permit P
            WHERE NEW.vehicle = P.vehicle AND (
                    (NEW.start >= P.start AND NEW.start <= P.expiry) OR         -- P.start <= NEW.start <= P.expiry
                    (NEW.expiry >= P.start AND NEW.expiry <= P.expiry) OR       -- P.start <= NEW.expiry <= P.expiry
                    (NEW.start <= P.start AND NEW.expiry >= P.expiry)           -- NEW.start <= P.start < P.expiry <= NEW.expiry
                )
        ) NOTNULL)
        THEN RAISE(ABORT, "permit overlaps") END;
        SELECT CASE WHEN ((
            SELECT V.expiry
            FROM Vehicle V
            WHERE NEW.vehicle = V.plate AND NEW.expiry > V.expiry
        ) NOTNULL)
        THEN RAISE(ABORT, "insurance expires before permit") END;
    END;

-- Trigger when insert of permit into PPKPermit.
-- Convert days into the expiry date of the permit.
-- Insert permit into Permit. Set issued datetime as now.
CREATE TRIGGER insertPPKPermit
    INSTEAD OF INSERT ON PPKPermit
    BEGIN
        INSERT INTO Permit(vehicle, start, expiry, issued) VALUES
            (NEW.vehicle, NEW.start, DATE(JULIANDAY(NEW.start) + NEW.days), DATETIME('now'));
    END;
