package car.genie.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleSpecs {
    private long vehicleId;
    private String cylinders;
    private String fuel;  // ENUM('GAS', 'DIESEL', 'ELECTRIC', 'HYBRID', 'OTHER')
    private String transmission;  // ENUM('AUTOMATIC', 'MANUAL', 'OTHER')
    private String drive;  // ENUM('FWD', 'RWD', '4WD')
}
