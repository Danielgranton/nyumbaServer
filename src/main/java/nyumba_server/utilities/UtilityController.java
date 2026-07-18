package nyumba_server.utilities;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nyumba_server.auth.User;
import nyumba_server.utilities.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilities")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LANDLORD')")
public class UtilityController {

    private final UtilityService utilityService;

    // Set or update utility rates for a property
    @PostMapping("/rates")
    public ResponseEntity<UtilityRateResponse> setRates(
            @Valid @RequestBody UtilityRateRequest request,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(utilityService.setUtilityRate(request, landlord));
    }

    // Get utility rates for a property
    @GetMapping("/rates/{propertyId}")
    public ResponseEntity<UtilityRateResponse> getRates(
            @PathVariable Long propertyId,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(utilityService.getUtilityRate(propertyId, landlord));
    }

    // Record water reading for a unit
    @PostMapping("/water")
    public ResponseEntity<WaterReadingResponse> recordWater(
            @Valid @RequestBody WaterReadingRequest request,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(utilityService.recordWaterReading(request, landlord));
    }

    // Get all water readings for a unit
    @GetMapping("/water/unit/{unitId}")
    public ResponseEntity<List<WaterReadingResponse>> getWaterReadings(
            @PathVariable Long unitId,
            @AuthenticationPrincipal User landlord) {
        return ResponseEntity.ok(utilityService.getWaterReadingsForUnit(unitId, landlord));
    }
}