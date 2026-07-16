package nyumba_server.units;

import java.math.BigDecimal;
import nyumba_server.property.Property;
import nyumba_server.units.UnitStatus;
import nyumba_server.units.UnitType;
import nyumba_server.tenants.Tenant;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "units")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String unitNumber; // e.g. A1, B3, 101

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitType unitType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rentAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnitStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @OneToOne(mappedBy = "unit", fetch = FetchType.LAZY)
    private Tenant tenant;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}