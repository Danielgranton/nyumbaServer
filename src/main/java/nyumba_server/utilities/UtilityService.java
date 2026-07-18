package nyumba_server.utilities;

import lombok.RequiredArgsConstructor;
import nyumba_server.auth.User;
import nyumba_server.property.Property;
import nyumba_server.property.PropertyRepository;
import nyumba_server.units.Unit;
import nyumba_server.units.UnitRepository;
import nyumba_server.utilities.dto.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilityService {

    private final UtilityRateRepository utilityRateRepository;
    private final WaterReadingRepository waterReadingRepository;
    private final PropertyRepository propertyRepository;
    private final UnitRepository unitRepository;

    // ── Utility Rates ──────────────────────────────────────────

    public UtilityRateResponse setUtilityRate(UtilityRateRequest request, User landlord) {
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!property.getLandlord().getId().equals(landlord.getId())) {
            throw new RuntimeException("Access denied");
        }

        UtilityRate rate = utilityRateRepository.findByPropertyId(property.getId())
                .orElse(UtilityRate.builder().property(property).build());

        rate.setWaterRatePerUnit(request.getWaterRatePerUnit());
        rate.setGarbageFixedCharge(request.getGarbageFixedCharge());
        utilityRateRepository.save(rate);

        return toRateResponse(rate);
    }

    public UtilityRateResponse getUtilityRate(Long propertyId, User landlord) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!property.getLandlord().getId().equals(landlord.getId())) {
            throw new RuntimeException("Access denied");
        }

        UtilityRate rate = utilityRateRepository.findByPropertyId(propertyId)
                .orElseThrow(() -> new RuntimeException("Utility rates not set for this property"));

        return toRateResponse(rate);
    }

    // ── Water Readings ──────────────────────────────────────────

    public WaterReadingResponse recordWaterReading(WaterReadingRequest request, User landlord) {
        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        if (!unit.getProperty().getLandlord().getId().equals(landlord.getId())) {
            throw new RuntimeException("Access denied");
        }

        // check duplicate reading for same month
        waterReadingRepository.findByUnitIdAndReadingMonth(unit.getId(), request.getReadingMonth())
                .ifPresent(r -> { throw new RuntimeException(
                        "Water reading for " + request.getReadingMonth() + " already exists"); });

        UtilityRate rate = utilityRateRepository.findByPropertyId(unit.getProperty().getId())
                .orElseThrow(() -> new RuntimeException("Utility rates not set for this property"));

        BigDecimal consumed = request.getCurrentReading().subtract(request.getPreviousReading());
        if (consumed.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Current reading cannot be less than previous reading");
        }

        BigDecimal charged = consumed.multiply(rate.getWaterRatePerUnit());

        WaterReading reading = WaterReading.builder()
                .unit(unit)
                .readingMonth(request.getReadingMonth())
                .previousReading(request.getPreviousReading())
                .currentReading(request.getCurrentReading())
                .unitsConsumed(consumed)
                .amountCharged(charged)
                .readingDate(request.getReadingDate())
                .build();

        waterReadingRepository.save(reading);
        return toReadingResponse(reading);
    }

    public List<WaterReadingResponse> getWaterReadingsForUnit(Long unitId, User landlord) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        if (!unit.getProperty().getLandlord().getId().equals(landlord.getId())) {
            throw new RuntimeException("Access denied");
        }

        return waterReadingRepository.findByUnitIdOrderByReadingDateDesc(unitId)
                .stream().map(this::toReadingResponse).toList();
    }

    // ── Response mappers ────────────────────────────────────────

    private UtilityRateResponse toRateResponse(UtilityRate rate) {
        return UtilityRateResponse.builder()
                .id(rate.getId())
                .propertyId(rate.getProperty().getId())
                .propertyName(rate.getProperty().getName())
                .waterRatePerUnit(rate.getWaterRatePerUnit())
                .garbageFixedCharge(rate.getGarbageFixedCharge())
                .createdAt(rate.getCreatedAt())
                .updatedAt(rate.getUpdatedAt())
                .build();
    }

    private WaterReadingResponse toReadingResponse(WaterReading reading) {
        return WaterReadingResponse.builder()
                .id(reading.getId())
                .unitId(reading.getUnit().getId())
                .unitNumber(reading.getUnit().getUnitNumber())
                .propertyName(reading.getUnit().getProperty().getName())
                .readingMonth(reading.getReadingMonth())
                .previousReading(reading.getPreviousReading())
                .currentReading(reading.getCurrentReading())
                .unitsConsumed(reading.getUnitsConsumed())
                .amountCharged(reading.getAmountCharged())
                .readingDate(reading.getReadingDate())
                .createdAt(reading.getCreatedAt())
                .build();
    }
}