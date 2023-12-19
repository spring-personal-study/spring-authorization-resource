package com.bos.resource.app.common.domain.enums;

import lombok.ToString;

@ToString
public enum UseType {
    Y, N;
    public  Boolean useTypeToBoolean(){
        return this.equals(UseType.Y);
    }
}
