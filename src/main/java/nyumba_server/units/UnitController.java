package nyumba_server.units;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nyumba_server.units.dto.UnitRequest;
import nyumba_server.units.dto.UnitResponse;
import nyumba_server.auth.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties/{propertyId}/units")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LANDLORD')")
public class UnitController {

    private final UnitService unitService;

    @PostMapping
    public ResponseEntity<UnitResponse> create(
            @PathVariable Long propertyId,
            @Valid @RequestBody UnitRequest request,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(unitService.create(propertyId, request, landlord));
    }

    @GetMapping
    public ResponseEntity<List<UnitResponse>> getAll(
            @PathVariable Long propertyId,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(unitService.getUnitsForProperty(propertyId, landlord));
    }

    @GetMapping("/vacant")
    public ResponseEntity<List<UnitResponse>> getVacant(
            @PathVariable Long propertyId,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(unitService.getVacantUnits(propertyId, landlord));
    }

    @PutMapping("/{unitId}")
    public ResponseEntity<UnitResponse> update(
            @PathVariable Long propertyId,
            @PathVariable Long unitId,
            @Valid @RequestBody UnitRequest request,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(unitService.update(unitId, request, landlord));
    }

    @DeleteMapping("/{unitId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long propertyId,
            @PathVariable Long unitId,
            @AuthenticationPrincipal User landlord) {
        unitService.delete(unitId, landlord);
        return ResponseEntity.noContent().build();
    }
}