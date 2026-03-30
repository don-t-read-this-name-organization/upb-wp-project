package org.unimate.unimate.domain.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class TaskPriorityDeserializer extends JsonDeserializer<TaskPriority> {

    @Override
    public TaskPriority deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        int value = p.getIntValue();
        return TaskPriority.fromValue(value);
    }
}
