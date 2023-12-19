package com.bos.resource.app.device.model.entity;

import com.bos.resource.app.common.domain.entity.BaseEntity;
import com.bos.resource.app.common.domain.enums.UseType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@SQLRestriction("USE_YN = 'Y'")
@Table(name = "TB_PLATFORM")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DevicePlatform extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PLATFORM_ID")
    @Comment("TB_PLATFORM primary key")
    Integer id;

    @Setter
    @Column(name = "PLATFORM_NM", nullable = false)
    @Comment("platform name")
    String platformName;

    @Column(name = "USE_YN", nullable = false)
    @Enumerated(EnumType.STRING)
    UseType useYn;

    @Column(name = "CREATE_ID", nullable = false)
    String createId;

    @Setter
    @Column(name = "UPDATE_ID", nullable = false)
    String updateId;
}
