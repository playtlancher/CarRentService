package com.venediktov.carservice.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@javax.persistence.MappedSuperclass
public abstract class BaseEntity {
    @javax.persistence.Id
    @javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    private Long id;
    
    @javax.persistence.Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @javax.persistence.Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 