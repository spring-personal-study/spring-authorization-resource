package com.bos.resource.app.common.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @Setter
    @Column(name = "CREATE_ID", length = 50, nullable = false)
    protected String createId;

    @Setter
    @Column(name = "UPDATE_ID", length = 50, nullable = false)
    protected String updateId;

    @CreationTimestamp
    @Column(name = "CREATE_DT", updatable = false)
    protected LocalDateTime createDt;

    @UpdateTimestamp
    @Column(name = "UPDATE_DT")
    protected LocalDateTime updateDt;

}
