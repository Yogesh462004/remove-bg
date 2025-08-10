package in.yogesh.removebg.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generate PK
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderId;

    private String clerkId;
    private String plan;
    private Double amount;
    private Integer credits;

    private Boolean payment;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @PrePersist
    public void prePersist() {
        if (payment == null) {
            payment = false;
        }
    }
}
