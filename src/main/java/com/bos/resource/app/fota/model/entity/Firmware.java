package com.bos.resource.app.fota.model.entity;

import com.bos.resource.app.common.domain.entity.BaseEntity;
import com.bos.resource.app.common.domain.enums.UseType;
import com.bos.resource.app.fota.model.constants.enums.FirmwareUploadServerType;
import com.bos.resource.app.fota.model.constants.enums.PackageType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "TB_PV_FIRMWARE_MAIN")
@Getter
public class Firmware extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FIRMWARE_MAIN_ID")
    private Long id;

    @Column(name = "FIRMWARE_NM")
    private String name;

    @Column(name = "FIRMWARE_VER")
    private String version;

    @Column(name = "MODEL")
    private String model;

    @Column(name = "ANDROID_VERSION_ID")
    private Long osVersion;

    @Column(name = "FIRMWARE_UUID_NM")
    private String uuid;

    @Column(name = "FIRMWARE_ORIGIN_NM")
    private String originFileName;

    @Column(name = "FIRMWARE_URL")
    private String url;

    @Column(name = "FIRMWARE_DESC")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "UPLOAD_SERVER_TYPE")
    private FirmwareUploadServerType uploadServerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "PACKAGE_TYPE")
    private PackageType packageType;

    @Enumerated(EnumType.STRING)
    @Column(name = "USE_YN")
    private UseType useYn;

    @Column(name = "PLATFORM_ID")
    private Long platformId;

    @Column(name = "COMPANY_ID")
    private Long companyId;

    @Builder
    public Firmware(Long id, String name, String version, String model, Long osVersion, String uuid, String originFileName, String url, String description, FirmwareUploadServerType uploadServerType, PackageType packageType, UseType useYn, Long platformId, Long companyId) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.model = model;
        this.osVersion = osVersion;
        this.uuid = uuid;
        this.originFileName = originFileName;
        this.url = url;
        this.description = description;
        this.uploadServerType = uploadServerType;
        this.packageType = packageType;
        this.useYn = useYn;
        this.platformId = platformId;
        this.companyId = companyId;
    }
}
