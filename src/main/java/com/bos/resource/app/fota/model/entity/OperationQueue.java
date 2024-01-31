package com.bos.resource.app.fota.model.entity;

import com.bos.resource.app.device.model.entity.Device;
import com.bos.resource.app.fota.model.enums.OpCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "TB_OPERATION_QUEUE")
public class OperationQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "OP_CODE")
    private OpCode opCode;

    @Column(name = "PAYLOAD")
    private String payLoad;

    @Column(name = "UPDATE_DT")
    @CreationTimestamp
    private LocalDateTime updateDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICE_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Device device;

    @Builder
    public OperationQueue(Long id, Device device, OpCode opCode, String payLoad, LocalDateTime updateDateTime) {
        this.id = id;
        this.device = device;
        this.opCode = opCode;
        this.payLoad = payLoad;
        this.updateDateTime = updateDateTime;
    }
}
