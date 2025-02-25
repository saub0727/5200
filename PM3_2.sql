USE CarGenieDB;
-- 从临时表导入数据到正式表
-- 1. States表
INSERT IGNORE INTO States (StateName)
SELECT DISTINCT state 
FROM temp_vehicles 
WHERE state IS NOT NULL;

-- 2. Regions表
INSERT IGNORE INTO Regions (RegionName, StateId)
SELECT DISTINCT tv.region, s.StateId
FROM temp_vehicles tv
JOIN States s ON tv.state = s.StateName
WHERE tv.region IS NOT NULL;

-- 3. Manufacturers表
INSERT IGNORE INTO Manufacturers (ManufacturerName)
SELECT DISTINCT manufacturer 
FROM temp_vehicles 
WHERE manufacturer IS NOT NULL;

-- 4. Models表
INSERT IGNORE INTO Models (ModelName, ManufacturerId)
SELECT DISTINCT tv.model, m.ManufacturerId
FROM temp_vehicles tv
JOIN Manufacturers m ON tv.manufacturer = m.ManufacturerName
WHERE tv.model IS NOT NULL;

-- 5. Vehicles表
INSERT IGNORE INTO Vehicles (VehicleId, Vin, Price, PostingDate, Description, ModelId)
SELECT 
    tv.id,
    tv.VIN,
    tv.price,
    tv.posting_date,
    tv.description,
    m.ModelId
FROM temp_vehicles tv
LEFT JOIN Models m ON tv.model = m.ModelName
LEFT JOIN Manufacturers mf ON tv.manufacturer = mf.ManufacturerName
WHERE m.ManufacturerId = mf.ManufacturerId;

-- 6. VehicleSpecs表
INSERT IGNORE INTO VehicleSpecs (VehicleId, Cylinders, Fuel, Transmission, Drive)
SELECT 
    id,
    cylinders,
    CASE 
        WHEN fuel LIKE '%gas%' THEN 'GAS'
        WHEN fuel LIKE '%diesel%' THEN 'DIESEL'
        WHEN fuel LIKE '%electric%' THEN 'ELECTRIC'
        WHEN fuel LIKE '%hybrid%' THEN 'HYBRID'
        ELSE 'OTHER'
    END,
    CASE 
        WHEN transmission LIKE '%automatic%' THEN 'AUTOMATIC'
        WHEN transmission LIKE '%manual%' THEN 'MANUAL'
        ELSE 'OTHER'
    END,
    CASE 
        WHEN drive = 'fwd' THEN 'FWD'
        WHEN drive = 'rwd' THEN 'RWD'
        WHEN drive = '4wd' THEN '4WD'
        ELSE NULL
    END
FROM temp_vehicles
WHERE id IN (SELECT VehicleId FROM Vehicles);

-- 7. VehicleConditions表
INSERT IGNORE INTO VehicleConditions (VehicleId, Odometer, VehicleCondition, TitleStatus)
SELECT 
    id,
    odometer,
    CASE 
        WHEN `condition` = 'excellent' THEN 'EXCELLENT'
        WHEN `condition` = 'fair' THEN 'FAIR'
        WHEN `condition` = 'good' THEN 'GOOD'
        WHEN `condition` = 'like new' THEN 'LIKE_NEW'
        WHEN `condition` = 'new' THEN 'NEW'
        WHEN `condition` = 'salvage' THEN 'SALVAGE'
        ELSE NULL
    END,
    CASE 
        WHEN title_status = 'clean' THEN 'CLEAN'
        WHEN title_status = 'lien' THEN 'LIEN'
        WHEN title_status = 'missing' THEN 'MISSING'
        WHEN title_status = 'parts only' THEN 'PARTS_ONLY'
        WHEN title_status = 'rebuilt' THEN 'REBUILT'
        WHEN title_status = 'salvage' THEN 'SALVAGE'
        ELSE NULL
    END
FROM temp_vehicles
WHERE id IN (SELECT VehicleId FROM Vehicles);

-- 8. VehicleClassification表
INSERT IGNORE INTO VehicleClassification (VehicleId, Year, Size, Type, Color)
SELECT 
    id,
    year,
    CASE 
        WHEN size = 'compact' THEN 'COMPACT'
        WHEN size = 'mid-size' THEN 'MID-SIZE'
        WHEN size = 'full-size' THEN 'FULL-SIZE'
        WHEN size = 'sub-compact' THEN 'SUB-COMPACT'
        ELSE NULL
    END,
    CASE 
        WHEN type = 'bus' THEN 'BUS'
        WHEN type = 'convertible' THEN 'CONVERTIBLE'
        WHEN type = 'coupe' THEN 'COUPE'
        WHEN type = 'hatchback' THEN 'HATCHBACK'
        WHEN type = 'mini-van' THEN 'MINI-VAN'
        WHEN type = 'offroad' THEN 'OFFROAD'
        WHEN type = 'pickup' THEN 'PICKUP'
        WHEN type = 'sedan' THEN 'SEDAN'
        WHEN type = 'SUV' THEN 'SUV'
        WHEN type = 'truck' THEN 'TRUCK'
        WHEN type = 'van' THEN 'VAN'
        WHEN type = 'wagon' THEN 'WAGON'
        ELSE 'OTHER'
    END,
    CASE 
        WHEN paint_color = 'black' THEN 'BLACK'
        WHEN paint_color = 'blue' THEN 'BLUE'
        WHEN paint_color = 'brown' THEN 'BROWN'
        WHEN paint_color = 'custom' THEN 'CUSTOM'
        WHEN paint_color = 'green' THEN 'GREEN'
        WHEN paint_color = 'grey' THEN 'GREY'
        WHEN paint_color = 'orange' THEN 'ORANGE'
        WHEN paint_color = 'purple' THEN 'PURPLE'
        WHEN paint_color = 'red' THEN 'RED'
        WHEN paint_color = 'silver' THEN 'SILVER'
        WHEN paint_color = 'white' THEN 'WHITE'
        WHEN paint_color = 'yellow' THEN 'YELLOW'
        ELSE NULL
    END
FROM temp_vehicles
WHERE id IN (SELECT VehicleId FROM Vehicles);

-- 9. Images表
INSERT IGNORE INTO Images (VehicleId, ImageURL)
SELECT id, image_url
FROM temp_vehicles
WHERE image_url IS NOT NULL
AND id IN (SELECT VehicleId FROM Vehicles);

-- 10. Locations表
INSERT IGNORE INTO Locations (VehicleId, Latitude, Longitude, RegionId)
SELECT 
    tv.id,
    tv.lat,
    tv.long,
    r.RegionId
FROM temp_vehicles tv
JOIN Regions r ON tv.region = r.RegionName
JOIN States s ON tv.state = s.StateName
WHERE tv.id IN (SELECT VehicleId FROM Vehicles)
AND r.StateId = s.StateId;