package com.bos.resource.app.device.controller;

import com.bos.resource.app.common.apiresponse.ResponseDto;
import com.bos.resource.app.device.model.dto.DeviceResponseDto;
import com.bos.resource.app.device.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class DeviceController {

    private final ApiService apiService;

/*    @GetMapping("/authentication/info")
    public Authentication index(Authentication authentication) {
        return authentication;
    }*/

    @GetMapping("/asset")
    public ResponseDto<DeviceResponseDto> asset(Authentication authentication) {
        String username = authentication.getName();
        DeviceResponseDto assetByEnrolledUser = apiService.findAssetByEnrolledUser(username);
        return new ResponseDto<>(assetByEnrolledUser);
    }
}
