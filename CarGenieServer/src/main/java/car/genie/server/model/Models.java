package car.genie.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class Models {
    private Integer modelId; // AUTO_INCREMENT
    private String modelName;
    private Manufacturers manufacturer;
}