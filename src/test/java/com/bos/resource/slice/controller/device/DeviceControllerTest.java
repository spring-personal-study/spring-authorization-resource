package com.bos.resource.slice.controller.device;

import com.bos.resource.app.device.controller.DeviceController;
import com.bos.resource.app.device.model.dto.DeviceResponseDto;
import com.bos.resource.app.device.model.dto.DeviceResponseDto.DeviceDto;
import com.bos.resource.app.device.service.ApiService;
import com.bos.resource.app.resourceowner.ResourceOwnerService;
import com.bos.resource.app.resourceowner.exception.ResourceOwnerErrorCode;
import com.bos.resource.exception.common.BizException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Device Controller")
@AutoConfigureMockMvc
@WebMvcTest(controllers = DeviceController.class)
public class DeviceControllerTest {

    @MockBean
    ApiService apiService;

    @MockBean
    ResourceOwnerService resourceOwnerService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper jsonConverter;

    @Nested
    @DisplayName("/v1/asset")
    public class AssetTest {

        @Test
        @DisplayName("Request from an authorized user - 200")
        void givenRequestIsAuthorized_whenGetAsset_thenOk() throws Exception {
            final String username = "resource_owner_id";
            final String message = "success";
            final String model = "EF501";
            final String serialNumber = "EF501XXXXXXXX1";

            DeviceDto deviceDto = new DeviceDto(model, serialNumber);
            DeviceResponseDto deviceResponseDto = new DeviceResponseDto(message, HttpStatus.OK.value(), List.of(deviceDto));
            when(apiService.findAssetByEnrolledUser(username)).thenReturn(deviceResponseDto);

            mockMvc.perform(get("/v1/asset")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username)))
                    )
                    .andExpect(status().isOk())
                    .andExpect(handler().handlerType(DeviceController.class))
                    .andExpect(handler().methodName("asset"))
                    .andExpect(jsonPath("$.data").isNotEmpty())
                    .andExpect(jsonPath("$.data.message").value(message))
                    .andExpect(jsonPath("$.data.assets").isNotEmpty())
                    .andExpect(jsonPath("$.data.assets[0].model").value(model))
                    .andExpect(jsonPath("$.data.assets[0].serialNumber").value(serialNumber));

            verify(apiService).findAssetByEnrolledUser(username);
        }

        @Test
        @DisplayName("Request from an unauthorized user - 401")
        void givenRequestIsAnonymous_whenGetAsset_thenUnauthorized() throws Exception {
            mockMvc.perform(get("/v1/asset").with(anonymous()))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Request from an authorized user, but cannot found in database - 404")
        void givenRequestIsAuthorized_whenTryToFindUserInfo_thenNotFound() throws Exception {
            final String username = "resource_owner_id";
            when(apiService.findAssetByEnrolledUser(username)).thenThrow(new BizException(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND));

            mockMvc.perform(get("/v1/asset")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username)))
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(handler().handlerType(DeviceController.class))
                    .andExpect(handler().methodName("asset"))
                    .andExpect(jsonPath("$.msg").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.internalCode").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getBizCode()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());

            verify(apiService, atMostOnce()).findAssetByEnrolledUser(username);
        }
    }


}
