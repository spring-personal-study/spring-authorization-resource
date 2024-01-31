package com.bos.resource.app.fota.model.entity;

import com.bos.resource.app.device.model.entity.Device;
import com.bos.resource.app.fota.model.enums.CampaignDeviceStatus;
import com.bos.resource.app.fota.model.enums.FirmwareUploadServerType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "TB_FOTA_CAMPAIGN_DEVICE_MAP")
@DynamicUpdate
public class CampaignDeviceMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CAM_DEVICE_MAP_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CAMPAIGN_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICE_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Device device;

    @Column(name = "STATUS")
    @Enumerated(value = EnumType.STRING)
    private CampaignDeviceStatus status;

    @Column(name = "DETAIL_STATUS")
    private String result;

    @Column(name = "DETAIL_STATUS_DESC")
    private String resultMessage;

    @Column(name = "SEQ")
    private Integer sequence;

    @Column(name = "UPLOAD_SERVER_TYPE")
    @Enumerated(value = EnumType.STRING)
    private FirmwareUploadServerType firmwareUploadServerType;

    @Column(name = "UPDATE_DT")
    @UpdateTimestamp
    private LocalDateTime updateDate;

    @Builder
    public CampaignDeviceMap(Long id, Campaign campaign, Device device, FirmwareUploadServerType firmwareUploadServerType) {
        this.id = id;
        this.campaign = campaign;
        this.device = device;
        this.status = CampaignDeviceStatus.PENDING;
        this.sequence = Integer.valueOf(campaign.getId() + "00");
        this.firmwareUploadServerType = firmwareUploadServerType;
    }

    public static CampaignDeviceMap prepareSave(Campaign campaign, Device device, FirmwareUploadServerType firmwareUploadServerType) {
        return CampaignDeviceMap.builder()
                .device(device)
                .campaign(campaign)
                .firmwareUploadServerType(firmwareUploadServerType)
                .build();
    }

    public void increaseSequence() {
        this.sequence += 1;
    }
}
