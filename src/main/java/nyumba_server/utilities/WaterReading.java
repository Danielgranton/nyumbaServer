package nyumba_server.utilities;

import jakarta.persistence.*;
import lombok.*;
import nyumba_server.units.Unit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "water_readings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column(nullable = false)
    private String readingMonth; // format: "2026-07"

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal previousReading;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal currentReading;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitsConsumed; // currentReading - previousReading

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amountCharged; // unitsConsumed * waterRatePerUnit

    @Column(nullable = false)
    private LocalDate readingDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}