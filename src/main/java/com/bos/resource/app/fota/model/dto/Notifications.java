package com.bos.resource.app.fota.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor
public class Notifications {
    private final String type;
    private final NotificationDetail value;

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class NotificationDetail {
        private final String model;
        private final String artifactName;
        @JsonProperty("isOptional")
        private final boolean optional;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private final NotificationDetailMetadata metadata;

        @Getter
        @Builder
        @RequiredArgsConstructor
        public static class NotificationDetailMetadata {
            private final LocalDateTime availableFrom;
            private final String type;
        }
    }
}
