package car.genie.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class Manufacturers {
    private Integer manufacturerId; // AUTO_INCREMENT
    private String manufacturerName;
}