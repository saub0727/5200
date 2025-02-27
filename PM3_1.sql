-- Make sure the database exists
CREATE SCHEMA IF NOT EXISTS CarGenieDB;
USE CarGenieDB;

-- Include DROP TABLE IF EXISTS statements at the top so the relational model can be easily recreated
DROP TABLE IF EXISTS Images;
DROP TABLE IF EXISTS Locations;
DROP TABLE IF EXISTS VehicleSpecs;
DROP TABLE IF EXISTS VehicleConditions;
DROP TABLE IF EXISTS VehicleClassification;
DROP TABLE IF EXISTS Vehicles;
DROP TABLE IF EXISTS Models;
DROP TABLE IF EXISTS Manufacturers;
DROP TABLE IF EXISTS Regions;
DROP TABLE IF EXISTS States;
DROP TABLE IF EXISTS temp_vehicles;

CREATE TABLE States (
                        StateId INT AUTO_INCREMENT,
                        StateName VARCHAR(5) UNIQUE,
                        CONSTRAINT pk_States_StateId PRIMARY KEY (StateId)
);

CREATE TABLE Regions (
                         RegionId INT AUTO_INCREMENT,
                         RegionName VARCHAR(50),
                         StateId INT NOT NULL,
                         CONSTRAINT pk_Regions_RegionId PRIMARY KEY (RegionId),
                         CONSTRAINT uq_Regions_RegionName_StateId UNIQUE (RegionName , StateId),
                         CONSTRAINT fk_Regions_StateId FOREIGN KEY (StateId)
                             REFERENCES States (StateId)
                             ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Manufacturers (
                               ManufacturerId INT AUTO_INCREMENT,
                               ManufacturerName VARCHAR(50) UNIQUE,
                               CONSTRAINT pk_Manufacturers_ManufacturerId PRIMARY KEY (ManufacturerId)
);

CREATE TABLE Models (
                        ModelId INT AUTO_INCREMENT,
                        ModelName VARCHAR(255),
                        ManufacturerId INT,
                        CONSTRAINT pk_Models_ModelId PRIMARY KEY (ModelId),
                        CONSTRAINT fk_Models_ManufacturerId FOREIGN KEY (ManufacturerId)
                            REFERENCES Manufacturers (ManufacturerId)
                            ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE Vehicles (
                          VehicleId BIGINT UNIQUE,
                          Vin VARCHAR(50) UNIQUE,
                          Price INT,
                          PostingDate DATE,
                          Description TEXT,
                          ModelId INT,
                          CONSTRAINT pk_Vehicles_VehicleId PRIMARY KEY (VehicleId),
                          CONSTRAINT fk_Vehicles_ModelId FOREIGN KEY (ModelId)
                              REFERENCES Models (ModelId)
                              ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE VehicleSpecs (
                              VehicleId BIGINT,
                              Cylinders VARCHAR(50),
                              Fuel ENUM('GAS', 'DIESEL', 'ELECTRIC', 'HYBRID', 'OTHER'),
                              Transmission ENUM('AUTOMATIC', 'MANUAL', 'OTHER'),
                              Drive ENUM('FWD', 'RWD', '4WD'),
                              CONSTRAINT pk_VehicleSpecs_VehicleId PRIMARY KEY (VehicleId),
                              CONSTRAINT fk_VehicleSpecs_VehicleId FOREIGN KEY (VehicleId)
                                  REFERENCES Vehicles (VehicleId)
                                  ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE VehicleConditions (
                                   VehicleId BIGINT,
                                   Odometer INT,
                                   VehicleCondition ENUM('EXCELLENT', 'FAIR', 'GOOD', 'LIKE_NEW', 'NEW', 'SALVAGE'),
                                   TitleStatus ENUM('CLEAN', 'LIEN', 'MISSING', 'PARTS_ONLY', 'REBUILT', 'SALVAGE'),
                                   CONSTRAINT pk_VehicleConditions_VehicleId PRIMARY KEY (VehicleId),
                                   CONSTRAINT fk_VehicleConditions_VehicleId FOREIGN KEY (VehicleId)
                                       REFERENCES Vehicles (VehicleId)
                                       ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE VehicleClassification (
                                       VehicleId BIGINT,
                                       Year INT,
                                       Size ENUM('COMPACT', 'MID-SIZE', 'FULL-SIZE', 'SUB-COMPACT'),
                                       Type ENUM('BUS', 'CONVERTIBLE', 'COUPE', 'HATCHBACK', 'MINI-VAN', 'OFFROAD', 'OTHER', 'PICKUP', 'SEDAN', 'SUV', 'TRUCK', 'VAN', 'WAGON'),
                                       Color ENUM('BLACK', 'BLUE', 'BROWN', 'CUSTOM', 'GREEN', 'GREY', 'ORANGE', 'PURPLE', 'RED', 'SILVER', 'WHITE', 'YELLOW'),
                                       CONSTRAINT pk_VehicleClassification_VehicleId PRIMARY KEY (VehicleId),
                                       CONSTRAINT fk_VehicleClassification_VehicleId FOREIGN KEY (VehicleId)
                                           REFERENCES Vehicles (VehicleId)
                                           ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Images (
                        ImageId INT AUTO_INCREMENT,
                        VehicleId BIGINT NOT NULL,
                        ImageURL VARCHAR(255),
                        CONSTRAINT pk_Images_ImageId PRIMARY KEY (ImageId),
                        CONSTRAINT fk_Images_VehicleId FOREIGN KEY (VehicleId)
                            REFERENCES Vehicles (VehicleId)
                            ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Locations (
                           VehicleId BIGINT,
                           Latitude FLOAT,
                           Longitude FLOAT,
                           RegionId INT,
                           CONSTRAINT pk_Locations_VehicleId PRIMARY KEY (VehicleId),
                           CONSTRAINT fk_Locations_VehicleId FOREIGN KEY (VehicleId)
                               REFERENCES Vehicles (VehicleId)
                               ON UPDATE CASCADE ON DELETE CASCADE,
                           CONSTRAINT fk_Locations_RegionId FOREIGN KEY (RegionId)
                               REFERENCES Regions (RegionId)
                               ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE temp_vehicles (
                               id BIGINT,
                               url TEXT,
                               region VARCHAR(100),
                               region_url TEXT,
                               price INT,
                               year INT,
                               manufacturer VARCHAR(50),
                               model VARCHAR(255),
                               `condition` VARCHAR(50),
                               cylinders VARCHAR(50),
                               fuel VARCHAR(50),
                               odometer INT,
                               title_status VARCHAR(50),
                               transmission VARCHAR(50),
                               VIN VARCHAR(50),
                               drive VARCHAR(10),
                               size VARCHAR(50),
                               type VARCHAR(50),
                               paint_color VARCHAR(50),
                               image_url TEXT,
                               description TEXT,
                               county VARCHAR(100),
                               state VARCHAR(5),
                               lat FLOAT,
                               `long` FLOAT,
                               posting_date DATE
);