package org.unimate.unimate.api.dto.notification.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.unimate.unimate.domain.entities.Notification;

import java.time.LocalDateTime;

public record NotificationResponse(
    @JsonProperty("id") Integer id,
    @JsonProperty("type") String type,
    @JsonProperty("title") String title,
    @JsonProperty("message") String message,
    @JsonProperty("relatedEntityType") String relatedEntityType,
    @JsonProperty("relatedEntityId") Integer relatedEntityId,
    @JsonProperty("read") Boolean read,
    @JsonProperty("createdAt") LocalDateTime createdAt
) {
  public static NotificationResponse fromEntity(Notification notification) {
    return new NotificationResponse(
        notification.getId(),
        notification.getType(),
        notification.getTitle(),
        notification.getMessage(),
        notification.getRelatedEntityType(),
        notification.getRelatedEntityId(),
        notification.getRead(),
        notification.getCreatedAt()
    );
  }
}
