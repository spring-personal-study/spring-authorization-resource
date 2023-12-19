package com.bos.resource.app.device.model.entity;

import com.bos.resource.app.common.domain.entity.BaseEntity;
import com.bos.resource.app.common.domain.enums.UseType;
import com.bos.resource.app.resourceowner.model.entity.Company;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "TB_DEVICE_GROUP")
@SQLRestriction("USE_YN = 'Y'")
@DynamicUpdate
public class DeviceGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEVICE_GRP_ID")
    Long id;

    @Column(name = "DEVICE_GRP_CD", length = 20)
    String groupCode;

    @Column(name = "DEVICE_GRP_NM", length = 50)
    String groupName;

    @Column(name = "DEVICE_GRP_DESC", length = 300)
    String groupDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private Company company;

    @OneToMany(mappedBy = "deviceGroup")
    private List<DeviceGroupMap> deviceGroups = new ArrayList<>();

    @Column(name = "USE_YN")
    @Enumerated(EnumType.STRING)
    UseType useType;
}