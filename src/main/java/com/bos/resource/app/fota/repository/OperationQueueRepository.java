package com.bos.resource.app.fota.repository;

import com.bos.resource.app.device.model.entity.Device;
import com.bos.resource.app.fota.model.entity.OperationQueue;
import com.bos.resource.app.fota.model.constants.enums.OpCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperationQueueRepository extends JpaRepository<OperationQueue, Long> {
    Optional<OperationQueue> findByOpCodeAndDevice(OpCode opCode, Device device);
}