package com.bos.resource.slice.controller.fota;

import com.bos.resource.app.fota.controller.CampaignController;
import com.bos.resource.app.fota.model.dto.CampaignRequestDto;
import com.bos.resource.app.fota.model.dto.CampaignResponseDto;
import com.bos.resource.app.fota.service.FOTAService;
import com.bos.resource.app.resourceowner.ResourceOwnerService;
import com.bos.resource.app.resourceowner.model.dto.ResourceOwnerDto;
import com.bos.resource.app.resourceowner.model.entity.ResourceOwner;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.time.Instant;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("FOTA Controller")
@AutoConfigureMockMvc
@WebMvcTest(controllers = CampaignController.class)
public class FOTAControllerTest {

    @MockBean
    FOTAService fotaService;

    @MockBean
    ResourceOwnerService resourceOwnerService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper jsonConverter;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setup() {
        this.webTestClient = MockMvcWebTestClient
                .bindTo(mockMvc)
                .build();
    }

    @Nested
    @DisplayName("/v1/devices")
    public class FOTADeviceTest {

        final Long companyId = 1L;
        final String fotaReady = "0", deviceLevel = "1";
        final String resourceOwnerId = "fake_name";

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
            CampaignResponseDto.FotaReadyDevice responseBody = new CampaignResponseDto.FotaReadyDevice(null, emptyList());
            ResourceOwnerDto resourceOwnerDto = new ResourceOwnerDto(
                    ResourceOwner.builder()
                            .id(1L)
                            .resourceOwnerId(resourceOwnerId)
                            .email("fake_email")
                            .companyId(companyId)
                            .build()
            );

            when(resourceOwnerService.findByResourceOwnerId(anyString())).thenReturn(resourceOwnerDto);
            when(fotaService.getFOTAReadyDevice(companyId, requestBody)).thenReturn(responseBody);

           /* CampaignRequestDto.FOTAReadyDevice requestBody = new CampaignRequestDto.FOTAReadyDevice(fotaReady, deviceLevel, null, null);
            CampaignResponseDto.FotaReadyDevice responseBody = new CampaignResponseDto.FotaReadyDevice(null, emptyList());
            ResourceOwnerDto resourceOwnerDto = new ResourceOwnerDto(
                    ResourceOwner.builder()
                            .id(1L)
                            .resourceOwnerId(resourceOwnerId)
                            .email("fake_email")
                            .companyId(companyId)
                            .build()
            );

            when(resourceOwnerService.findByResourceOwnerId(anyString())).thenReturn(resourceOwnerDto);
            when(fotaService.getFOTAReadyDevice(companyId, requestBody)).thenReturn(responseBody);

            mockMvc.perform(post("/v1/devices")
                            .with(new JK(jwt -> jwt.claim(StandardClaimNames.SUB, resourceOwnerId)))
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"));

            verify(fotaService, atMostOnce()).getFOTAReadyDevice(companyId, requestBody);
            verify(resourceOwnerService, atMostOnce()).findByResourceOwnerId(resourceOwnerId);*/
        }

        @Test
        @DisplayName("401 Unauthorized - Invalid Token")
        void givenUserIsUnauthorizedWithInvalidToken_whenGetDevices_thenUnauthorized() throws Exception {
            CampaignRequestDto.FOTAReadyDevice requestBody = new CampaignRequestDto.FOTAReadyDevice(fotaReady, deviceLevel, null, null);
            when(resourceOwnerService.findByResourceOwnerId(anyString())).thenReturn(resourceOwnerDto);
            when(fotaService.getFOTAReadyDevice(companyId, requestBody)).thenReturn(null);


            mockMvc.perform(post("/v1/devices")
                            .with(jwt().jwt(jwt -> jwt.tokenValue("invalid_token")))
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                    )
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json"));

            verify(resourceOwnerService, atMostOnce()).findByResourceOwnerId(resourceOwnerId);
            verify(fotaService, never()).getFOTAReadyDevice(companyId, null);
        }


        @Test
        @DisplayName("401 Unauthorized - Expired Token")
        void givenUserIsUnauthorizedWithExpiredToken_whenGetDevices_thenUnauthorized() throws Exception {
            final Long companyId = 1L;
            final String resourceOwnerId = "fake_name";
            final String fotaReady = "0", deviceLevel = "1";

            CampaignRequestDto.FOTAReadyDevice requestBody = new CampaignRequestDto.FOTAReadyDevice(fotaReady, deviceLevel, null, null);

            when(resourceOwnerService.findByResourceOwnerId(anyString())).thenReturn(resourceOwnerDto);

            mockMvc.perform(post("/v1/devices")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, resourceOwnerId).expiresAt(Instant.MIN)))
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                    )
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json"));

        }

        // hold
        @Test
        @Disabled
        @DisplayName("400 Bad Request - all missed request body")
        void givenInsufficient_whenGetDevices_thenBadRequest() throws Exception {
            final String resourceOwnerId = "username";

            CampaignRequestDto.FOTAReadyDevice requestBody = new CampaignRequestDto.FOTAReadyDevice(null, null, null, null);

            mockMvc.perform(post("/v1/devices")
                            .with(jwt().jwt(jwt -> jwt.claim(StandardClaimNames.SUB, resourceOwnerId)))
                            .accept(APPLICATION_JSON)
                            .contentType(APPLICATION_JSON)
                            .content(jsonConverter.writeValueAsString(requestBody))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType("application/json"));

        }

    }

    @Nested
    @DisplayName("/v1/deployments/**")
    public class DeploymentsTest {

        @Test
        @DisplayName("/deployments 200 OK - LATEST")
        void givenNewCampaignInfo_whenRegisterNewCampaignWithLatestFirmware_thenOK() throws Exception {

        }

        @Test
        @DisplayName("/deployments 401 Unauthorized")
        void givenUserIsUnauthorized_whenRegisterNewCampaignWithLatestFirmware_thenUnauthorized() throws Exception {

        }

        @Test
        @DisplayName("/deployments 400 Bad Request - LATEST")
        void givenInsufficientRequestBody_whenRegisterNewCampaignWithLatestFirmware_thenBadRequest() throws Exception {

        }

        @Test
        @DisplayName("/deployments 200 OK - CUSTOM")
        void givenNewCampaignInfo_whenRegisterNewCampaignWithSpecificVersionFirmware_thenOK() throws Exception {

        }

        @Test
        @DisplayName("/deployments 400 Bad Request - LATEST")
        void givenInsufficientRequestBody_whenRegisterNewCampaignWithSpecificVersionFirmware_thenBadRequest() throws Exception {

        }

        @Test
        @DisplayName("/deployments/status 200 OK")
        void givenUserIsAuthorized_whenGetCampaignStatus_thenOk() throws Exception {

        }

        @Test
        @DisplayName("/deployments/detail 200 OK")
        void givenUserIsAuthorized_whenGetCampaignDetail_thenOk() throws Exception {

        }

        @Test
        @DisplayName("/deployments/cancel 200 OK")
        void givenUserIsAuthorized_whenAskCampaignCancel_thenOk() throws Exception {

        }


    }

}
