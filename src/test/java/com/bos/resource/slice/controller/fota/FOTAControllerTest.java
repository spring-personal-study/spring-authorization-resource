package com.bos.resource.slice.controller.fota;

import com.bos.resource.app.common.apiresponse.ApiSuccessMessage;
import com.bos.resource.app.common.domain.dto.Paging;
import com.bos.resource.app.fota.controller.CampaignController;
import com.bos.resource.app.fota.exception.FOTACrudErrorCode;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.CreateCampaignDto.CampaignDevice;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.CreateCampaignDto.CampaignProfile;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.CreateCampaignDto.CampaignProfile.ProfileTarget;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.CreateCampaignDto.CampaignProfile.ProfileTarget.TargetValue;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.CreateCampaignDto.CampaignRule;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto.CreateCampaignDto.CampaignRule.InstallRule;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.FotaReadyDevice.FOTAReadyDeviceContent;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.FotaReadyDevice.FOTAReadyDeviceWrapper;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto.FoundCampaignStatus.CampaignStatusContent;
import com.bos.resource.app.fota.model.constants.enums.CampaignStatus;
import com.bos.resource.app.fota.service.FOTAService;
import com.bos.resource.app.resourceowner.ResourceOwnerService;
import com.bos.resource.app.resourceowner.exception.ResourceOwnerErrorCode;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import com.bos.resource.app.resourceowner.model.entity.ResourceOwner;
import com.bos.resource.exception.common.BizException;
import com.bos.resource.exception.common.GeneralErrorMessage;
import com.bos.resource.exception.common.GeneralParameterErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.bos.resource.app.common.apiresponse.ApiSuccessMessage.CANCEL_DEPLOYMENT_SUCCESS;
import static java.time.LocalDateTime.now;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("FOTA Controller")
@AutoConfigureMockMvc
@WebMvcTest(controllers = CampaignController.class)
public class FOTAControllerTest {

    @MockBean
    FOTAService fotaService;

    @MockBean
    ResourceOwnerService resourceOwnerService;

    @Autowired
    private ObjectMapper jsonConverter;

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("/v1/devices")
    public class FOTADeviceTest {

        final Long companyId = 1L;
        final String fotaReady = "0", deviceLevel = "1";
        final String resourceOwnerId = "fake_name";

        CampaignResponseDto.FotaReadyDevice responseBody = new CampaignResponseDto.FotaReadyDevice(
                new Paging(0, 10, 10, 100),
                new FOTAReadyDeviceWrapper(
                        List.of(
                        new FOTAReadyDeviceContent("EF501_SN1", "EF501", 1, now(), "20991231", "10")
                ))
        );

        private final ResourceOwnerDto resourceOwnerDto = new ResourceOwnerDto(
                ResourceOwner.builder()
                        .id(1L)
                        .resourceOwnerId(resourceOwnerId)
                        .email("fake_email")
                        .companyId(companyId)
                        .build()
        );

        @Test
        @DisplayName("200 OK")
        void givenUserIsAuthenticated_whenGetDevices_thenOk() throws Exception {
            CampaignRequestDto.FOTAReadyDevice requestBody = new CampaignRequestDto.FOTAReadyDevice(fotaReady, deviceLevel, null, null);

            when(resourceOwnerService.findByResourceOwnerId(anyString())).thenReturn(resourceOwnerDto);
            when(fotaService.getFOTAReadyDevice(companyId, requestBody)).thenReturn(responseBody);

            mockMvc.perform(post("/v1/devices")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, resourceOwnerId)))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"));

            verify(fotaService, atMostOnce()).getFOTAReadyDevice(companyId, requestBody);
            verify(resourceOwnerService, atMostOnce()).findByResourceOwnerId(resourceOwnerId);
        }

        @Test
        @DisplayName("401 Unauthorized - Missing Token")
        void givenRequestWithoutToken_whenGetDevices_thenUnauthorized() throws Exception {
            CampaignRequestDto.FOTAReadyDevice requestBody = new CampaignRequestDto.FOTAReadyDevice(fotaReady, deviceLevel, null, null);

            when(resourceOwnerService.findByResourceOwnerId(anyString())).thenReturn(resourceOwnerDto);
            when(fotaService.getFOTAReadyDevice(companyId, requestBody)).thenReturn(responseBody);

            mockMvc.perform(post("/v1/devices")
                            //.with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, resourceOwnerId)))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                    )
                    .andExpect(status().isUnauthorized());

            verify(resourceOwnerService, never()).findByResourceOwnerId(resourceOwnerId);
            verify(fotaService, never()).getFOTAReadyDevice(companyId, requestBody);
        }

        @Test
        @DisplayName("401 Unauthorized - Invalid Token Format (Not JWT)")
        void givenUserIsUnauthorizedWithInvalidTokenFormat_whenGetDevices_thenUnauthorized() throws Exception {

            CampaignRequestDto.FOTAReadyDevice requestBody = new CampaignRequestDto.FOTAReadyDevice(fotaReady, deviceLevel, null, null);

            when(resourceOwnerService.findByResourceOwnerId(anyString())).thenReturn(resourceOwnerDto);
            when(fotaService.getFOTAReadyDevice(companyId, requestBody)).thenReturn(responseBody);

            mockMvc.perform(post("/v1/devices")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer invalid_token")
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                    )
                    .andExpect(status().isUnauthorized());

            verify(resourceOwnerService, never()).findByResourceOwnerId(resourceOwnerId);
            verify(fotaService, never()).getFOTAReadyDevice(companyId, requestBody);
        }

        @Test
        @DisplayName("401 Unauthorized - Invalid Token Header - different alg (expected RS256, but found HS256) and missed key (kid).")
        void givenUserIsUnauthorizedWithInvalidTokenHeader_whenGetDevices_thenUnauthorized() throws Exception {
            CampaignRequestDto.FOTAReadyDevice requestBody = new CampaignRequestDto.FOTAReadyDevice(fotaReady, deviceLevel, null, null);

            when(resourceOwnerService.findByResourceOwnerId(anyString())).thenReturn(resourceOwnerDto);
            when(fotaService.getFOTAReadyDevice(companyId, requestBody)).thenReturn(responseBody);

            mockMvc.perform(post("/v1/devices")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                    )
                    .andExpect(status().isUnauthorized());

            verify(resourceOwnerService, never()).findByResourceOwnerId(resourceOwnerId);
            verify(fotaService, never()).getFOTAReadyDevice(companyId, requestBody);
        }

        @Test
        @DisplayName("400 Bad Request - all missed request body")
        void givenInsufficientRequestBodyAll_whenGetDevices_thenBadRequest() throws Exception {
            CampaignRequestDto.FOTAReadyDevice requestBody = new CampaignRequestDto.FOTAReadyDevice(null, null, null, null);

            when(resourceOwnerService.findByResourceOwnerId(anyString())).thenReturn(resourceOwnerDto);
            when(fotaService.getFOTAReadyDevice(companyId, requestBody)).thenReturn(responseBody);

            mockMvc.perform(post("/v1/devices")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, resourceOwnerId)))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.msg").value(GeneralParameterErrorCode.INVALID_PARAMETER.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(FOTACrudErrorCode.FOTA_CRUD_FAIL.getHttpStatus().value()))
                    .andExpect(jsonPath("$.internalCode").value(FOTACrudErrorCode.FOTA_CRUD_FAIL.getBizCode()))
                    .andExpect(jsonPath("$.detailErrors[*].reason", containsInAnyOrder(
                            FOTACrudErrorCode.FOTA_READY_IS_NULL.getMsg(),
                            FOTACrudErrorCode.FOTA_READY_IS_EMPTY.getMsg(),
                            FOTACrudErrorCode.DETAIL_LEVEL_IS_NULL.getMsg(),
                            FOTACrudErrorCode.DETAIL_LEVEL_IS_EMPTY.getMsg()
                    )))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());

            verify(resourceOwnerService, never()).findByResourceOwnerId(resourceOwnerId);
            verify(fotaService, never()).getFOTAReadyDevice(companyId, requestBody);
        }

        @Test
        @DisplayName("400 Bad Request - missed fotaReady in request body")
        void givenInsufficientRequestBodyFotaReady_whenGetDevices_thenBadRequest() throws Exception {
            CampaignRequestDto.FOTAReadyDevice requestBody = new CampaignRequestDto.FOTAReadyDevice(null, "1", null, null);

            when(resourceOwnerService.findByResourceOwnerId(anyString())).thenReturn(resourceOwnerDto);
            when(fotaService.getFOTAReadyDevice(companyId, requestBody)).thenReturn(responseBody);

            mockMvc.perform(post("/v1/devices")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, resourceOwnerId)))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.msg").value(GeneralParameterErrorCode.INVALID_PARAMETER.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(FOTACrudErrorCode.FOTA_CRUD_FAIL.getHttpStatus().value()))
                    .andExpect(jsonPath("$.internalCode").value(FOTACrudErrorCode.FOTA_CRUD_FAIL.getBizCode()))
                    .andExpect(jsonPath("$.detailErrors[*].reason", containsInAnyOrder(
                            FOTACrudErrorCode.FOTA_READY_IS_NULL.getMsg(),
                            FOTACrudErrorCode.FOTA_READY_IS_EMPTY.getMsg()
                    )))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());

            verify(resourceOwnerService, never()).findByResourceOwnerId(resourceOwnerId);
            verify(fotaService, never()).getFOTAReadyDevice(companyId, requestBody);
        }

        @Test
        @DisplayName("400 Bad Request - empty fotaReady in request body")
        void givenInsufficientRequestBodyEmptyFotaReady_whenGetDevices_thenBadRequest() throws Exception {
            CampaignRequestDto.FOTAReadyDevice requestBody = new CampaignRequestDto.FOTAReadyDevice("", "1", null, null);

            when(resourceOwnerService.findByResourceOwnerId(anyString())).thenReturn(resourceOwnerDto);
            when(fotaService.getFOTAReadyDevice(companyId, requestBody)).thenReturn(responseBody);

            mockMvc.perform(post("/v1/devices")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, resourceOwnerId)))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.msg").value(GeneralParameterErrorCode.INVALID_PARAMETER.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(FOTACrudErrorCode.FOTA_CRUD_FAIL.getHttpStatus().value()))
                    .andExpect(jsonPath("$.internalCode").value(FOTACrudErrorCode.FOTA_CRUD_FAIL.getBizCode()))
                    .andExpect(jsonPath("$.detailErrors[0].reason").value(FOTACrudErrorCode.FOTA_READY_IS_EMPTY.getMsg()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());

            verify(resourceOwnerService, never()).findByResourceOwnerId(resourceOwnerId);
            verify(fotaService, never()).getFOTAReadyDevice(companyId, requestBody);
        }

        @Test
        @DisplayName("400 Bad Request - missed detailLevel in request body")
        void givenInsufficientRequestBodyDetailLevel_whenGetDevices_thenBadRequest() throws Exception {
            CampaignRequestDto.FOTAReadyDevice requestBody = new CampaignRequestDto.FOTAReadyDevice("1", null, null, null);

            when(resourceOwnerService.findByResourceOwnerId(anyString())).thenReturn(resourceOwnerDto);
            when(fotaService.getFOTAReadyDevice(companyId, requestBody)).thenReturn(responseBody);

            mockMvc.perform(post("/v1/devices")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, resourceOwnerId)))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.msg").value(GeneralParameterErrorCode.INVALID_PARAMETER.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(FOTACrudErrorCode.FOTA_CRUD_FAIL.getHttpStatus().value()))
                    .andExpect(jsonPath("$.internalCode").value(FOTACrudErrorCode.FOTA_CRUD_FAIL.getBizCode()))
                    .andExpect(jsonPath("$.detailErrors[*].reason", containsInAnyOrder(
                            FOTACrudErrorCode.DETAIL_LEVEL_IS_NULL.getMsg(),
                            FOTACrudErrorCode.DETAIL_LEVEL_IS_EMPTY.getMsg()
                    )))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());

            verify(resourceOwnerService, never()).findByResourceOwnerId(resourceOwnerId);
            verify(fotaService, never()).getFOTAReadyDevice(companyId, requestBody);
        }

        @Test
        @DisplayName("400 Bad Request - empty detailLevel in request body")
        void givenInsufficientRequestBodyEmptyDetailLevel_whenGetDevices_thenBadRequest() throws Exception {
            CampaignRequestDto.FOTAReadyDevice requestBody = new CampaignRequestDto.FOTAReadyDevice(null, "", null, null);

            when(resourceOwnerService.findByResourceOwnerId(anyString())).thenThrow(new BizException(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND));
            when(fotaService.getFOTAReadyDevice(companyId, requestBody)).thenReturn(responseBody);

            mockMvc.perform(post("/v1/devices")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, resourceOwnerId)))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.msg").value(GeneralParameterErrorCode.INVALID_PARAMETER.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(FOTACrudErrorCode.FOTA_CRUD_FAIL.getHttpStatus().value()))
                    .andExpect(jsonPath("$.internalCode").value(FOTACrudErrorCode.FOTA_CRUD_FAIL.getBizCode()))
                    .andExpect(jsonPath("$.detailErrors[*].reason", containsInAnyOrder(
                            FOTACrudErrorCode.FOTA_READY_IS_NULL.getMsg(),
                            FOTACrudErrorCode.FOTA_READY_IS_EMPTY.getMsg(),
                            FOTACrudErrorCode.DETAIL_LEVEL_IS_EMPTY.getMsg()
                    )))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());

            verify(resourceOwnerService, atMostOnce()).findByResourceOwnerId(resourceOwnerId);
            verify(fotaService, never()).getFOTAReadyDevice(companyId, requestBody);
        }

        @Test
        @DisplayName("Request from an authorized user, but cannot found in database - 404")
        void givenRequestIsAuthorized_whenTryToFindUserInfo_thenNotFound() throws Exception {
            CampaignRequestDto.FOTAReadyDevice requestBody = new CampaignRequestDto.FOTAReadyDevice("1", "0", null, null);

            when(resourceOwnerService.findByResourceOwnerId(anyString())).thenThrow(new BizException(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND));

            mockMvc.perform(post("/v1/devices")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, resourceOwnerId)))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.msg").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.internalCode").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getBizCode()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());

            verify(resourceOwnerService, atMostOnce()).findByResourceOwnerId(anyString());
        }

    }

    @Nested
    @DisplayName("/v1/deployments/**")
    public class DeploymentsTest {

        final Long companyId = 1L;
        final String resourceOwnerId = "fake_name";

        private final ResourceOwnerDto resourceOwnerDto = new ResourceOwnerDto(
                ResourceOwner.builder()
                        .id(1L)
                        .resourceOwnerId(resourceOwnerId)
                        .email("fake_email")
                        .companyId(companyId)
                        .build()
        );

        private final CampaignResponseDto.CreatedCampaign responseBody = new CampaignResponseDto.CreatedCampaign(
                "FOTA-1",
                HttpStatus.CREATED.name(),
                ApiSuccessMessage.CREATE_DEPLOYMENT_SUCCESS,
                String.valueOf(HttpStatus.OK.value()),
                now()
        );

        private final CampaignProfile profile = new CampaignProfile("LATEST", new ProfileTarget(new TargetValue("20991231")));
        private final CampaignRule rule = new CampaignRule(new InstallRule("19700101", "00:00", "23:59", false));
        private final CampaignDevice device = new CampaignDevice("EF501", List.of("EF501_SN1", "EF501_SN2"));

        @Test
        @DisplayName("/deployments 401 Unauthorized")
        void givenUserIsUnauthorized_whenRegisterNewCampaign_thenUnauthorized() throws Exception {
            CampaignRequestDto.CreateCampaignDto requestBody = new CampaignRequestDto.CreateCampaignDto(profile, rule, device);

            when(resourceOwnerService.findByResourceOwnerId(resourceOwnerId)).thenReturn(resourceOwnerDto);
            when(fotaService.createCampaign(any(), any())).thenReturn(responseBody);

            mockMvc.perform(post("/v1/deployments")
                            //.with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, "username")))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                            .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isUnauthorized());

            verify(resourceOwnerService, never()).findByResourceOwnerId(resourceOwnerId);
            verify(fotaService, never()).createCampaign(any(), any());
        }

        @Test
        @DisplayName("/deployments 200 OK")
        void givenNewCampaignInfo_whenRegisterNewCampaign_thenOK() throws Exception {
            CampaignRequestDto.CreateCampaignDto requestBody = new CampaignRequestDto.CreateCampaignDto(profile, rule, device);

            when(resourceOwnerService.findByResourceOwnerId(resourceOwnerId)).thenReturn(resourceOwnerDto);
            when(fotaService.createCampaign(any(), any())).thenReturn(responseBody);

            mockMvc.perform(post("/v1/deployments")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, "username")))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                            .contentType(APPLICATION_JSON)
                    )
                    //.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"));

            verify(resourceOwnerService, atMostOnce()).findByResourceOwnerId(resourceOwnerId);
            verify(fotaService, atMostOnce()).createCampaign(any(), any());
        }

        @Test
        @DisplayName("/deployments 400 Bad Request - All missed request body")
        void givenInsufficientRequestBody_whenRegisterNewCampaign_thenBadRequest() throws Exception {
            //CampaignRequestDto.CreateCampaignDto requestBody = new CampaignRequestDto.CreateCampaignDto(profile, rule, device);

            when(resourceOwnerService.findByResourceOwnerId(resourceOwnerId)).thenReturn(resourceOwnerDto);
            when(fotaService.createCampaign(any(), any())).thenReturn(responseBody);

            mockMvc.perform(post("/v1/deployments")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, "username")))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            //.content(jsonConverter.writeValueAsString(requestBody))
                            .contentType(APPLICATION_JSON)
                    )
                    //.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.msg").value(GeneralErrorMessage.ALL_PARAMETER_IS_NULL))
                    .andExpect(jsonPath("$.httpCode").value(FOTACrudErrorCode.FOTA_CRUD_FAIL.getHttpStatus().value()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());

            verify(resourceOwnerService, never()).findByResourceOwnerId(resourceOwnerId);
            verify(fotaService, never()).createCampaign(any(), any());
        }

        @Test
        @DisplayName("/deployments 400 Bad Request - missed campaign profile")
        void givenInsufficientRequestBodyProfile_whenRegisterNewCampaign_thenBadRequest() throws Exception {
            CampaignRequestDto.CreateCampaignDto requestBody = new CampaignRequestDto.CreateCampaignDto(null, null, null);

            when(resourceOwnerService.findByResourceOwnerId(resourceOwnerId)).thenReturn(resourceOwnerDto);
            when(fotaService.createCampaign(any(), any())).thenReturn(responseBody);

            mockMvc.perform(post("/v1/deployments")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, "username")))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                            .contentType(APPLICATION_JSON)
                    )
                    //.andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.msg").value(GeneralErrorMessage.INVALID_PARAMETER))
                    .andExpect(jsonPath("$.httpCode").value(FOTACrudErrorCode.FOTA_CRUD_FAIL.getHttpStatus().value()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());

            verify(resourceOwnerService, never()).findByResourceOwnerId(resourceOwnerId);
            verify(fotaService, never()).createCampaign(any(), any());
        }

        @Test
        @DisplayName("/deployments 404 Not Found - User")
        void givenUserIsAuthorizedButNotFound_whenRegisterNewCampaign_thenNotFoundUser() throws Exception {
            CampaignRequestDto.CreateCampaignDto requestBody = new CampaignRequestDto.CreateCampaignDto(profile, rule, device);

            when(resourceOwnerService.findByResourceOwnerId(anyString())).thenThrow(new BizException(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND));
            when(fotaService.createCampaign(any(), any())).thenReturn(responseBody);

            mockMvc.perform(post("/v1/deployments")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, "username")))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                            .contentType(APPLICATION_JSON)
                    )
                    //.andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.msg").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getHttpStatus().value()))
                    .andExpect(jsonPath("$.internalCode").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getBizCode()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());

            verify(resourceOwnerService, atMostOnce()).findByResourceOwnerId(anyString());
            verify(fotaService, never()).createCampaign(any(), any());
        }

        @Test
        @DisplayName("/deployments/status 200 OK")
        void givenUserIsAuthorized_whenGetCampaignStatus_thenOk() throws Exception {
            CampaignRequestDto.CampaignStatus requestBody = new CampaignRequestDto.CampaignStatus(List.of("FOTA-1"), null, null, null);
            CampaignStatusContent content1 = new CampaignStatusContent("FOTA-1", CampaignStatus.ACTIVE, 23L, 5, 3, 4, 5, 6, now());
            CampaignStatusContent content2 = new CampaignStatusContent("FOTA-1", CampaignStatus.ACTIVE, 10L, 1, 2, 3, 4, 5, now());

            CampaignResponseDto.FoundCampaignStatus responseBody = new CampaignResponseDto.FoundCampaignStatus(List.of(content1, content2));

            when(resourceOwnerService.findByResourceOwnerId(resourceOwnerId)).thenReturn(resourceOwnerDto);
            when(fotaService.getCampaignStatus(any(), any())).thenReturn(responseBody);

            mockMvc.perform(post("/v1/deployments/status")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, "username")))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                            .contentType(APPLICATION_JSON)
                    )
                    //.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.data", hasSize(2)))
                    .andExpect(jsonPath("$.data[*]", everyItem(hasKey("deploymentStatus"))))
                    .andExpect(jsonPath("$.data[*]", everyItem(hasKey("totalDevices"))))
                    .andExpect(jsonPath("$.data[*]", everyItem(hasKey("scheduled"))))
                    .andExpect(jsonPath("$.data[*]", everyItem(hasKey("downloading"))))
                    .andExpect(jsonPath("$.data[*]", everyItem(hasKey("awaitingInstall"))))
                    .andExpect(jsonPath("$.data[*]", everyItem(hasKey("completed"))))
                    .andExpect(jsonPath("$.data[*]", everyItem(hasKey("failed"))))
                    .andExpect(jsonPath("$.data[*]", everyItem(hasKey("completedOn"))))
                    .andExpect(jsonPath("$.data[*].totalDevices", contains(23, 10)));


            verify(resourceOwnerService, atMostOnce()).findByResourceOwnerId(anyString());
            verify(fotaService, atMostOnce()).getCampaignStatus(any(), any());

        }

        @Test
        @DisplayName("/deployments/status 400 Bad Request - Missed deploymentId")
        void givenInsufficientRequestBodyDeploymentId_whenGetCampaignStatus_thenBadRequest() throws Exception {
            CampaignRequestDto.CampaignStatus requestBody = new CampaignRequestDto.CampaignStatus(null, null, null, null);
            CampaignStatusContent content1 = new CampaignStatusContent("FOTA-1", CampaignStatus.ACTIVE, 23L, 5, 3, 4, 5, 6, now());
            CampaignStatusContent content2 = new CampaignStatusContent("FOTA-1", CampaignStatus.ACTIVE, 10L, 1, 2, 3, 4, 5, now());

            CampaignResponseDto.FoundCampaignStatus responseBody = new CampaignResponseDto.FoundCampaignStatus(List.of(content1, content2));

            when(resourceOwnerService.findByResourceOwnerId(resourceOwnerId)).thenReturn(resourceOwnerDto);
            when(fotaService.getCampaignStatus(any(), any())).thenReturn(responseBody);

            mockMvc.perform(post("/v1/deployments/status")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, "username")))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                            .contentType(APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.msg").value(GeneralErrorMessage.INVALID_PARAMETER))
                    .andExpect(jsonPath("$.httpCode").value(FOTACrudErrorCode.FOTA_CRUD_FAIL.getHttpStatus().value()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty())
                    .andExpect(jsonPath("$.detailErrors[*].reason", containsInAnyOrder(
                            FOTACrudErrorCode.DEPLOYMENT_ID_IS_NULL.getMsg(),
                            FOTACrudErrorCode.DEPLOYMENT_ID_IS_EMPTY.getMsg()
                    )));

            verify(resourceOwnerService, never()).findByResourceOwnerId(anyString());
            verify(fotaService, never()).getCampaignStatus(any(), any());
        }

        @Test
        @DisplayName("/deployments/detail 200 OK")
        void givenUserIsAuthorized_whenGetCampaignDetail_thenOk() throws Exception {
            CampaignRequestDto.CampaignStatusDetail requestBody = new CampaignRequestDto.CampaignStatusDetail("FOTA-1", true, null, null);
            CampaignResponseDto.FoundCampaignStatusDetail responseBody = new CampaignResponseDto.FoundCampaignStatusDetail(
                    new CampaignResponseDto.FoundCampaignStatusDetail.CampaignStatusDetailContent(
                            "FOTA-1",
                            CampaignStatus.ACTIVE,
                            23L, 5, 3, 4, 5, 6, now(), null),
                    new Paging(0, 0, 10, 100));

            when(resourceOwnerService.findByResourceOwnerId(resourceOwnerId)).thenReturn(resourceOwnerDto);
            when(fotaService.getCampaignStatusDetail(any(), any())).thenReturn(responseBody);

            mockMvc.perform(post("/v1/deployments/detail")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, "username")))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                            .contentType(APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.data", hasKey("deploymentId")))
                    .andExpect(jsonPath("$.data", hasKey("deploymentStatus")))
                    .andExpect(jsonPath("$.data", hasKey("totalDevices")))
                    .andExpect(jsonPath("$.data", hasKey("scheduled")))
                    .andExpect(jsonPath("$.data", hasKey("downloading")))
                    .andExpect(jsonPath("$.data", hasKey("awaitingInstall")))
                    .andExpect(jsonPath("$.data", hasKey("completed")))
                    .andExpect(jsonPath("$.data", hasKey("failed")))
                    .andExpect(jsonPath("$.data", hasKey("completedOn")))
                    .andExpect(jsonPath("$.data.devices").doesNotHaveJsonPath())
                    .andExpect(jsonPath("$.head", hasKey("page")))
                    .andExpect(jsonPath("$.head", hasKey("offset")))
                    .andExpect(jsonPath("$.head", hasKey("limit")))
                    .andExpect(jsonPath("$.head", hasKey("total")))
                    .andExpect(jsonPath("$.data.deploymentId").value("FOTA-1"));
        }


        @Test
        @DisplayName("/deployments/detail 400 Bad Request")
        void givenMissedDeploymentId_whenGetCampaignDetail_thenBadRequest() throws Exception {
            CampaignRequestDto.CampaignStatusDetail requestBody = new CampaignRequestDto.CampaignStatusDetail(null, true, null, null);
            CampaignResponseDto.FoundCampaignStatusDetail responseBody = new CampaignResponseDto.FoundCampaignStatusDetail(
                    new CampaignResponseDto.FoundCampaignStatusDetail.CampaignStatusDetailContent(
                            "FOTA-1",
                            CampaignStatus.ACTIVE,
                            23L, 5, 3, 4, 5, 6, now(), null),
                    new Paging(0, 0, 10, 100));

            when(resourceOwnerService.findByResourceOwnerId(resourceOwnerId)).thenReturn(resourceOwnerDto);
            when(fotaService.getCampaignStatusDetail(any(), any())).thenReturn(responseBody);

            mockMvc.perform(post("/v1/deployments/detail")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, "username")))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                            .contentType(APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.msg").value(GeneralErrorMessage.INVALID_PARAMETER))
                    .andExpect(jsonPath("$.httpCode").value(FOTACrudErrorCode.FOTA_CRUD_FAIL.getHttpStatus().value()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty())
                    .andExpect(jsonPath("$.detailErrors[*].reason", containsInAnyOrder(
                            FOTACrudErrorCode.DEPLOYMENT_ID_IS_NULL.getMsg(),
                            FOTACrudErrorCode.DEPLOYMENT_ID_IS_EMPTY.getMsg()
                    )));

            verify(resourceOwnerService, never()).findByResourceOwnerId(anyString());
            verify(fotaService, never()).getCampaignStatusDetail(any(), any());
        }

        @Test
        @DisplayName("/deployments/detail 404 Not Found")
        void givenUserIsAuthorizedButNotFound_whenGetDeviceDetail_thenNotFound() throws Exception {

            CampaignRequestDto.CampaignStatusDetail requestBody = new CampaignRequestDto.CampaignStatusDetail("FOTA-1", true, null, null);
            CampaignResponseDto.FoundCampaignStatusDetail responseBody = new CampaignResponseDto.FoundCampaignStatusDetail(
                    new CampaignResponseDto.FoundCampaignStatusDetail.CampaignStatusDetailContent(
                            "FOTA-1",
                            CampaignStatus.ACTIVE,
                            23L, 5, 3, 4, 5, 6, now(), null),
                    new Paging(0, 0, 10, 100));

            when(resourceOwnerService.findByResourceOwnerId(anyString())).thenThrow(new BizException(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND));
            when(fotaService.getCampaignStatusDetail(any(), any())).thenReturn(responseBody);

            mockMvc.perform(post("/v1/deployments/detail")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, "username")))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                            .contentType(APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.msg").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getMsg()))
                    .andExpect(jsonPath("$.httpCode").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getHttpStatus().value()))
                    .andExpect(jsonPath("$.internalCode").value(ResourceOwnerErrorCode.RESOURCE_OWNER_NOT_FOUND.getBizCode()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());

            verify(resourceOwnerService, atMostOnce()).findByResourceOwnerId(anyString());
            verify(fotaService, never()).getCampaignStatusDetail(any(), any());


        }

        @Test
        @DisplayName("/deployments/cancel 200 OK")
        void givenUserIsAuthorized_whenAskCampaignCancel_thenOk() throws Exception {

            CampaignRequestDto.CancelCampaign requestBody = new CampaignRequestDto.CancelCampaign("FOTA-1");
            CampaignResponseDto.CancelledCampaign responseBody = new CampaignResponseDto.CancelledCampaign("FOTA-1", "canceled", CANCEL_DEPLOYMENT_SUCCESS, HttpStatus.OK.name(), "success", now());

            when(resourceOwnerService.findByResourceOwnerId(resourceOwnerId)).thenReturn(resourceOwnerDto);
            when(fotaService.cancelCampaign(any(), any())).thenReturn(responseBody);

            mockMvc.perform(post("/v1/deployments/cancel")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, "username")))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                            .contentType(APPLICATION_JSON)
                    )
                    //.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.deploymentId").value("FOTA-1"))
                    .andExpect(jsonPath("$.action").value("canceled"))
                    .andExpect(jsonPath("$.message").value(CANCEL_DEPLOYMENT_SUCCESS))
                    .andExpect(jsonPath("$.status").value(HttpStatus.OK.name()))
                    .andExpect(jsonPath("$.code").value("success"))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());

            verify(resourceOwnerService, atMostOnce()).findByResourceOwnerId(anyString());
            verify(fotaService, atMostOnce()).cancelCampaign(any(), any());

        }

        @Test
        @DisplayName("/deployments/cancel 400 Bad Request")
        void givenMissingDeploymentId_whenAskCampaignCancel_thenBadRequest() throws Exception {
            CampaignRequestDto.CancelCampaign requestBody = new CampaignRequestDto.CancelCampaign(null);
            CampaignResponseDto.CancelledCampaign responseBody = new CampaignResponseDto.CancelledCampaign("FOTA-1", "canceled", CANCEL_DEPLOYMENT_SUCCESS, HttpStatus.OK.name(), "success", now());

            when(resourceOwnerService.findByResourceOwnerId(resourceOwnerId)).thenReturn(resourceOwnerDto);
            when(fotaService.cancelCampaign(any(), any())).thenReturn(responseBody);

            mockMvc.perform(post("/v1/deployments/cancel")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, "username")))
                            .with(csrf())
                            .accept(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                            .contentType(APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.msg").value(GeneralErrorMessage.INVALID_PARAMETER))
                    .andExpect(jsonPath("$.httpCode").value(FOTACrudErrorCode.FOTA_CRUD_FAIL.getHttpStatus().value()))
                    .andExpect(jsonPath("$.timestamp").isNotEmpty());

            verify(resourceOwnerService, never()).findByResourceOwnerId(anyString());
            verify(fotaService, never()).cancelCampaign(any(), any());
        }
    }

}
