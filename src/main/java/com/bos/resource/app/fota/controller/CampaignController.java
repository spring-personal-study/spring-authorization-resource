package com.bos.resource.app.fota.controller;

import com.bos.resource.app.fota.model.dto.CampaignRequestDto.Notification;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.CreatedNotification;
import com.bos.resource.app.fota.service.FOTAService;
import com.bos.resource.app.resourceowner.ResourceOwnerService;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
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

    @PostMapping("/notification")
    public CreatedNotification notification(@RequestBody Notification notification, @PageableDefault(page = 0, size = 10) Pageable pageable, Authentication authentication) {
        ResourceOwnerDto resourceOwner = resourceOwnerService.findByResourceOwnerId(authentication.getName());
        return fotaService.processNotification(resourceOwner, notification, pageable);
    }

}
