package com.bos.resource.app.fota.model.dto;

import com.bos.resource.app.fota.model.enums.NotificationType;

import java.util.List;

public class CampaignRequestDto {

    public record Notification(
            String notificationType,
            Integer offset,
            Integer limit,
            NotificationParams params
    ) {

        public Notification(String notificationType, Integer offset, Integer limit, NotificationParams params) {
            this.notificationType = NotificationType.findType(notificationType);
            this.offset = offset;
            this.limit = limit;
            this.params = params;
        }

        public record NotificationParams(
                String model,
                List<String> serialNumbers,
                String targetBuild,
                String subType
        ) {
            public NotificationParams(String model, List<String> serialNumbers, String targetBuild, String subType) {
                this.model = model;
                this.serialNumbers = serialNumbers;
                this.targetBuild = targetBuild;
                this.subType = subType == null ? "GMS" : subType;
            }
        }
    }
}
