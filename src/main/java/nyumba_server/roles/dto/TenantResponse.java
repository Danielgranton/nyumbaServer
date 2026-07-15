package nyumba_server.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Long unitId;
    private String unitNumber;
    private String propertyName;
    private LocalDate moveInDate;
    private LocalDate moveOutDate;
    private LocalDateTime createdAt;
}