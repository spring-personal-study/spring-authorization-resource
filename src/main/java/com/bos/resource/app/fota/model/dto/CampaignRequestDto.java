package com.bos.resource.app.fota.model.dto;

import com.bos.resource.app.fota.model.enums.NotificationType;
import com.bos.resource.exception.common.ApiErrorMessage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class CampaignRequestDto {

    public record CreateCampaignDto(
            //Boolean simulate,
            @Valid
            CampaignProfile profile,
            //CampaignSchedule schedule,
            @Valid
            CampaignRule rules,
            @Valid
            CampaignDevice devices
    ) {
        public record CampaignProfile(
                @NotNull(message = ApiErrorMessage.UPDATE_TYPE_IS_NULL)
                @NotEmpty(message = ApiErrorMessage.UPDATE_TYPE_IS_EMPTY)
                String updateType,
                //String timeOffset,
                @Valid
                ProfileTarget target
                //List<CampaignMediaServer> mediaServer
        ) {
            public record ProfileTarget(
                    //String type,
                    @Valid
                    TargetValue value
            ) {
                public record TargetValue(
                        @NotNull(message = ApiErrorMessage.ARTIFACT_NAME_IS_NULL)
                        @NotEmpty(message = ApiErrorMessage.ARTIFACT_NAME_IS_EMPTY)
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
                @Valid
                InstallRule install
                //BatteryRule battery
        ) {
            /*public record DownloadRule(
                    String startDate,
                    String network,
                    Integer autoUpdateDelay
            ) {}*/

            public record InstallRule(
                    @NotNull(message = ApiErrorMessage.START_DATE_IS_NULL)
                    @NotEmpty(message = ApiErrorMessage.START_DATE_IS_EMPTY)
                    String startDate,
                    @NotNull(message = ApiErrorMessage.TIME_WINDOW_START_IS_NULL)
                    @NotEmpty(message = ApiErrorMessage.TIME_WINDOW_START_IS_EMPTY)
                    String timeWindowStart,
                    @NotNull(message = ApiErrorMessage.TIME_WINDOW_END_IS_NULL)
                    @NotEmpty(message = ApiErrorMessage.TIME_WINDOW_END_IS_EMPTY)
                    String timeWindowEnd,
                    @NotNull(message = ApiErrorMessage.ALLOW_USER_POSTPONE_IS_NULL)
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
                @NotNull(message = ApiErrorMessage.MODEL_IS_NULL)
                String model,
                @NotNull(message = ApiErrorMessage.SERIAL_IS_NULL)
                @NotEmpty(message = ApiErrorMessage.SERIAL_IS_EMPTY)
                List<String> serial
        ) {
        }
    }

    public record CampaignStatus(
            //String deploymentTag,
            //List<String> deploymentId,
            @NotNull(message = ApiErrorMessage.DEPLOYMENT_ID_IS_NULL)
            @NotEmpty(message = ApiErrorMessage.DEPLOYMENT_ID_IS_EMPTY)
            String deploymentId,
            @NotNull(message = ApiErrorMessage.STATUS_IS_NULL)
            @NotEmpty(message = ApiErrorMessage.STATUS_IS_EMPTY)
            String status,
            LocalDateTime fromTime,
            LocalDateTime toTime,
            Integer offset,
            Integer size
    ) {
        public CampaignStatus(String deploymentId, String status, LocalDateTime fromTime, LocalDateTime toTime, Integer offset, Integer size) {
            this.deploymentId = deploymentId;
            this.status = status;
            this.fromTime = fromTime == null ? LocalDateTime.now().minusDays(1) : fromTime;
            this.toTime = toTime == null ? LocalDateTime.now().minusDays(90) : toTime;
            this.offset = offset == null ? 0 : offset;
            this.size = size == null ? 10 : size;
        }
    }

    public record Notification(
            @NotNull(message = ApiErrorMessage.NOTIFICATION_TYPE_IS_NULL)
            @NotEmpty(message = ApiErrorMessage.NOTIFICATION_TYPE_IS_EMPTY)
            String notificationType,
            NotificationParams params,
            Integer offset,
            Integer limit
    ) {

        public Notification(@NotNull(message = ApiErrorMessage.NOTIFICATION_TYPE_IS_NULL)
                            String notificationType, NotificationParams params, Integer offset, Integer limit) {
            this.notificationType = NotificationType.findType(notificationType);
            this.params = params;
            this.offset = offset;
            this.limit = limit;
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
            @NotNull(message = ApiErrorMessage.DEPLOYMENT_ID_IS_NULL)
            @NotEmpty(message = ApiErrorMessage.DEPLOYMENT_ID_IS_EMPTY)
            String deploymentId,
            @NotNull(message = ApiErrorMessage.APPEND_STATUS_IS_NULL)
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
            @NotNull(message = ApiErrorMessage.DEPLOYMENT_ID_IS_NULL)
            String deploymentId
    ) {
    }

    public record FOTAReadyDevice(
            @NotNull(message = ApiErrorMessage.FOTA_READY_IS_NULL)
            @NotEmpty(message = ApiErrorMessage.FOTA_READY_IS_EMPTY)
            String fotaReady,
            @NotNull(message = ApiErrorMessage.DETAIL_LEVEL_IS_NULL)
            @NotEmpty(message = ApiErrorMessage.DETAIL_LEVEL_IS_EMPTY)
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
