package com.bos.resource.app.fota.model.dto;

import com.bos.resource.app.common.domain.enums.UseType;
import com.bos.resource.app.fota.model.enums.CampaignStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CampaignStatusAggregation {
    //private final String autoUpdateId;
    //private final String deploymentId;
    //private final String deploymentTag;
    private final CampaignStatus deploymentStatus;
    private final Long totalDevices;
    //private final Integer created;
    private final Integer scheduled;
    private final Integer downloading;
    private final Integer awaitingInstall;
    private final Integer completed;
    //private final Integer cancelled;
    //private final Integer unknown;
    //private final UseType cancelRequested; // TODO: need to change to Boolean*/
    private final Integer failed;
    //private final Integer failedDownload;
    //private final Integer failedInstall;
    private final LocalDateTime completedOn;
    //private final LocalDateTime cancelledOn;
}
