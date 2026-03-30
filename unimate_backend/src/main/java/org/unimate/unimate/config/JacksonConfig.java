package org.unimate.unimate.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.unimate.unimate.domain.enums.TaskPriority;
import org.unimate.unimate.domain.enums.TaskPriorityDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public SimpleModule taskPriorityModule() {
        SimpleModule module = new SimpleModule("TaskPriorityModule");
        module.addDeserializer(TaskPriority.class, new TaskPriorityDeserializer());
        return module;
    }
}
