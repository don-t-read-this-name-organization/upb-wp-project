package org.unimate.unimate.domain.enums;

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

}
