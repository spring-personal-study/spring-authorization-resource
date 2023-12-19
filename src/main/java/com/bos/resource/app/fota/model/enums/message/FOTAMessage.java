package com.bos.resource.app.fota.model.enums.message;

import com.bos.resource.app.common.apiresponse.ApiSuccessMessage;
import com.bos.resource.app.common.apiresponse.SuccessMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FOTAMessage implements SuccessMessage {
    INQUIRY_FOTA_SUCCESS(ApiSuccessMessage.INQUIRY_FOTA_SUCCESS),
    ;

    private final String successMessage;

    @Override
    public String getSuccessMessage() {
        return successMessage;
    }
}
