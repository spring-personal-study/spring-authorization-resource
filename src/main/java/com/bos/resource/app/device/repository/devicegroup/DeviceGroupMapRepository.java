package com.bos.resource.app.device.repository.devicegroup;

import com.bos.resource.app.device.model.entity.Device;
import com.bos.resource.app.device.model.entity.DeviceGroupMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceGroupMapRepository extends JpaRepository<DeviceGroupMap, Long> {
    List<DeviceGroupMap> findByDevice(Device device);
}
