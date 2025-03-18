package car.genie.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Locations {
    private long vehicleId;
    private float latitude;
    private float longitude;
    private int regionId;
}
