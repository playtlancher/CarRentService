package com.venediktov.carservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import java.time.LocalDate;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "rentals")
public class Rental extends BaseEntity {
    private Long carId;
    private Long userId; 
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private RentalStatus status;

    public enum RentalStatus {
        ACTIVE,
        COMPLETED,
        CANCELED
    }
}
