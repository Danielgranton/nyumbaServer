package nyumba_server.maintenance;

import lombok.RequiredArgsConstructor;
import nyumba_server.auth.User;
import nyumba_server.maintenance.dto.MaintenanceRequestDto;
import nyumba_server.maintenance.dto.MaintenanceResponseDto;
import nyumba_server.maintenance.dto.MaintenanceUpdateDto;
import nyumba_server.tenants.Tenant;
import nyumba_server.tenants.TenantRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRequestRepository maintenanceRepository;
    private final TenantRepository tenantRepository;

    // Tenant submits a request
    public MaintenanceResponseDto submit(MaintenanceRequestDto request, User user) {
        Tenant tenant = tenantRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Tenant profile not found"));

        MaintenanceRequest maintenance = MaintenanceRequest.builder()
                .tenant(tenant)
                .unit(tenant.getUnit())
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .status(MaintenanceStatus.PENDING)
                .build();

        maintenanceRepository.save(maintenance);
        return toResponse(maintenance);
    }

    // Tenant views their own requests
    public List<MaintenanceResponseDto> getMyRequests(User user) {
        Tenant tenant = tenantRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Tenant profile not found"));

        return maintenanceRepository.findByTenantId(tenant.getId())
                .stream().map(this::toResponse).toList();
    }

    // Landlord views all requests
    public List<MaintenanceResponseDto> getAllRequests(User landlord) {
        return maintenanceRepository.findAllByLandlordId(landlord.getId())
                .stream().map(this::toResponse).toList();
    }

    // Landlord filters by status
    public List<MaintenanceResponseDto> getByStatus(MaintenanceStatus status, User landlord) {
        return maintenanceRepository.findByLandlordIdAndStatus(landlord.getId(), status)
                .stream().map(this::toResponse).toList();
    }

    // Landlord updates status
    public MaintenanceResponseDto updateStatus(Long id, MaintenanceUpdateDto update, User landlord) {
        MaintenanceRequest request = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance request not found"));

        if (!request.getUnit().getProperty().getLandlord().getId().equals(landlord.getId())) {
            throw new RuntimeException("Access denied");
        }

        request.setStatus(update.getStatus());
        request.setLandlordNotes(update.getLandlordNotes());

        if (update.getStatus() == MaintenanceStatus.RESOLVED) {
            request.setResolvedAt(LocalDateTime.now());
        }

        maintenanceRepository.save(request);
        return toResponse(request);
    }

    private MaintenanceResponseDto toResponse(MaintenanceRequest request) {
        return MaintenanceResponseDto.builder()
                .id(request.getId())
                .tenantId(request.getTenant().getId())
                .tenantName(request.getTenant().getUser().getFullName())
                .unitId(request.getUnit().getId())
                .unitNumber(request.getUnit().getUnitNumber())
                .propertyName(request.getUnit().getProperty().getName())
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .status(request.getStatus())
                .landlordNotes(request.getLandlordNotes())
                .resolvedAt(request.getResolvedAt())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}