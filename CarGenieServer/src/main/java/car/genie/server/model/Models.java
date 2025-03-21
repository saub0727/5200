package car.genie.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Models {
    private Integer modelId; // AUTO_INCREMENT
    private String modelName;
    private Integer manufacturerId;
}