package com.sellular.commons.jpa.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
public class ExternalBase extends BaseDomain {

    @JsonProperty (access = JsonProperty.Access.READ_ONLY)
    @Column(name = "external_id", unique = true)
    private String externalId;

    @PrePersist
    @Override
    public void prePersist() {
        super.prePersist();
        // TODO: Set created_by updated_by to userName from application context
        final String name = "SYSTEM";
        setCreatedBy(name);
        setUpdatedBy(name);
        if (Strings.isNullOrEmpty(externalId)) {
            externalId = UUID.randomUUID().toString();
        }
    }

    @PreUpdate
    @Override
    public void preUpdate() {
        super.preUpdate();
        // TODO: Set updated_by to userName from application context
        if (Strings.isNullOrEmpty(externalId)) {
            externalId = UUID.randomUUID().toString();
        }
    }

}
