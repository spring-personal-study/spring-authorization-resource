package com.bos.resource.unit.device;

import com.bos.resource.app.common.domain.enums.UseType;
import com.bos.resource.app.device.model.entity.Device;
import com.bos.resource.app.device.model.enums.DeviceStatus;
import com.bos.resource.app.device.repository.device.DeviceRepository;
import com.bos.resource.config.DatabaseConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application_db.yml")
@Import({DatabaseConfig.class})
@DisplayName("Device Entity")
public class DeviceEntityTest {

    @Autowired
    DeviceRepository deviceRepository;

    @Test
    @DisplayName("Create Device Entity - OK")
    void createDeviceEntity_Ok() {

        Device device = Device.builder()
                .id(1L)
                .serialNumber("1234567890")
                .deviceCode("1234567890")
                .deviceName("deviceName")
                .deviceDescription("deviceDescription")
                .status(DeviceStatus.CREATED)
                .enrolmentDate(LocalDateTime.now())
                .validWarranty(UseType.Y)
                .deviceDetail(null)
                .resourceOwner(null)
                .build();

        assertThat(device).isNotNull();
        assertThat(device.getId()).isEqualTo(1L);
        assertThat(device.getSerialNumber()).isEqualTo("1234567890");
        assertThat(device.getDeviceCode()).isEqualTo("1234567890");
        assertThat(device.getDeviceName()).isEqualTo("deviceName");
        assertThat(device.getDeviceDescription()).isEqualTo("deviceDescription");
        assertThat(device.getStatus()).isEqualTo(DeviceStatus.CREATED);
        assertThat(device.getEnrolmentDate()).isNotNull();
        assertThat(device.getValidWarranty()).isEqualTo(UseType.Y);
        assertThat(device.getDeviceDetail()).isNull();
        assertThat(device.getResourceOwner()).isNull();
    }

    @Test
    @DisplayName("Create Device Entity - OK")
    void findById() {
        Optional<Device> byId = deviceRepository.findById(1L);
        assertThat(byId).isNotNull();
        assertThat(byId.isPresent()).isTrue();
        assertThat(byId.get().getId()).isEqualTo(1L);
    }

}
