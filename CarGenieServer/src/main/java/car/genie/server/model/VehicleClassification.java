package car.genie.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleClassification {
    private long vehicleId;
    private int year;
    private String size; // ENUM('COMPACT', 'MID-SIZE', 'FULL-SIZE', 'SUB-COMPACT')
    private String type; // ENUM('BUS', 'CONVERTIBLE', 'COUPE', 'HATCHBACK', 'MINI-VAN', 'OFFROAD', 'OTHER', 'PICKUP', 'SEDAN', 'SUV', 'TRUCK', 'VAN', 'WAGON')
    private String color; // ENUM('BLACK', 'BLUE', 'BROWN', 'CUSTOM', 'GREEN', 'GREY', 'ORANGE', 'PURPLE', 'RED', 'SILVER', 'WHITE', 'YELLOW')
}
