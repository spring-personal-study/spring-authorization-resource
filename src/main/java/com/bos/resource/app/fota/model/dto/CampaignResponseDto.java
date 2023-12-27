package com.bos.resource.app.fota.model.dto;

import com.bos.resource.app.common.domain.dto.Paging;
import com.bos.resource.app.common.domain.enums.UseType;
import com.bos.resource.app.fota.model.dto.Notifications.NotificationDetail;
import com.bos.resource.app.fota.model.dto.Notifications.NotificationDetail.NotificationDetailMetadata;
import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.Firmware;
import com.bos.resource.app.fota.model.enums.*;
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
                                    campaign.getAutoUpdateId(),
                                    campaign.getDeploymentTag(),
                                    campaign.getDeploymentId(),
                                    campaign.getDeploymentStatus(),
                                    campaign.getTotalDevices(),
                                    campaign.getCreated(),
                                    campaign.getScheduled(),
                                    campaign.getDownloading(),
                                    campaign.getAwaitingInstall(),
                                    campaign.getCompleted(),
                                    campaign.getCancelled(),
                                    campaign.getUnknown(),
                                    campaign.getCancelRequested(),
                                    new CampaignStatusContent.Failed(
                                            campaign.getFailedDownload(),
                                            campaign.getFailedInstall()
                                    ),
                                    campaign.getCompletedOn(),
                                    campaign.getCancelledOn()
                            ))
                            .collect(toList())
            );
        }

        @Getter
        @RequiredArgsConstructor
        public static class CampaignStatusContent {
            private final String autoUpdateId;
            private final String deploymentTag;
            private final String deploymentId;
            private final CampaignStatus deploymentStatus;
            private final Long totalDevices;
            private final Integer created;
            private final Integer scheduled;
            private final Integer downloading;
            private final Integer awaitingInstall;
            private final Integer completed;
            private final Integer cancelled;
            private final Integer unknown;
            private final UseType cancelRequested; // TODO: need to change to Boolean
            private final Failed failed;
            private final LocalDateTime completedOn;
            private final LocalDateTime cancelledOn;
            //private final List<String> associatedDeployments;

            @Getter
            @RequiredArgsConstructor
            public static class Failed {
                private final Integer download;
                private final Integer install;
            }
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

}
