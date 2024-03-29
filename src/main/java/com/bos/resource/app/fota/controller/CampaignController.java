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
import com.bos.resource.exception.common.BizException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class CampaignController {

    private final FOTAService fotaService;
    private final ResourceOwnerService resourceOwnerService;

    @PostMapping("/notification")
    public CreatedNotification notification(
            JwtAuthenticationToken authentication,
            @Valid @RequestBody
            Notification notification,
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
            JwtAuthenticationToken authentication,
            @Valid @RequestBody
            CreateCampaignDto createCampaignDto,
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
            JwtAuthenticationToken authentication,
            @Valid @RequestBody
            CampaignStatus campaignStatus,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            throw new InvalidFOTAParameterException(result, FOTACrudErrorCode.FOTA_CRUD_FAIL);
        }
        if (checkDurationExceeded(campaignStatus.fromTime(), campaignStatus.toTime())) {
            throw new BizException(FOTACrudErrorCode.DATE_RANGE_EXCEEDED);
        }
        ResourceOwnerDto resourceOwner = resourceOwnerService.findByResourceOwnerId(authentication.getName());
        return fotaService.getCampaignStatus(resourceOwner, campaignStatus);
    }

    private boolean checkDurationExceeded(LocalDateTime fromTime, LocalDateTime toTime) {
        Duration duration = Duration.between(fromTime, toTime);
        long daysDifference = duration.toDays();
        return daysDifference > 90;
    }

    @PostMapping("/deployments/detail")
    public FoundCampaignStatusDetail campaignsStatusDetail(
            JwtAuthenticationToken authentication,
            @Valid @RequestBody
            CampaignStatusDetail campaignStatus,
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
            JwtAuthenticationToken authentication,
            @Valid @RequestBody(required = true)
            CampaignRequestDto.CancelCampaign cancelCampaign,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            throw new InvalidFOTAParameterException(result, FOTACrudErrorCode.FOTA_CRUD_FAIL);
        }
        return fotaService.cancelCampaign(authentication.getName(), cancelCampaign.deploymentId());
    }

    @PostMapping("/devices")
    public CampaignResponseDto.FotaReadyDevice campaignDevice(
            JwtAuthenticationToken authentication,
            @Valid @RequestBody
            CampaignRequestDto.FOTAReadyDevice campaignDevice,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            throw new InvalidFOTAParameterException(result, FOTACrudErrorCode.FOTA_CRUD_FAIL);
        }
        ResourceOwnerDto resourceOwner = resourceOwnerService.findByResourceOwnerId(authentication.getName());
        return fotaService.getFOTAReadyDevice(resourceOwner.getCompanyId(), campaignDevice);
    }
}
