package nyumba_server.property;

import lombok.RequiredArgsConstructor;
import nyumba_server.property.dto.PropertyRequest;
import nyumba_server.property.dto.PropertyResponse;
import nyumba_server.property.dto.OccupancyResponse;
import nyumba_server.units.UnitStatus;
import nyumba_server.auth.User;
import nyumba_server.units.UnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UnitRepository unitRepository;

    public PropertyResponse create(PropertyRequest request, User landlord) {
        Property property = Property.builder()
                .name(request.getName())
                .address(request.getAddress())
                .type(request.getType())
                .landlord(landlord)
                .build();

        propertyRepository.save(property);
        return toResponse(property);
    }

    public List<PropertyResponse> getAllForLandlord(User landlord) {
        return propertyRepository.findByLandlordId(landlord.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PropertyResponse getById(Long id, User landlord) {
        Property property = findAndVerifyOwner(id, landlord);
        return toResponse(property);
    }

    public PropertyResponse update(Long id, PropertyRequest request, User landlord) {
        Property property = findAndVerifyOwner(id, landlord);
        property.setName(request.getName());
        property.setAddress(request.getAddress());
        property.setType(request.getType());
        propertyRepository.save(property);
        return toResponse(property);
    }

    public void delete(Long id, User landlord) {
        Property property = findAndVerifyOwner(id, landlord);
        propertyRepository.delete(property);
    }

    public OccupancyResponse getOccupancy(Long propertyId, User landlord) {
        Property property = findAndVerifyOwner(propertyId, landlord);
        long occupied = unitRepository.countByPropertyIdAndStatus(property.getId(), nyumba_server.units.UnitStatus.OCCUPIED);
        long vacant   = unitRepository.countByPropertyIdAndStatus(property.getId(), nyumba_server.units.UnitStatus.VACANT);
        long booked   = unitRepository.countByPropertyIdAndStatus(property.getId(), nyumba_server.units.UnitStatus.BOOKED);
        long total    = occupied + vacant + booked;
        double pct    = total > 0 ? (occupied * 100.0) / total : 0.0;
        return OccupancyResponse.builder()
                .propertyId(property.getId())
                .propertyName(property.getName())
                .address(property.getAddress())
                .totalUnits(total)
                .occupiedUnits(occupied)
                .vacantUnits(vacant)
                .bookedUnits(booked)
                .occupancyPercentage(Math.round(pct * 100.0) / 100.0)
                .build();
    }

    private Property findAndVerifyOwner(Long id, User landlord) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        if (!property.getLandlord().getId().equals(landlord.getId())) {
            throw new RuntimeException("Access denied");
        }
        return property;
    }

    private PropertyResponse toResponse(Property property) {
        long total    = unitRepository.countByPropertyIdAndStatus(property.getId(), UnitStatus.VACANT)
                      + unitRepository.countByPropertyIdAndStatus(property.getId(), UnitStatus.OCCUPIED);
        long occupied = unitRepository.countByPropertyIdAndStatus(property.getId(), UnitStatus.OCCUPIED);
        long vacant   = unitRepository.countByPropertyIdAndStatus(property.getId(), UnitStatus.VACANT);

        return PropertyResponse.builder()
                .id(property.getId())
                .name(property.getName())
                .address(property.getAddress())
                .type(property.getType())
                .totalUnits(total)
                .occupiedUnits(occupied)
                .vacantUnits(vacant)
                .createdAt(property.getCreatedAt())
                .build();
    }
}