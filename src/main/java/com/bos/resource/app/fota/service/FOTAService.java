package com.bos.resource.app.fota.service;

import com.bos.resource.app.fota.exception.FOTACrudErrorCode;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.Notification;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.CancelledCampaign;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.CreatedNotification;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.FoundCampaignStatus;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.FoundCampaignStatusDetail;
import com.bos.resource.app.fota.model.dto.CampaignStatusAggregation;
import com.bos.resource.app.fota.model.entity.Campaign;
import com.bos.resource.app.fota.model.entity.CampaignDeviceMap;
import com.bos.resource.app.fota.repository.CampaignDeviceGroupMapRepository;
import com.bos.resource.app.fota.repository.CampaignDeviceTagMapRepository;
import com.bos.resource.app.fota.repository.CampaignPackageMapRepository;
import com.bos.resource.app.fota.repository.CampaignRepository;
import com.bos.resource.app.fota.repository.devicemap.CampaignDeviceMapRepository;
import com.bos.resource.app.resourceowner.ResourceOwnerService;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import com.bos.resource.exception.common.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
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
    private final ResourceOwnerService resourceOwnerService;

    public CreatedNotification processNotification(ResourceOwnerDto requestUser, Notification notification, Pageable pageable) {
        Notifier notifier = notificationProcessors.get(notification.notificationType());
        return notifier.createCampaign(requestUser, notification, pageable);
    }

    @Transactional(readOnly = false)
    public CampaignResponseDto createCampaign(ResourceOwnerDto resourceOwner, CampaignRequestDto.CreateCampaignDto createCampaignDto, Pageable pageable) {

        return null;
    }

    public FoundCampaignStatus getCampaignStatus(ResourceOwnerDto resourceOwner, CampaignRequestDto.CampaignStatus campaignStatus, Pageable pageable) {
        List<CampaignStatusAggregation> campaignStatusByCampaignIdAndBetweenDate = campaignRepository.findCampaignStatusByCampaignIdAndBetweenDate(
                campaignStatus.deploymentId(), campaignStatus.fromTime(), campaignStatus.toTime()
        );
        return FoundCampaignStatus.from(campaignStatusByCampaignIdAndBetweenDate);
    }

    public FoundCampaignStatusDetail getCampaignStatusDetail(ResourceOwnerDto resourceOwner, CampaignRequestDto.CampaignStatusDetail campaignStatus, Pageable pageable) {
        CampaignStatusAggregation campaignStatusAggregation = null;
        Campaign targetCampaign = campaignRepository.findByCampaignName(resourceOwner.getCompanyId(), campaignStatus.deploymentId());
        if (campaignStatus.appendStatus()) {
            campaignStatusAggregation = campaignRepository.findCampaignStatusByCampaign(targetCampaign);
        }
        if (targetCampaign == null) {
            throw new BizException(FOTACrudErrorCode.CAMPAIGN_NOT_FOUND);
        }
        Page<CampaignDeviceMap> campaignDevices = campaignDeviceMapRepository.findByCampaignDevices(targetCampaign, pageable);
        return FoundCampaignStatusDetail.of(campaignStatus.deploymentId(), campaignStatusAggregation, campaignDevices, pageable);
    }

    @Transactional(readOnly = false)
    public CancelledCampaign cancelCampaign(String resourceOwnerName, Long deploymentId) {
        Campaign campaign = campaignRepository.findById(deploymentId)
                .orElseThrow(() -> new BizException(FOTACrudErrorCode.CAMPAIGN_NOT_FOUND));

        ResourceOwnerDto resourceOwner = resourceOwnerService.findByResourceOwnerId(resourceOwnerName);

        boolean doesBelongToCompany = resourceOwner.getCompanyId().equals(campaign.getCompanyId());
        if (!doesBelongToCompany) {
            throw new BizException(FOTACrudErrorCode.ATTEMPTED_CANCEL_CAMPAIGN_WITH_NOT_VALID_USER);
        }

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
