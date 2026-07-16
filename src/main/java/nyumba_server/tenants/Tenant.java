package nyumba_server.tenants;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import nyumba_server.auth.User;
import nyumba_server.units.Unit;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", unique = true)
    private Unit unit;

    @Column(nullable = false)
    private LocalDate moveInDate;

    private LocalDate moveOutDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}