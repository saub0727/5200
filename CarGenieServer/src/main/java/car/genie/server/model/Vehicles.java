package car.genie.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@AllArgsConstructor
@Builder
@Data
public class Vehicles {
    @NonNull
    private Long vehicleId; // Primary key
    private String vin; // Unique
    private Integer price;
    private LocalDate postingDate;
    private String description;
    private Models model;
}