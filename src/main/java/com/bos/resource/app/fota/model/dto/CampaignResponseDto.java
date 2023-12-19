package com.bos.resource.app.fota.model.dto;

import com.bos.resource.app.fota.model.entity.Firmware;
import org.springframework.data.domain.Page;

public class CampaignResponseDto {

    public static class CreatedNotification {

        public static CreatedNotification of(Page<Firmware> firmwares, CampaignRegistrationResult result) {
            return null;
        }
    }
}
