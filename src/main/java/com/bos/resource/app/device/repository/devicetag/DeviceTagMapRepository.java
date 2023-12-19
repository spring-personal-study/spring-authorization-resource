package com.bos.resource.app.device.repository.devicetag;

import com.bos.resource.app.device.model.entity.Device;
import com.bos.resource.app.device.model.entity.DeviceTag;
import com.bos.resource.app.device.model.entity.DeviceTagMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceTagMapRepository extends JpaRepository<DeviceTagMap, Long> {
    List<DeviceTagMap> findByDevice(Device device);
}
