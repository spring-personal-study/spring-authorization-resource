package com.bos.resource.app.fota.model.entity;

import com.bos.resource.app.device.model.entity.DeviceGroup;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "TB_FOTA_DEVICE_GROUP_MAP")
public class CampaignDeviceGroupMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FOTA_DEVICE_GROUP_MAP_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CAMPAIGN_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICE_GRP_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private DeviceGroup deviceGroup;

    @Builder
    public CampaignDeviceGroupMap(Campaign campaign, DeviceGroup deviceGroup) {
        this.campaign = campaign;
        this.deviceGroup = deviceGroup;
    }

    public static CampaignDeviceGroupMap prepareSave(Campaign campaign, DeviceGroup deviceGroup) {
        return CampaignDeviceGroupMap.builder()
                .deviceGroup(deviceGroup)
                .campaign(campaign)
                .build();
    }



    public void update(DeviceGroup deviceGroup) {
        this.deviceGroup = deviceGroup;
    }
}
