package com.bos.resource.app.device.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
}
