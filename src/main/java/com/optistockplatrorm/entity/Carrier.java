package com.optistockplatrorm.entity;

import com.optistockplatrorm.entity.Enums.CarrierStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "carriers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Carrier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private String contactEmail;
    private String contactPhone;

    private BigDecimal baseShippingRate = BigDecimal.ZERO;
    private Integer maxDailyCapacity = 100;
    private Integer currentDailyShipments = 0;
    private LocalTime cutOffTime = LocalTime.of(15, 0);

    @Enumerated(EnumType.STRING)
    private CarrierStatus status = CarrierStatus.ACTIVE;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

