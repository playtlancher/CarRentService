package com.venediktov.carservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@javax.persistence.Entity
@javax.persistence.Table(name = "cars")
public class Car extends BaseEntity {
    private String brand;
    private String model;
    private String vinCode;
    private String cityBased;
    private int productionYear;
    private String imageUrl;
    
    private java.math.BigDecimal dailyPrice;
    
    @javax.persistence.Enumerated(javax.persistence.EnumType.STRING)
    private CarStatus status;
    
    private String description;

    private Double latitude;
    private Double longitude;

    public enum CarStatus {
        AVAILABLE,
        RENTED,
        MAINTENANCE
    }
}
