package nyumba_server.tenants;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {
    Optional<Tenant> findByUserId(Long userId);
    Optional<Tenant> findByUnitId(Long unitId);
    boolean existsByUnitId(Long unitId);
}