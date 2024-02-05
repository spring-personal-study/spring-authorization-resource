package com.bos.resource.app.device.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "TB_DEVICE_DETAIL")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Comment("TB_DEVICE_DETAIL primary key")
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICE_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    Device device;

    @Column(name = "SN", nullable = false)
    @Comment("serial number of the device")
    String serialNumber;

    @Column(name = "IMEI")
    @Comment("IMEI of the device")
    String imei;

    @Column(name = "IMSI")
    @Comment("IMSI of the device")
    String imsi;

    @Column(name = "MAC_ADDRESS")
    @Comment("Mac address of the device")
    String macAddress;

    @Column(name = "MODEL_NM")
    @Comment("model name of the device")
    String modelName;

    @Column(name = "VENDOR")
    @Comment("vendor of the device")
    String vendor;

    @Column(name = "OS_VER")
    @Comment("OS version of the device")
    String osVersion;

    @Column(name = "OS_BUILD_DATE")
    @Comment("os build date of the device")
    String osBuildDate;

    @Column(name = "OS_BUILD_NUMBER")
    @Comment("OS build number of the device")
    String osBuildNumber;

    @Column(name = "OS_BUILD_DATE_STR")
    @Comment("os build date string")
    String osBuildDateString;

    @Column(name = "FCM_TOKEN")
    @Comment("FCM token for the device")
    String fcmToken;

    @Column(name = "UPDATE_DT", nullable = false)
    LocalDateTime updatedDate;

    @Builder
    public DeviceDetail(Long id, Device device, String serialNumber, String imei, String imsi, String macAddress, String modelName, String vendor, String osVersion, String osBuildDate, String osBuildNumber, String osBuildDateString, String fcmToken, LocalDateTime updatedDate) {
        this.id = id;
        this.device = device;
        this.serialNumber = serialNumber;
        this.imei = imei;
        this.imsi = imsi;
        this.macAddress = macAddress;
        this.modelName = modelName;
        this.vendor = vendor;
        this.osVersion = osVersion;
        this.osBuildDate = osBuildDate;
        this.osBuildNumber = osBuildNumber;
        this.osBuildDateString = osBuildDateString;
        this.fcmToken = fcmToken;
        this.updatedDate = updatedDate;
    }
}
