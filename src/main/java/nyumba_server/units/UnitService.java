package nyumba_server.units;

import lombok.RequiredArgsConstructor;
import nyumba_server.auth.User;
import nyumba_server.property.Property;
import nyumba_server.property.PropertyRepository;
import nyumba_server.units.dto.UnitRequest;
import nyumba_server.units.dto.UnitResponse;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;
    private final PropertyRepository propertyRepository;

    public UnitResponse create(Long propertyId, UnitRequest request, User landlord) {
        Property property = getPropertyAndVerify(propertyId, landlord);

        Unit unit = Unit.builder()
                .unitNumber(request.getUnitNumber())
                .unitType(request.getUnitType())
                .rentAmount(request.getRentAmount())
                .status(UnitStatus.VACANT)
                .property(property)
                .build();

        unitRepository.save(unit);
        return toResponse(unit);
    }

    public List<UnitResponse> getUnitsForProperty(Long propertyId, User landlord) {
        getPropertyAndVerify(propertyId, landlord);
        return unitRepository.findByPropertyId(propertyId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<UnitResponse> getVacantUnits(Long propertyId, User landlord) {
        getPropertyAndVerify(propertyId, landlord);
        return unitRepository.findByPropertyIdAndStatus(propertyId, UnitStatus.VACANT)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UnitResponse update(Long unitId, UnitRequest request, User landlord) {
        Unit unit = findAndVerifyOwner(unitId, landlord);
        unit.setUnitNumber(request.getUnitNumber());
        unit.setUnitType(request.getUnitType());
        unit.setRentAmount(request.getRentAmount());
        unitRepository.save(unit);
        return toResponse(unit);
    }

    public void delete(Long unitId, User landlord) {
        Unit unit = findAndVerifyOwner(unitId, landlord);
        unitRepository.delete(unit);
    }

    private Property getPropertyAndVerify(Long propertyId, User landlord) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        if (!property.getLandlord().getId().equals(landlord.getId())) {
            throw new RuntimeException("Access denied");
        }
        return property;
    }

    private Unit findAndVerifyOwner(Long unitId, User landlord) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
        if (!unit.getProperty().getLandlord().getId().equals(landlord.getId())) {
            throw new RuntimeException("Access denied");
        }
        return unit;
    }

    public UnitResponse toResponse(Unit unit) {
        return UnitResponse.builder()
                .id(unit.getId())
                .unitNumber(unit.getUnitNumber())
                .unitType(unit.getUnitType())
                .rentAmount(unit.getRentAmount())
                .status(unit.getStatus())
                .propertyId(unit.getProperty().getId())
                .propertyName(unit.getProperty().getName())
                .createdAt(unit.getCreatedAt())
                .build();
    }
}