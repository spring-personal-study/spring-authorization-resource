package com.bos.resource.app.assets.model.entity;

import com.bos.resource.app.assets.model.enums.DeviceStatus;
import com.bos.resource.app.resourceowner.model.entity.ResourceOwner;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;


@Entity
@Getter
@SQLRestriction("USE_YN = 'Y'")
@Table(name = "TB_DEVICE")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEVICE_ID")
    @Comment("TB_DEVICE primary key")
    private Long id;

    @Column(name = "SN", nullable = false)
    @Comment("device serial number")
    private String serialNumber;

    @Column(name = "DEVICE_CD", nullable = false)
    @Comment("device code")
    private String deviceCode;

    @Setter
    @Column(name = "DEVICE_NM", nullable = false)
    @Comment("device name")
    private String deviceName;

    @Setter
    @Column(name = "DEVICE_DESC")
    @Comment("device description")
    private String deviceDescription;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    @Column(name = "ENROLL_DT")
    private LocalDateTime enrolmentDate;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "device")
    private DeviceDetail deviceDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ResourceOwner resourceOwner;
}
