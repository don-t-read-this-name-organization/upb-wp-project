package org.unimate.unimate.api.dto.notification.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NotificationRequest(
    @JsonProperty("type") String type,
    @JsonProperty("title") String title,
    @JsonProperty("message") String message,
    @JsonProperty("relatedEntityType") String relatedEntityType,
    @JsonProperty("relatedEntityId") Integer relatedEntityId
) {
}
