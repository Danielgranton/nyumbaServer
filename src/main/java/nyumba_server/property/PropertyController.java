package nyumba_server.property;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nyumba_server.property.dto.PropertyRequest;
import nyumba_server.property.dto.PropertyResponse;
import nyumba_server.auth.User;
import nyumba_server.property.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LANDLORD')")
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    public ResponseEntity<PropertyResponse> create(
            @Valid @RequestBody PropertyRequest request,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(propertyService.create(request, landlord));
    }

    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAll(
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(propertyService.getAllForLandlord(landlord));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyResponse> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(propertyService.getById(id, landlord));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PropertyRequest request,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(propertyService.update(id, request, landlord));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User landlord) {
        propertyService.delete(id, landlord);
        return ResponseEntity.noContent().build();
    }
}