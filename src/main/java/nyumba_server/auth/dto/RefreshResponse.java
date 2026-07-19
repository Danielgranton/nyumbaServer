package nyumba_server.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshResponse {
    private String token;
    private String email;
}
