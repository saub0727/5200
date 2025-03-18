package car.genie.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleConditions {
    private long vehicleId;
    private int odometer;
    private String vehicleCondition; // ENUM('EXCELLENT', 'FAIR', 'GOOD', 'LIKE_NEW', 'NEW', 'SALVAGE')
    private String titleStatus; // ENUM('CLEAN', 'LIEN', 'MISSING', 'PARTS_ONLY', 'REBUILT', 'SALVAGE')
}
