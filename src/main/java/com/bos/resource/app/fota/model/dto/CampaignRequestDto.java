package com.bos.resource.app.fota.model.dto;

import com.bos.resource.app.fota.model.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.List;

public class CampaignRequestDto {

    public record CreateCampaignDto(
            //Boolean simulate,
            CampaignProfile profile,
            //CampaignSchedule schedule,
            CampaignRule rules,
            CampaignDevice devices
    ) {
        public record CampaignProfile(
                String updateType,
                //String timeOffset,
                ProfileTarget target
                //List<CampaignMediaServer> mediaServer
        ) {
            public record ProfileTarget(
                    //String type,
                    TargetValue value
            ) {
                public record TargetValue(
                        String artifactName
                        //String bsp,
                        //String patch,
                        //String OSVersion
                ) {
                }
            }

           /* public record CampaignMediaServer(
                    String artifactUrl,
                    String authHeaderName,
                    String authHeaderValue
            ) {}*/
        }

       /* public record CampaignSchedule(
                String mode,
                String duration
        ) {}*/

        public record CampaignRule(
                //DownloadRule download,
                InstallRule install
                //BatteryRule battery
        ) {
            /*public record DownloadRule(
                    String startDate,
                    String network,
                    Integer autoUpdateDelay
            ) {}*/

            public record InstallRule(
                    String startDate,
                    String timeWindowStart,
                    String timeWindowEnd,
                    Boolean allowUserPostpone
                    //Integer postponeMaxDuration,
                    //String postponeMessage,
                    //String userMessage
            ) {
            }

            /*public record BatteryRule(
                    Integer level,
                    Boolean enforceOnCharger
            ) {}*/
        }

        public record CampaignDevice(
                String model,
                List<String> serial
        ) {
        }
    }

    public record CampaignStatus(
            //String deploymentTag,
            //List<String> deploymentId,
            String deploymentId,
            String status,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            Integer offset,
            Integer size
    ) {
        public CampaignStatus(String deploymentId, String status, LocalDateTime fromTime, LocalDateTime toTime, Integer offset, Integer size) {
            this.deploymentId = deploymentId;
            this.status = status;
            this.fromTime = fromTime;
            this.toTime = toTime;
            this.offset = offset == null ? 0 : offset;
            this.size = size == null ? 10 : size;
        }
    }

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

    public record CampaignStatusDetail(
            String deploymentId,
            Boolean appendStatus,
            Integer offset,
            Integer size
    ) {
        public CampaignStatusDetail(String deploymentId, Boolean appendStatus, Integer offset, Integer size) {
            this.deploymentId = deploymentId;
            this.appendStatus = appendStatus;
            this.offset = offset == null ? 0 : offset;
            this.size = size == null ? 10 : size;
        }
    }

    public record CancelCampaign(
            String deploymentId
    ) {
    }

    public record FOTAReadyDevice(
            String fotaReady,
            String detailLevel,
            Integer offset,
            Integer size
    ) {
        public FOTAReadyDevice(String fotaReady, String detailLevel, Integer offset, Integer size) {
            this.fotaReady = fotaReady;
            this.detailLevel = detailLevel;
            this.offset = offset == null ? 0 : offset;
            this.size = size == null ? 10 : size;
        }
    }

}
