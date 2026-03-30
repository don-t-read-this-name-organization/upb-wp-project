package org.unimate.unimate.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TaskPriority {
    LOW(1),
    MEDIUM(2),
    HIGH(3);

    private final int value;

    TaskPriority(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    @JsonCreator
    public static TaskPriority fromValue(int value) {
        for (TaskPriority priority : TaskPriority.values()) {
            if (priority.value == value) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Unknown TaskPriority value: " + value);
    }
}
