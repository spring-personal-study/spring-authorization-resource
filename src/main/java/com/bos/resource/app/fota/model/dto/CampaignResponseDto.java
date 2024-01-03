package com.bos.resource.app.fota.model.dto;

import com.bos.resource.app.common.domain.dto.Paging;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.FoundCampaignStatus.CampaignStatusContent;
import com.bos.resource.app.fota.model.dto.Notifications.NotificationDetail;
import com.bos.resource.app.fota.model.dto.Notifications.NotificationDetail.NotificationDetailMetadata;
import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.CampaignDeviceMap;
import com.bos.resource.app.fota.model.entity.Firmware;
import com.bos.resource.app.fota.model.enums.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.bos.resource.app.fota.model.enums.DeviceWithCampaignFailureType.EXPIRED_WARRANTY;
import static com.bos.resource.app.fota.model.enums.DeviceWithCampaignFailureType.NOT_FOUND;
import static java.util.stream.Collectors.toList;

public class CampaignResponseDto {

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class CreatedCampaign {
        private final String deploymentId;
        private final String action;
        private final String message;
        private final String code;
        private final LocalDateTime timestamp;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class CreatedNotification {

        private final Paging head;
        private final List<Notifications> notifications;
        private final List<NotificationsError> notificationsErrors;

        public static CreatedNotification of(
                NotificationType notificationType,
                Page<Firmware> firmwares,
                Campaign savedCampaign,
                CampaignRegistrationResult result,
                Pageable pageable
        ) {

            Map<DeviceWithCampaignFailureType, List<String>> failToAddDevicesIntoCampaign = result.failToAddDevicesIntoCampaign();
            List<String> expiredWarranty = failToAddDevicesIntoCampaign.get(EXPIRED_WARRANTY);
            List<String> notFound = failToAddDevicesIntoCampaign.get(NOT_FOUND);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH:mm");
            LocalDateTime notificationTimestamp = LocalDateTime.parse(savedCampaign.getStartDate() + savedCampaign.getStartTime(), formatter);

            final List<NotificationsError.ErrorDetail> errorDetails = new ArrayList<>();
            final List<NotificationsError> notificationsErrors = new ArrayList<>();
            addErrors(notificationType, notificationsErrors, errorDetails, expiredWarranty, notFound);

            return CreatedNotification.builder()
                    .head(new Paging(
                            firmwares.getNumber(),
                            pageable.getOffset(),
                            pageable.getPageSize(),
                            firmwares.getNumberOfElements()))
                    .notifications(
                            firmwares.stream()
                                    .map(firmware -> Notifications.builder()
                                            .type(notificationType.name())
                                            .value(NotificationDetail.builder()
                                                    .model(firmware.getModel())
                                                    .artifactName(firmware.getVersion())
                                                    .artifactUrl(firmware.getUrl() + "/update.zip")
                                                    .notificationTimestamp(notificationTimestamp)
                                                    .optional(PackageType.FULL.equals(firmware.getPackageType()))
                                                    .metadata(NotificationDetailMetadata.builder()
                                                            .availableFrom(firmware.getCreateDt())
                                                            .type(firmware.getPackageType().name())
                                                            .build())
                                                    .build())
                                            .build())
                                    .collect(toList())
                    )
                    .notificationsErrors(notificationsErrors)
                    .build();
        }

        private static void addErrors(
                NotificationType notificationType,
                List<NotificationsError> notificationsErrors,
                List<NotificationsError.ErrorDetail> errorDetails,
                List<String> expiredWarranty,
                List<String> notFound
        ) {
            if (expiredWarranty != null && !expiredWarranty.isEmpty()) {
                errorDetails.add(
                        NotificationsError.ErrorDetail.builder()
                                .code(EXPIRED_WARRANTY.getErrorCode())
                                .value(expiredWarranty)
                                .message(EXPIRED_WARRANTY.getDetailMsg())
                                .build()
                );
            }
            if (notFound != null && !notFound.isEmpty()) {
                errorDetails.add(
                        NotificationsError.ErrorDetail.builder()
                                .code(NOT_FOUND.getErrorCode())
                                .value(notFound)
                                .message(NOT_FOUND.getDetailMsg())
                                .build()
                );
            }
            if (!errorDetails.isEmpty()) {
                notificationsErrors.add(new NotificationsError(notificationType.name(), errorDetails));
            }
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class FoundCampaignStatus {
        private final List<CampaignStatusContent> data;
        //private final Paging head;

        public static FoundCampaignStatus from(List<CampaignStatusAggregation> campaigns) {
            return new FoundCampaignStatus(
                    campaigns.stream()
                            .map(campaign -> new CampaignStatusContent(
                                    campaign.getDeploymentStatus(),
                                    campaign.getTotalDevices(),
                                    campaign.getScheduled(),
                                    campaign.getDownloading(),
                                    campaign.getAwaitingInstall(),
                                    campaign.getCompleted(),
                                    campaign.getFailed(),
                                    campaign.getCompletedOn()
                            ))
                            .collect(toList())
            );
        }

        @Getter
        @RequiredArgsConstructor
        public static class CampaignStatusContent {
            private final CampaignStatus deploymentStatus;
            private final Long totalDevices;
            private final Integer scheduled;
            private final Integer downloading;
            private final Integer awaitingInstall;
            private final Integer completed;
            private final Integer failed;
            private final LocalDateTime completedOn;
        }

    }

    @Getter
    @RequiredArgsConstructor
    public static class FoundCampaignStatusDetail {
        private final CampaignStatusDetailContent data;
        private final Paging head;

        public static FoundCampaignStatusDetail of(
                String deploymentId,
                CampaignStatusAggregation campaignStatusAggregation,
                Page<CampaignDeviceMap> campaignDevices,
                Pageable pageable
        ) {
            CampaignStatusContent campaignStatusContent = getCampaignStatusContent(campaignStatusAggregation);
            CampaignStatusDetailContent campaignStatusDetailContent = getCampaignStatusDetailContent(deploymentId, campaignStatusContent, campaignDevices.getContent());
            Paging paging = getPaging(campaignDevices, pageable);
            return new FoundCampaignStatusDetail(campaignStatusDetailContent, paging);
        }

        private static Paging getPaging(Page<CampaignDeviceMap> page, Pageable pageable) {
            return new Paging(
                    page.getNumber(),
                    pageable.getOffset(),
                    pageable.getPageSize(),
                    page.getNumberOfElements()
            );
        }

        private static CampaignStatusDetailContent getCampaignStatusDetailContent(
                String deploymentId,
                CampaignStatusContent campaignStatusContent,
                List<CampaignDeviceMap> devices
        ) {
            return CampaignStatusDetailContent.of(
                    deploymentId,
                    campaignStatusContent,
                    devices.stream()
                            .map(device -> new CampaignStatusDetailDevice(
                                    device.getDevice().getDeviceDetail().getModelName(),
                                    device.getDevice().getSerialNumber(),
                                    device.getStatus(),
                                    device.getResultMessage(),
                                    device.getUpdateDate()
                            ))
                            .collect(toList())
            );
        }

        private static CampaignStatusContent getCampaignStatusContent(CampaignStatusAggregation campaign) {
            CampaignStatusContent foundCampaignStatusDetail = null;
            if (campaign != null) {
                foundCampaignStatusDetail = new CampaignStatusContent(
                        campaign.getDeploymentStatus(),
                        campaign.getTotalDevices(),
                        campaign.getScheduled(),
                        campaign.getDownloading(),
                        campaign.getAwaitingInstall(),
                        campaign.getCompleted(),
                        campaign.getFailed(),
                        campaign.getCompletedOn()
                );
            }
            return foundCampaignStatusDetail;


        }

        @Getter
        @Builder
        @RequiredArgsConstructor
        public static class CampaignStatusDetailContent {
            private final String deploymentId;
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private final CampaignStatus deploymentStatus;
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private final Long totalDevices;
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private final Integer scheduled;
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private final Integer downloading;
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private final Integer awaitingInstall;
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private final Integer completed;
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private final Integer failed;
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private final LocalDateTime completedOn;
            private final List<CampaignStatusDetailDevice> devices;

            public static CampaignStatusDetailContent of(String deploymentId, CampaignStatusContent campaignStatusContent, List<CampaignStatusDetailDevice> detailDevices) {
                if (campaignStatusContent == null) {
                    return CampaignStatusDetailContent.builder()
                            .deploymentId(deploymentId)
                            .devices(detailDevices)
                            .build();
                }
                return CampaignStatusDetailContent.builder()
                        .deploymentId(deploymentId)
                        .deploymentStatus(campaignStatusContent.getDeploymentStatus())
                        .totalDevices(campaignStatusContent.getTotalDevices())
                        .scheduled(campaignStatusContent.getScheduled())
                        .downloading(campaignStatusContent.getDownloading())
                        .awaitingInstall(campaignStatusContent.getAwaitingInstall())
                        .completed(campaignStatusContent.getCompleted())
                        .failed(campaignStatusContent.getFailed())
                        .completedOn(campaignStatusContent.getCompletedOn())
                        .devices(detailDevices)
                        .build();
            }
        }

        @Getter
        @RequiredArgsConstructor
        public static class CampaignStatusDetailDevice {
            private final String model;
            private final String serialNumber;
            private final CampaignDeviceStatus status;
            private final String message;
            private final LocalDateTime completionTime;
        }
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class CancelledCampaign {
        private final Long deploymentId;
        private final String action;
        private final String message;
        private final String status;
        private final String code;
        private final LocalDateTime timestamp;
    }

    @Getter
    @RequiredArgsConstructor
    public static class FotaReadyDevice {
        private final Paging head;
        private final List<FOTAReadyDeviceContent> data;

        @Getter
        public static class FOTAReadyDeviceContent {
            private final String serialNumber;
            private final String model;
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private Long fotaReady;
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private LocalDateTime lastStatusReportTime;
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private String buildId;
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private String OSVersion;

            public FOTAReadyDeviceContent(String serialNumber, String model) {
                this.model = model;
                this.serialNumber = serialNumber;
            }

            public FOTAReadyDeviceContent(String serialNumber, String model, Long fotaReady, LocalDateTime lastStatusReportTime, String buildId, String OSVersion) {
                this.model = model;
                this.serialNumber = serialNumber;
                this.fotaReady = fotaReady;
                this.lastStatusReportTime = lastStatusReportTime;
                this.buildId = buildId;
                this.OSVersion = OSVersion;
            }
        }

        public static FotaReadyDevice of(Page<FOTAReadyDeviceContent> fotaReadyDevice) {
            return new FotaReadyDevice(
                    new Paging(
                            fotaReadyDevice.getNumber(),
                            fotaReadyDevice.getPageable().getOffset(),
                            fotaReadyDevice.getPageable().getPageSize(),
                            fotaReadyDevice.getNumberOfElements()
                    ),
                    fotaReadyDevice.getContent()
            );
        }
    }

}
