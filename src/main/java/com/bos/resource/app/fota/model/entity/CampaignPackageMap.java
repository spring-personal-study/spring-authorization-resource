package com.bos.resource.app.fota.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "TB_FOTA_CAMPAIGN_PACKAGE_MAP")
public class CampaignPackageMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CAM_PACK_MAP_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PACKAGE_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Package fotaPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CAMPAIGN_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Campaign campaign;

    @Builder
    public CampaignPackageMap(Long id, Package fotaPackage, Campaign campaign) {
        this.id = id;
        this.fotaPackage = fotaPackage;
        this.campaign = campaign;
    }

    public static CampaignPackageMap prepareSave(Campaign fotaCampaign, Package fotaPackage) {
        return CampaignPackageMap.builder()
                .fotaPackage(fotaPackage)
                .campaign(fotaCampaign)
                .build();
    }
}
