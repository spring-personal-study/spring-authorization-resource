package com.bos.resource.app.device.model.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "TB_DEVICE_GROUP_MAP")
public class DeviceGroupMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEVICE_GROUP_MAP_ID")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICE_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICE_GRP_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private DeviceGroup deviceGroup;

    @Builder
    public DeviceGroupMap(Long id, Device device, DeviceGroup deviceGroup) {
        this.id = id;
        this.device = device;
        this.deviceGroup = deviceGroup;
    }
}
