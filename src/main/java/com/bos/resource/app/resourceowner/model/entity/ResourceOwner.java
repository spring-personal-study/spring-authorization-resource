package com.bos.resource.app.resourceowner.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "TB_USER")
public class ResourceOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "ID", length = 20, nullable = false, unique = true)
    private String resourceOwnerId;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "COMPANY_ID")
    private Long companyId;

    @Builder
    public ResourceOwner(Long id, String resourceOwnerId, String email, Long companyId) {
        this.id = id;
        this.resourceOwnerId = resourceOwnerId;
        this.email = email;
        this.companyId = companyId;
    }
}
