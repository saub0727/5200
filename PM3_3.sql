-- Count the number of vehicles listed for sale, grouped by color
SELECT Color,
       COUNT(*) AS ColorCount
FROM VehicleClassification
WHERE Color IS NOT NULL
GROUP BY Color
ORDER BY ColorCount DESC;

-- Count vehicles by condition and calculate their percentage of the total listings
SELECT VehicleConditions.VehicleCondition,
       COUNT(*) AS VehicleCount,
       ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*)
                                 FROM VehicleConditions
                                 WHERE VehicleCondition IS NOT NULL),
             2) AS Percentage
FROM VehicleConditions
WHERE VehicleConditions.VehicleCondition IS NOT NULL
GROUP BY VehicleConditions.VehicleCondition
ORDER BY VehicleCount DESC;

-- Count the number of vehicles listed for each fuel type
SELECT Fuel,
       COUNT(*) AS VehicleCount
FROM Vehicles
         JOIN
     VehicleSpecs ON Vehicles.VehicleId = VehicleSpecs.VehicleId
WHERE Fuel IS NOT NULL
GROUP BY Fuel
ORDER BY VehicleCount DESC;

-- Count vehicle listings by a specific year and month
SELECT YEAR(PostingDate)  AS PostYear,
       MONTH(PostingDate) AS PostMonth,
       COUNT(VehicleId)   AS ListingCount
FROM Vehicles
WHERE YEAR(PostingDate) = 2021
  AND MONTH(PostingDate) = 4
GROUP BY PostYear, PostMonth;

-- Count the number of vehicles listed for each type
SELECT Type,
       COUNT(VehicleId) AS TypeCount
FROM VehicleClassification
WHERE Type IS NOT NULL
GROUP BY Type
ORDER BY TypeCount DESC;

-- Rank the top 10 manufacturers by average vehicle listing price
SELECT ManufacturerName,
       COUNT(*)          AS VehicleCount,
       ROUND(AVG(Price)) AS AvgPrice
FROM Vehicles
         JOIN
     Models ON Vehicles.ModelId = Models.ModelId
         JOIN
     Manufacturers ON Models.ManufacturerId = Manufacturers.ManufacturerId
GROUP BY ManufacturerName
ORDER BY AvgPrice DESC
LIMIT 10;

-- Count the number of vehicles listed in each state
SELECT StateName,
       COUNT(*) AS VehicleCount
FROM Vehicles
         JOIN
     Locations ON Vehicles.VehicleId = Locations.VehicleId
         JOIN
     Regions ON Locations.RegionId = Regions.RegionId
         JOIN
     States ON Regions.StateId = States.StateId
GROUP BY StateName
ORDER BY VehicleCount DESC, StateName ASC
LIMIT 10;
-- for ease of display

-- Retrieve the top 10 most available vehicle models along with their manufacturers in Washington State
SELECT Manufacturers.ManufacturerName,
       Models.ModelName,
       T.VehicleCnt
FROM (SELECT Vehicles.ModelId,
             COUNT(*) AS VehicleCnt
      FROM Vehicles
               JOIN Locations ON Locations.VehicleId = Vehicles.VehicleId
               JOIN Regions ON Locations.RegionId = Regions.RegionId
               JOIN States ON Regions.StateId = States.StateId
      WHERE Vehicles.ModelId IS NOT NULL
        AND States.StateName = 'wa'
      GROUP BY Vehicles.ModelId
      ORDER BY VehicleCnt DESC
      LIMIT 10) AS T
         JOIN Models ON Models.ModelId = T.ModelId
         JOIN Manufacturers ON Manufacturers.ManufacturerId = Models.ManufacturerId;

-- Retrieve the top 10 RAV4s in Washington State with the highest price-to-mileage ratio
SELECT FilteredVehicles.VehicleId,
       Manufacturers.ManufacturerName,
       FilteredVehicles.ModelName,
       FilteredVehicles.Price,
       FilteredVehicles.Odometer                                               AS Mileage,
       ROUND(FilteredVehicles.Price / NULLIF(FilteredVehicles.Odometer, 0), 2) AS PriceToQualityRatio
FROM (SELECT Vehicles.VehicleId,
             Vehicles.ModelId,
             Models.ManufacturerId,
             Models.ModelName,
             Vehicles.Price,
             VehicleConditions.Odometer
      FROM Vehicles
               JOIN Models ON Vehicles.ModelId = Models.ModelId
               JOIN VehicleConditions ON Vehicles.VehicleId = VehicleConditions.VehicleId
               JOIN Locations ON Vehicles.VehicleId = Locations.VehicleId
               JOIN Regions ON Locations.RegionId = Regions.RegionId
               JOIN States ON Regions.StateId = States.StateId
      WHERE Models.ModelName LIKE '%rav4%'
        AND States.StateName = 'wa'
        AND Vehicles.Price IS NOT NULL
        AND VehicleConditions.Odometer IS NOT NULL) AS FilteredVehicles
         JOIN Manufacturers ON Manufacturers.ManufacturerId = FilteredVehicles.ManufacturerId
ORDER BY PriceToQualityRatio DESC
LIMIT 10;

-- Calculate the average vehicle price grouped by vehicle age
SELECT 2025 - VehicleClassification.Year AS VehicleAge,
       COUNT(Vehicles.VehicleId)         AS VehicleCount,
       ROUND(AVG(Vehicles.Price), 0)     AS AvgPrice
FROM Vehicles
         JOIN
     VehicleClassification ON Vehicles.VehicleId = VehicleClassification.VehicleId
WHERE Vehicles.Price IS NOT NULL
  AND VehicleClassification.Year IS NOT NULL
GROUP BY VehicleAge
ORDER BY VehicleAge ASC
LIMIT 10; -- for ease of display
