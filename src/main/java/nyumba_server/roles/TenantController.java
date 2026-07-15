package nyumba_server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nyumba_server.dto.TenantRequest;
import nyumba_server.dto.TenantResponse;
import nyumba_server.model.User;
import nyumba_server.service.TenantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<TenantResponse> create(
            @Valid @RequestBody TenantRequest request,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(tenantService.create(request, landlord));
    }

    @GetMapping
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<List<TenantResponse>> getAll(
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(tenantService.getAllForLandlord(landlord));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<TenantResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tenantService.getById(id));
    }

    @PatchMapping("/{id}/move-out")
    @PreAuthorize("hasRole('LANDLORD')")
    public ResponseEntity<Void> moveOut(
            @PathVariable Long id,
            @AuthenticationPrincipal User landlord) {
        tenantService.moveOut(id, landlord);
        return ResponseEntity.noContent().build();
    }
}