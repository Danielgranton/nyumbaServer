package nyumba_server.maintenance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import nyumba_server.maintenance.MaintenanceStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceUpdateDto {

    @NotNull
    private MaintenanceStatus status;

    private String landlordNotes;
}