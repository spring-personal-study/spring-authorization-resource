package com.bos.resource.app.device.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "TB_DEVICE_TAG_MAP")
public class DeviceTagMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEVICE_TAG_MAP_ID")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICE_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICE_TAG_ID", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private DeviceTag deviceTag;
}
