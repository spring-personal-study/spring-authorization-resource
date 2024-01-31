package com.bos.resource.app.fota.model.entity;

import com.bos.resource.app.device.model.entity.DeviceTag;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "TB_FOTA_DEVICE_TAG_MAP")
public class CampaignDeviceTagMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOTA_DEVICE_TAG_MAP_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CAMPAIGN_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICE_TAG_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private DeviceTag deviceTag;


    @Builder
    public CampaignDeviceTagMap(Long id, Campaign campaign, DeviceTag deviceTag) {
        this.id = id;
        this.campaign = campaign;
        this.deviceTag = deviceTag;
    }

    public static CampaignDeviceTagMap prepareSave(Campaign campaign, DeviceTag deviceTag) {
        return CampaignDeviceTagMap.builder()
                .deviceTag(deviceTag)
                .campaign(campaign)
                .build();
    }
}
