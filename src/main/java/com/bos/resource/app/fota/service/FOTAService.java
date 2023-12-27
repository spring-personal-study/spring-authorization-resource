package com.bos.resource.app.fota.service;

import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.Notification;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.CancelledCampaign;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.CreatedNotification;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.FoundCampaignStatus;
import com.bos.resource.app.fota.model.dto.CampaignStatusAggregation;
import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.CampaignDeviceTagMap;
import com.bos.resource.app.fota.repository.*;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FOTAService {

    private final Map<String, Notifier> notificationProcessors;
    private final CampaignRepository campaignRepository;
    private final CampaignDeviceMapRepository campaignDeviceMapRepository;
    private final CampaignPackageMapRepository campaignPackageMapRepository;
    private final CampaignDeviceGroupMapRepository campaignDeviceGroupMapRepository;
    private final CampaignDeviceTagMapRepository campaignDeviceTagMapRepository;

    public CreatedNotification processNotification(ResourceOwnerDto requestUser, Notification notification, Pageable pageable) {
        Notifier notifier = notificationProcessors.get(notification.notificationType());
        return notifier.createCampaign(requestUser, notification, pageable);
    }

    public FoundCampaignStatus getCampaignStatus(ResourceOwnerDto resourceOwner, CampaignRequestDto.CampaignStatus campaignStatus, Pageable pageable) {
        List<CampaignStatusAggregation> campaignStatusByCampaignIdAndBetweenDate = campaignRepository.findCampaignStatusByCampaignIdAndBetweenDate(
                campaignStatus.deploymentId().get(0), campaignStatus.fromTime(), campaignStatus.toTime()
        );
        return FoundCampaignStatus.from(campaignStatusByCampaignIdAndBetweenDate);
    }

    public CampaignResponseDto createCampaign(ResourceOwnerDto resourceOwner, CampaignRequestDto.CreateCampaignDto createCampaignDto, Pageable pageable) {
        return null;
    }

    @Transactional
    public CancelledCampaign cancelCampaign(Long deploymentId) {
        Campaign campaign = campaignRepository.findById(deploymentId)
                .orElseThrow(() -> new RuntimeException("not found campaign"));
        campaignDeviceMapRepository.deleteByCampaign(campaign);
        campaignPackageMapRepository.deleteByCampaign(campaign);
        campaignDeviceGroupMapRepository.deleteByCampaign(campaign);
        campaignDeviceTagMapRepository.deleteByCampaign(campaign);
        campaignDeviceMapRepository.flush();
        campaignPackageMapRepository.flush();
        campaignDeviceGroupMapRepository.flush();
        campaignDeviceTagMapRepository.flush();
        campaignRepository.deleteById(deploymentId);

        return CancelledCampaign.builder()
                .deploymentId(deploymentId)
                .action("canceled")
                .message("cancel deployment success")
                .status("success")
                .code("200")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
