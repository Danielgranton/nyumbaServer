package nyumba_server.maintenance;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nyumba_server.auth.User;
import nyumba_server.maintenance.dto.MaintenanceRequestDto;
import nyumba_server.maintenance.dto.MaintenanceResponseDto;
import nyumba_server.maintenance.dto.MaintenanceUpdateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    // Tenant submits a request
    @PostMapping
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<MaintenanceResponseDto> submit(
            @Valid @RequestBody MaintenanceRequestDto request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(maintenanceService.submit(request, user));
    }

    // Tenant views their own requests
    @GetMapping("/my")
    @PreAuthorize("hasRole('TENANT')")
    public ResponseEntity<List<MaintenanceResponseDto>> myRequests(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(maintenanceService.getMyRequests(user));
    }

    // Landlord views all requests
    @GetMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<MaintenanceResponseDto>> allRequests(
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(maintenanceService.getAllRequests(landlord));
    }

    // Landlord filters by status
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<MaintenanceResponseDto>> byStatus(
            @PathVariable MaintenanceStatus status,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(maintenanceService.getByStatus(status, landlord));
    }

    // Landlord updates a request status
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<MaintenanceResponseDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody MaintenanceUpdateDto update,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(maintenanceService.updateStatus(id, update, landlord));
    }
}