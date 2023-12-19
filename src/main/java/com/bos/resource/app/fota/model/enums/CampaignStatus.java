package com.bos.resource.app.fota.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CampaignStatus {
    ACTIVE(1L),
    INACTIVE(2L),
    NOT_FOUND(99L);

    private final Long id;

    public static CampaignStatus getCampaignStatusById(Long id) {
        for (CampaignStatus campaignStatus : values()) {
            if (campaignStatus.id.equals(id)) {
                return campaignStatus;
            }
        }
        return NOT_FOUND;
    }
}
