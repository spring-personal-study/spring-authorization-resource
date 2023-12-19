package com.bos.resource.app.fota.model.dto;

import java.util.List;

public record CampaignRegistrationResult(
        List<String> successToAddDevicesIntoCampaign,
        List<String> expiredWarranty,
        List<String> notFound
) {
}