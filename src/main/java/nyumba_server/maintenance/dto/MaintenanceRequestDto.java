package nyumba_server.maintenance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRequestDto {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private String imageUrl;
}