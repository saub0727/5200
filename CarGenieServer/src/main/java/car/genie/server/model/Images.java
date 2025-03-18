package car.genie.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Images {
    private Integer imageId;
    private long vehicleId;
    private String imageURL;
}
