package com.bos.resource.app.device.repository.device;

import com.bos.resource.app.device.model.entity.Device;
import com.bos.resource.app.device.repository.device.querydsl.QDeviceRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long>, QDeviceRepository {
    Device findBySerialNumber(String sn);
}
