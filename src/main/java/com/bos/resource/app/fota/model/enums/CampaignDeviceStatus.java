package com.bos.resource.app.fota.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CampaignDeviceStatus { //// IN_PROGRESS, PENDING, COMPLETED, ERROR, REPEATED, SUCCESS, FAIL
    PENDING(1L, "PENDING"),
    IN_PROGRESS(2L, "IN_PROGRESS"),
    COMPLETED(3L, "COMPLETED"),
    DOWNLOADING(4L, "DOWNLOADING"),
    DOWNLOADING_P2P(5L, "DOWNLOADING-P2P"),
    DOWNLOADED(6L, "DOWNLOADED"),
    UPGRADING(7L, "UPGRADING"),
    UPDATED(8L, "UPDATED"),
    FAIL(9L, "FAIL"),
    ;

    private final Long id;
    private final String status;

    public static CampaignDeviceStatus getCampaignStatusById(Long id) {
        for (CampaignDeviceStatus campaignStatus : values()) {
            if (campaignStatus.id.equals(id)) {
                return campaignStatus;
            }
        }
        return null;
    }
}
