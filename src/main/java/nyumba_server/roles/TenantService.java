package nyumba_server.roles;

import lombok.RequiredArgsConstructor;
import nyumba_server.auth.User;
import nyumba_server.auth.UserRepository;
import nyumba_server.roles.dto.TenantRequest;
import nyumba_server.roles.dto.TenantResponse;
import nyumba_server.units.Unit;
import nyumba_server.units.UnitRepository;
import nyumba_server.units.UnitStatus;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TenantResponse create(TenantRequest request, User landlord) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new RuntimeException("Unit not found"));

        if (!unit.getProperty().getLandlord().getId().equals(landlord.getId())) {
            throw new RuntimeException("Access denied");
        }

        if (unit.getStatus() == UnitStatus.OCCUPIED) {
            throw new RuntimeException("Unit is already occupied");
        }

        // Create user account for tenant
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.TENANT)
                .build();
        userRepository.save(user);

        // Create tenant profile
        Tenant tenant = Tenant.builder()
                .user(user)
                .unit(unit)
                .moveInDate(request.getMoveInDate())
                .build();
        tenantRepository.save(tenant);

        // Mark unit as occupied
        unit.setStatus(UnitStatus.OCCUPIED);
        unitRepository.save(unit);

        return toResponse(tenant);
    }

    public List<TenantResponse> getAllForLandlord(User landlord) {
        return tenantRepository.findAll()
                .stream()
                .filter(t -> t.getUnit().getProperty().getLandlord().getId().equals(landlord.getId()))
                .map(this::toResponse)
                .toList();
    }

    public TenantResponse getById(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        return toResponse(tenant);
    }

    @Transactional
    public void moveOut(Long tenantId, User landlord) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        if (!tenant.getUnit().getProperty().getLandlord().getId().equals(landlord.getId())) {
            throw new RuntimeException("Access denied");
        }

        Unit unit = tenant.getUnit();
        unit.setStatus(UnitStatus.VACANT);
        unitRepository.save(unit);

        tenant.setMoveOutDate(java.time.LocalDate.now());
        tenant.setUnit(null);
        tenantRepository.save(tenant);
    }

    private TenantResponse toResponse(Tenant tenant) {
        return TenantResponse.builder()
                .id(tenant.getId())
                .fullName(tenant.getUser().getFullName())
                .email(tenant.getUser().getEmail())
                .phone(tenant.getUser().getPhone())
                .unitId(tenant.getUnit() != null ? tenant.getUnit().getId() : null)
                .unitNumber(tenant.getUnit() != null ? tenant.getUnit().getUnitNumber() : null)
                .propertyName(tenant.getUnit() != null ? tenant.getUnit().getProperty().getName() : null)
                .moveInDate(tenant.getMoveInDate())
                .moveOutDate(tenant.getMoveOutDate())
                .createdAt(tenant.getCreatedAt())
                .build();
    }
}