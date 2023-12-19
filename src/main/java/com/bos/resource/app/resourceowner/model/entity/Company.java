package com.bos.resource.app.resourceowner.model.entity;

import com.bos.resource.app.common.domain.entity.BaseEntity;
import com.bos.resource.app.common.domain.enums.UseType;
import com.bos.resource.app.device.model.entity.DeviceGroup;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "TB_COMPANY")
@SQLRestriction("USE_YN = 'Y'")
@NoArgsConstructor
@DynamicUpdate
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMPANY_ID")
    private Long id;

    @Column(name = "COMPANY_NM", nullable = false)
    private String name;

    @Column(name = "COMPANY_DESC", nullable = false)
    private String description;

    @Column(name = "USE_YN", nullable = false)
    @Enumerated(EnumType.STRING)
    private UseType useFlag;

    @Column(name = "ADMIN_YN")
    @Enumerated(EnumType.STRING)
    private UseType adminFlag;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    private List<DeviceGroup> deviceGroups = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    private List<UserGroup> userGroups = new ArrayList<>();

}
