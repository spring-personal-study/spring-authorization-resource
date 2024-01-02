package com.bos.resource.app.device.model.entity;

import com.bos.resource.app.common.domain.entity.BaseEntity;
import com.bos.resource.app.common.domain.enums.UseType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Table(name = "TB_SUPPORT_MODEL")
@SQLRestriction("USE_YN = 'Y'")
@NoArgsConstructor
@DynamicUpdate
public class SupportModel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MODEL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLATFORM_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private DevicePlatform platform;

    @Column(name = "MODEL_NM")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "USE_YN")
    private UseType useType;
}
