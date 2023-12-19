package com.bos.resource.app.fota.model.entity;

import com.bos.resource.app.common.domain.entity.BaseEntity;
import com.bos.resource.app.common.domain.enums.UseType;
import com.bos.resource.app.device.model.entity.SupportModel;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@NoArgsConstructor
@Entity
@Table(name = "TB_FOTA_PACKAGE")
@SQLRestriction("USE_YN = 'Y'")
@Getter
public class Package extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PACKAGE_ID")
    private Long packageId;

    @Column(name = "PACKAGE_NM")
    private String packageName;

    @Column(name = "PACKAGE_DESC")
    private String description;

    @Column(name = "CURRENT_VER")
    private String currentVersion;

    @Column(name = "TARGET_VER")
    private String targetVersion;

    @Column(name = "USE_YN")
    @Enumerated(EnumType.STRING)
    private UseType useYn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MODEL_CD", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private SupportModel model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FIRMWARE_MAIN_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Firmware firmware;

    @Column(name = "COMPANY_ID")
    private Long companyId;

    @Column(name = "PLATFORM_ID")
    private Long platformId;

    @Builder
    public Package(Long packageId, String packageName, String description, String currentVersion, String targetVersion, UseType useYn, SupportModel model, Firmware firmware, Long companyId, Long platformId, String email) {
        this.packageId = packageId;
        this.packageName = packageName;
        this.description = description;
        this.currentVersion = currentVersion;
        this.targetVersion = targetVersion;
        this.useYn = useYn;
        this.model = model;
        this.firmware = firmware;
        this.companyId = companyId;
        this.platformId = platformId;
        super.createId = email;
        super.updateId = email;
    }

    public static Package createPackage(String newPackageName, Firmware firmware, SupportModel supportModel, ResourceOwnerDto requestUser) {
        return Package.builder()
                .platformId(firmware.getPlatformId())
                .description(newPackageName)
                .currentVersion("1")
                .targetVersion(firmware.getVersion())
                .model(supportModel)
                .firmware(firmware)
                .companyId(requestUser.getCompanyId())
                .packageName( newPackageName)
                .useYn(UseType.Y)
                .email(requestUser.getEmail())
                .build();
    }
}