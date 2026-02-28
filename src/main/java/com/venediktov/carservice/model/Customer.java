package com.venediktov.carservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@javax.persistence.Entity
@javax.persistence.Table(name = "customers")
public class Customer extends BaseEntity {
    private String name;
    private String email;
    private int rentAmountTimes;
}
