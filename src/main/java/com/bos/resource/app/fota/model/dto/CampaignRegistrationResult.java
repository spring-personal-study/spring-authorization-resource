package com.bos.resource.app.fota.model.dto;

import com.bos.resource.app.fota.model.constants.enums.DeviceWithCampaignFailureType;

import java.util.List;
import java.util.Map;

public record CampaignRegistrationResult(
        List<String> successToAddDevicesIntoCampaign,
        Map<DeviceWithCampaignFailureType, List<String>> failToAddDevicesIntoCampaign
) {
}