package com.bos.resource.app.device.model.entity;

import com.bos.resource.app.common.domain.enums.UseType;
import com.bos.resource.app.device.model.enums.DeviceStatus;
import com.bos.resource.app.resourceowner.model.entity.ResourceOwner;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;


@Entity
@Getter
@SQLRestriction("USE_YN = 'Y'")
@Table(name = "TB_DEVICE")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEVICE_ID")
    @Comment("TB_DEVICE primary key")
    private Long id;

    @Column(name = "SN", nullable = false, unique = true)
    @Comment("device serial number")
    private String serialNumber;

    @Column(name = "DEVICE_CD", nullable = false, unique = true)
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

    @Column(name = "IN_WARRANTY", nullable = false)
    @Enumerated(EnumType.STRING)
    private UseType validWarranty;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "device")
    private DeviceDetail deviceDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ResourceOwner resourceOwner;

    @Builder
    public Device(Long id, String serialNumber, String deviceCode, String deviceName, String deviceDescription, DeviceStatus status, LocalDateTime enrolmentDate, UseType validWarranty, DeviceDetail deviceDetail, ResourceOwner resourceOwner) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.deviceCode = deviceCode;
        this.deviceName = deviceName;
        this.deviceDescription = deviceDescription;
        this.status = status;
        this.enrolmentDate = enrolmentDate;
        this.validWarranty = validWarranty;
        this.deviceDetail = deviceDetail;
        this.resourceOwner = resourceOwner;
    }
}
