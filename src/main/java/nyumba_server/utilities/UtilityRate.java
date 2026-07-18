package nyumba_server.utilities;

import jakarta.persistence.*;
import lombok.*;
import nyumba_server.property.Property;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "utility_rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilityRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false, unique = true)
    private Property property;

    // price per cubic meter (m³)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal waterRatePerUnit;

    // fixed monthly garbage charge
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal garbageFixedCharge;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}