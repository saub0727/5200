# PM3: Business Insights

## Project and Team

Project Name: **CarGenie**

Team Name: **HuskyAutoCoders** (Team 4)

Team Members:
- Yun Chen Chang
- Jinpeng Liu
- Luwei Fang
- Qiuying Zhuo
- Wen Xie
- Zhe Zhang
- Ziqi Liu

## Business Insights Questions

1. What are the most common vehicle colors listed for sale?  Understanding color popularity helps in analyzing customer preferences and market trends.
2. How are vehicles distributed across different conditions (e.g., Excellent, Good, Fair, Salvage)? This helps assess the quality of listings and market segmentation.
3. How many vehicles are listed for each fuel type? This helps analyze the adoption of different fuel types in the used car market.
4. How many vehicles were listed in a specific year and month? This helps analyze seasonal trends and listing activity over time.
5. Which vehicle types are most common in the used car market? This helps identify trends and customer demand for different vehicle categories.
6. Which manufacturers have the highest average vehicle listing prices? This helps identify premium brands and market trends in vehicle pricing.
7. Which states have the highest number of vehicle listings? Understanding regional demand helps optimize inventory distribution.
8. Which vehicle models are the most popular in a specific state (e.g., Washington)? This helps analyze local market trends and buyer preferences.
9. As a buyer in a specific state (e.g., Washington) looking for a certain model (e.g., Toyota RAV4), which listings offer the best value in terms of price relative to mileage? For now, we define high-value vehicles as those with a high price-to-mileage ratio, where lower mileage suggests better condition.
10. How does vehicle price vary based on vehicle age? This helps buyers compare pricing trends for older vs. newer vehicles.

## Select Queries for Business Insights

1. What are the most common vehicle colors listed for sale?  Understanding color popularity helps in analyzing customer preferences and market trends.

    ```sql
    -- Count the number of vehicles listed for sale, grouped by color
    SELECT 
        Color, COUNT(*) AS ColorCount
    FROM
        VehicleClassification
    WHERE
        Color IS NOT NULL
    GROUP BY Color
    ORDER BY ColorCount DESC;

    /*
    Color,ColorCount
    WHITE,21884
    BLACK,18446
    SILVER,13430
    BLUE,9026
    RED,8005
    GREY,7463
    CUSTOM,1894
    GREEN,1749
    BROWN,1630
    ORANGE,502
    YELLOW,446
    PURPLE,181
    */
    ```

2. How are vehicles distributed across different conditions (e.g., Excellent, Good, Fair, Salvage)? This helps assess the quality of listings and market segmentation.

    ```sql
    -- Count vehicles by condition and calculate their percentage of the total listings
    SELECT 
        VehicleConditions.VehicleCondition,
        COUNT(*) AS VehicleCount,
        ROUND(COUNT(*) * 100.0 / (SELECT 
                        COUNT(*)
                    FROM
                        VehicleConditions
                    WHERE
                        VehicleCondition IS NOT NULL),
                2) AS Percentage
    FROM
        VehicleConditions
    WHERE
        VehicleConditions.VehicleCondition IS NOT NULL
    GROUP BY VehicleConditions.VehicleCondition
    ORDER BY VehicleCount DESC;

    /*
    VehicleCondition,VehicleCount,Percentage
    EXCELLENT,31163,52.61
    GOOD,23231,39.22
    LIKE_NEW,3786,6.39
    FAIR,687,1.16
    NEW,309,0.52
    SALVAGE,56,0.09
    */
    ```

3. How many vehicles are listed for each fuel type? This helps analyze the adoption of different fuel types in the used car market.

    ```sql
    -- Count the number of vehicles listed for each fuel type
    SELECT 
        Fuel, COUNT(*) AS VehicleCount
    FROM
        Vehicles
            JOIN
        VehicleSpecs ON Vehicles.VehicleId = VehicleSpecs.VehicleId
    WHERE
        Fuel IS NOT NULL
    GROUP BY Fuel
    ORDER BY VehicleCount DESC;
    
    /*
    GAS,98030
    OTHER,9046
    DIESEL,5347
    HYBRID,1544
    ELECTRIC,505
    */
    ```
    
4. How many vehicles were listed in a specific year and month? This helps analyze seasonal trends and listing activity over time.

    ```sql
    -- Count vehicle listings by a specific year and month
    SELECT 
        YEAR(PostingDate) AS PostYear,
        MONTH(PostingDate) AS PostMonth,
        COUNT(VehicleId) AS ListingCount
    FROM
        Vehicles
    WHERE
        YEAR(PostingDate) = 2021
            AND MONTH(PostingDate) = 4
    GROUP BY PostYear , PostMonth;

    /*
    PostYear,PostMonth,ListingCount
    2021,4,74783
    */
    ```

5. Which vehicle types are most common in the used car market? This helps identify trends and customer demand for different vehicle categories.

    ```sql
    -- Count the number of vehicles listed for each type
    SELECT 
        Type, COUNT(VehicleId) AS TypeCount
    FROM
        VehicleClassification
    WHERE
        Type IS NOT NULL
    GROUP BY Type
    ORDER BY TypeCount DESC;
    
    /*
    Type,TypeCount
    SEDAN,28146
    SUV,27082
    OTHER,21599
    PICKUP,10067
    TRUCK,8842
    HATCHBACK,4707
    COUPE,4282
    WAGON,3754
    VAN,2536
    CONVERTIBLE,1828
    MINI-VAN,1505
    OFFROAD,70
    BUS,54
    */
    ```

6. Which manufacturers have the highest average vehicle listing prices? This helps identify premium brands and market trends in vehicle pricing.

    ```sql
    -- Rank the top 10 manufacturers by average vehicle listing price
    SELECT 
        ManufacturerName,
        COUNT(*) AS VehicleCount,
        ROUND(AVG(Price)) AS AvgPrice
    FROM
        Vehicles
            JOIN
        Models ON Vehicles.ModelId = Models.ModelId
            JOIN
        Manufacturers ON Models.ManufacturerId = Manufacturers.ManufacturerId
    GROUP BY ManufacturerName
    ORDER BY AvgPrice DESC
    LIMIT 10;

    /*
    ManufacturerName,VehicleCount,AvgPrice
    ferrari,30,120983
    aston-martin,11,52743
    tesla,194,39502
    porsche,471,36817
    rover,634,28205
    ram,4401,26800
    alfa-romeo,160,26165
    chevrolet,14009,26158
    gmc,4339,22714
    jaguar,345,20386
    */
    ```

7. Which states have the highest number of vehicle listings? Understanding regional demand helps optimize inventory distribution.

    ```sql
    -- Count the number of vehicles listed in each state
    SELECT 
        StateName, COUNT(*) AS VehicleCount
    FROM
        Vehicles
            JOIN
        Locations ON Vehicles.VehicleId = Locations.VehicleId
            JOIN
        Regions ON Locations.RegionId = Regions.RegionId
            JOIN
        States ON Regions.StateId = States.StateId
    GROUP BY StateName
    ORDER BY VehicleCount DESC , StateName ASC
    LIMIT 10; -- for ease of display

    /*
    StateName,VehicleCount
    ca,15456
    or,6890
    fl,6740
    tx,5768
    wa,5194
    ny,4329
    co,4087
    oh,4001
    nc,3435
    id,3416
    */    
    ```

8. Which vehicle models are the most popular in a specific state (e.g., Washington)? This helps analyze local market trends and buyer preferences.

    ```sql
    -- Retrieve the top 10 most available vehicle models along with their manufacturers in Washington State
    SELECT 
        Manufacturers.ManufacturerName, 
        Models.ModelName, 
        T.VehicleCnt
    FROM
        (SELECT 
            Vehicles.ModelId, 
            COUNT(*) AS VehicleCnt
        FROM Vehicles
        JOIN Locations ON Locations.VehicleId = Vehicles.VehicleId
        JOIN Regions ON Locations.RegionId = Regions.RegionId
        JOIN States ON Regions.StateId = States.StateId
        WHERE 
            Vehicles.ModelId IS NOT NULL 
            AND States.StateName = 'wa'
        GROUP BY Vehicles.ModelId
        ORDER BY VehicleCnt DESC
        LIMIT 10) AS T
    JOIN Models ON Models.ModelId = T.ModelId
    JOIN Manufacturers ON Manufacturers.ManufacturerId = Models.ManufacturerId;
        
    /*
    ManufacturerName,ModelName,VehicleCnt
    ford,f-150,86
    ram,1500,79
    chevrolet,silverado 1500,53
    subaru,outback,41
    toyota,tacoma,37
    jeep,wrangler unlimited,31
    mitsubishi,outlander sport,30
    jeep,wrangler unlimited sahara,30
    jeep,wrangler,29
    ford,super duty f-350 srw,27
    */
    ```

9. As a buyer in a specific state (e.g., Washington) looking for a certain model (e.g., Toyota RAV4), which listings offer the best value in terms of price relative to mileage? For now, we define high-value vehicles as those with a high price-to-mileage ratio, where lower mileage suggests better condition.

    ```sql
    -- Retrieve the top 10 RAV4s in Washington State with the highest price-to-mileage ratio  
    SELECT 
        FilteredVehicles.VehicleId,
        Manufacturers.ManufacturerName,
        FilteredVehicles.ModelName,
        FilteredVehicles.Price,
        FilteredVehicles.Odometer AS Mileage,
        ROUND(FilteredVehicles.Price / NULLIF(FilteredVehicles.Odometer, 0), 2) AS PriceToQualityRatio
    FROM
        (SELECT 
            Vehicles.VehicleId,
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
        WHERE
            Models.ModelName LIKE '%rav4%'
            AND States.StateName = 'wa'
            AND Vehicles.Price IS NOT NULL
            AND VehicleConditions.Odometer IS NOT NULL
        ) AS FilteredVehicles
    JOIN Manufacturers ON Manufacturers.ManufacturerId = FilteredVehicles.ManufacturerId
    ORDER BY PriceToQualityRatio DESC
    LIMIT 10;
    
    /*
    VehicleId,ManufacturerName,ModelName,Price,Mileage,PriceToQualityRatio
    7316250174,toyota,rav4 xle,27500,3920,7.02
    7310929118,toyota,rav4,33499,6477,5.17
    7307947980,toyota,electric rav4 prime xse awd,49995,12500,4.00
    7314361239,toyota,rav4 xle fwd gas suv auto,30999,9111,3.40
    7317089710,toyota,rav4 le awd 10k miles camera lane departure crash avoid 34 mpg,31995,10978,2.91
    7307760890,toyota,rav4 hybrid,32999,14325,2.30
    7316568002,toyota,rav4,27988,21361,1.31
    7304985323,toyota,rav4 adventure awd gas,32489,25143,1.29
    7316223193,toyota,rav4 le,25000,19498,1.28
    7305007657,toyota,rav4 adventure awd gas,32436,26632,1.22
    */
    ```

10. How does vehicle price vary based on vehicle age? This helps buyers compare pricing trends for older vs. newer vehicles.  

    ```sql
    -- Calculate the average vehicle price grouped by vehicle age
    SELECT 
        2025 - VehicleClassification.Year AS VehicleAge,
        COUNT(Vehicles.VehicleId) AS VehicleCount,
        ROUND(AVG(Vehicles.Price), 0) AS AvgPrice
    FROM
        Vehicles
            JOIN
        VehicleClassification ON Vehicles.VehicleId = VehicleClassification.VehicleId
    WHERE
        Vehicles.Price IS NOT NULL
            AND VehicleClassification.Year IS NOT NULL
    GROUP BY VehicleAge
    ORDER BY VehicleAge ASC
    LIMIT 10; -- for ease of display
    
    /*
    VehicleAge,VehicleCount,AvgPrice
    3,38,12141
    4,912,26211
    5,5164,31440
    6,7401,26962
    7,11237,23286
    8,10690,21314
    9,8693,19122
    10,9311,31028
    11,8938,16259
    12,9046,13642
    */
    ```

## Quick Refernece

Latest UML (please refer to the PM2 submission for the CREATE queries): ![UML](./PM2_UML.png)

PM1: [Project Idea](https://docs.google.com/document/d/1gjREEfOMyCWx_K2DIqv8zDLRQRgHMfI71NrNcuou1_Y)

PM2: [Database Schema](https://github.com/saub0727/5200/blob/b8ed9806e45b2b9123fccd131ef54a496cc0243a/PM2.md)