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

import java.util.List;

@Entity
@Table(name = "TB_DEVICE_TAG")
@SQLRestriction("USE_YN = 'Y'")
@NoArgsConstructor
@Getter
@DynamicUpdate
public class DeviceTag extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEVICE_TAG_ID")
    Long id;

    @Column(name = "DEVICE_TAG_NM", length = 50, nullable = false)
    String name;

    @Column(name = "DEVICE_TAG_DESC", length = 300, nullable = false)
    String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    Company company;

    @Column(name = "USE_YN")
    @Enumerated(value = EnumType.STRING)
    UseType useYn;

    @Column(name = "CREATE_ID", length = 50, nullable = false)
    String createId;

    @Column(name = "UPDATE_ID", length = 50, nullable = false)
    String updateId;

    public DeviceTag(Long id, String name, String description, Company company, UseType useYn, String createId, String updateId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.company = company;
        this.useYn = useYn;
        this.createId = createId;
        this.updateId = updateId;
    }
}
