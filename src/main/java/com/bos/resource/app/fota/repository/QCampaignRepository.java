package com.bos.resource.app.fota.repository;

import com.bos.resource.app.fota.model.entity.Campaign;

public interface QCampaignRepository {
    Campaign findFirstByNameStartsWithAndCompanyIdOrderByNameDesc(String newDeploymentPrefix, Long companyId);
}
