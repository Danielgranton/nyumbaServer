package nyumba_server.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nyumba_server.auth.dto.LoginRequest;
import nyumba_server.auth.dto.RefreshResponse;
import nyumba_server.auth.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(authService.refresh(user));
    }
}
