package com.bos.resource.app.fota.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@Builder
@RequiredArgsConstructor
public class NotificationsError {

    private final String type;
    private final List<ErrorDetail> value;

    @Getter
    public static class ErrorDetail {
        private final String code;
        private final List<String> value;
        private final String message;

        @Builder
        public ErrorDetail(String code, List<String> value, String message) {
            this.code = code;
            this.value = value;
            this.message = message;

        }
    }
}
