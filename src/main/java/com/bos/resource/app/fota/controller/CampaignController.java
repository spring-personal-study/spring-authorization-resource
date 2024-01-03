package com.bos.resource.app.fota.controller;

import com.bos.resource.app.fota.exception.FOTACrudErrorCode;
import com.bos.resource.app.fota.exception.InvalidFOTAParameterException;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.CampaignStatus;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.CampaignStatusDetail;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.CreateCampaignDto;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.Notification;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.CreatedNotification;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.FoundCampaignStatus;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.FoundCampaignStatusDetail;
import com.bos.resource.app.fota.service.FOTAService;
import com.bos.resource.app.resourceowner.ResourceOwnerService;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class CampaignController {

    private final FOTAService fotaService;
    private final ResourceOwnerService resourceOwnerService;

    @Deprecated(since = "0.0.1, not supported in our service")
    @PostMapping("/notification")
    public CreatedNotification notification(
            @RequestBody
            Notification notification,
            Authentication authentication,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            throw new InvalidFOTAParameterException(result, FOTACrudErrorCode.FOTA_CRUD_FAIL);
        }
        ResourceOwnerDto resourceOwner = resourceOwnerService.findByResourceOwnerId(authentication.getName());
        return fotaService.processNotification(resourceOwner, notification);
    }

    @PostMapping("/deployments")
    public CampaignResponseDto.CreatedCampaign campaigns(
            @RequestBody
            CreateCampaignDto createCampaignDto,
            Authentication authentication,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            throw new InvalidFOTAParameterException(result, FOTACrudErrorCode.FOTA_CRUD_FAIL);
        }
        ResourceOwnerDto resourceOwner = resourceOwnerService.findByResourceOwnerId(authentication.getName());
        return fotaService.createCampaign(resourceOwner, createCampaignDto);
    }

    @PostMapping("/deployments/status")
    public FoundCampaignStatus campaignsStatus(
            @RequestBody
            CampaignStatus campaignStatus,
            Authentication authentication,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            throw new InvalidFOTAParameterException(result, FOTACrudErrorCode.FOTA_CRUD_FAIL);
        }
        ResourceOwnerDto resourceOwner = resourceOwnerService.findByResourceOwnerId(authentication.getName());
        return fotaService.getCampaignStatus(resourceOwner, campaignStatus);
    }

    @PostMapping("/deployments/detail")
    public FoundCampaignStatusDetail campaignsStatusDetail(
            @RequestBody
            CampaignStatusDetail campaignStatus,
            Authentication authentication,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            throw new InvalidFOTAParameterException(result, FOTACrudErrorCode.FOTA_CRUD_FAIL);
        }
        ResourceOwnerDto resourceOwner = resourceOwnerService.findByResourceOwnerId(authentication.getName());
        return fotaService.getCampaignStatusDetail(resourceOwner, campaignStatus);
    }

    @PostMapping("/deployments/cancel")
    public CampaignResponseDto.CancelledCampaign cancelCampaign(
            @RequestBody(required = true)
            CampaignRequestDto.CancelCampaign cancelCampaign,
            Authentication authentication
    ) {
        Long deploymentIdLong = Long.parseLong(cancelCampaign.deploymentId());
        return fotaService.cancelCampaign(authentication.getName(), deploymentIdLong);
    }

    @PostMapping("/devices")
    public CampaignResponseDto.FotaReadyDevice campaignDevice(
            @RequestBody(required = true)
            CampaignRequestDto.FOTAReadyDevice campaignDevice,
            Authentication authentication
    ) {
        ResourceOwnerDto resourceOwner = resourceOwnerService.findByResourceOwnerId(authentication.getName());
        return fotaService.getFOTAReadyDevice(resourceOwner.getCompanyId(), campaignDevice);
    }
}
