package com.bos.resource.integration.device;

import com.bos.resource.app.device.controller.DeviceController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.profiles.active=test")
public class DeviceControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Nested
    @DisplayName("/v1/asset test")
    class AssetTest {

        @Test
        @DisplayName("Test asset API - 200 OK")
        public void testAsset() throws Exception {
//            final String username = "dev_admin";
            final String username = "fake_user1";
            final String message = "success";
            final String model = "EF501";
            final String serialNumber = "HF550XANLCBA001";

            mockMvc.perform(get("/v1/asset")
                            .with(jwt()
                                    .jwt(jwt -> jwt.claim(StandardClaimNames.SUB, username)))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(handler().handlerType(DeviceController.class))
                    .andExpect(handler().methodName("asset"))
                    .andExpect(jsonPath("$.data").isNotEmpty())
                    .andExpect(jsonPath("$.data.message").value(message))
                    .andExpect(jsonPath("$.data.assets").isNotEmpty())
                    .andExpect(jsonPath("$.data.assets[0].model").value(model))
                    .andExpect(jsonPath("$.data.assets[0].serialNumber").value(serialNumber));
        }
    }
}
