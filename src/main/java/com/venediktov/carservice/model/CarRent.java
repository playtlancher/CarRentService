package com.venediktov.carservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class CarRent extends BaseEntity {
    private Long carId;
    private Long customerId;
    private LocalDate startDate;
    private LocalDate endDate;
}
