package com.sellular.commons.jpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@MappedSuperclass
@Getter
public abstract class BaseDomain implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @Setter
    private Timestamp createdAt;

    @Setter
    private Timestamp updatedAt;

    @Setter
    private String createdBy;

    @Setter
    private String updatedBy;

    @Setter
    private boolean deleted;

    @PrePersist
    public void prePersist() {
        final Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        setCreatedAt(currentTime);
        setUpdatedAt(currentTime);
    }

    @PreUpdate
    public void preUpdate() {
        setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    }

}
