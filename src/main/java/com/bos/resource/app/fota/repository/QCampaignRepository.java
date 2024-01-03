package com.bos.resource.app.fota.repository;

import com.bos.resource.app.fota.model.dto.CampaignStatusAggregation;
import com.bos.resource.app.fota.model.entity.Campaign;

import java.time.LocalDateTime;
import java.util.List;

public interface QCampaignRepository {
    Campaign findFirstByNameStartsWithAndCompanyIdOrderByNameDesc(String newDeploymentPrefix, Long companyId);

    List<CampaignStatusAggregation> findCampaignStatusByCompanyIdAndCampaignIdAndBetweenDate(
            Long companyId, String campaignId, LocalDateTime startDate, LocalDateTime endDate);

    CampaignStatusAggregation findCampaignStatusByCampaign( Campaign targetCampaign);

    Campaign findByCampaignName(Long companyId, String campaignName);
}
