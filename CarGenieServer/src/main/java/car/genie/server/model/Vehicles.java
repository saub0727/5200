package car.genie.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Vehicles {
    @NonNull
    private Long vehicleId; // Primary key
    private String vin; // Unique
    private Integer price;
    private LocalDate postingDate;
    private String description;
    private Integer modelId;
    
    // New fields to add
    private Integer year;
    private String make;
    private String model;
    private String condition;
    private Integer mileage;
    private String fuel;
    private String transmission;
    private String drive;
    private String titleStatus;
}