package com.bos.resource.app.fota.repository;

import com.bos.resource.app.fota.model.dto.CampaignStatusAggregation;
import com.bos.resource.app.fota.model.entity.Campaign;

import java.time.LocalDateTime;

public interface QCampaignRepository {
    Campaign findFirstByNameStartsWithAndCompanyIdOrderByNameDesc(String newDeploymentPrefix, Long companyId);

    CampaignStatusAggregation findCampaignStatusByCompanyIdAndCampaignIdAndBetweenDateAndStatus(
            Long companyId, String campaignId, LocalDateTime startDate, LocalDateTime endDate, String status);

    CampaignStatusAggregation findCampaignStatusByCampaign( Campaign targetCampaign);

    Campaign findByCampaignName(Long companyId, String campaignName);
}
