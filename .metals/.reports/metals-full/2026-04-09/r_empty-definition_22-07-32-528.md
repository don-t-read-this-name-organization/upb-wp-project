error id: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/config/WebConfig.java:org/springframework/beans/factory/annotation/Value#
file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/config/WebConfig.java
empty definition using pc, found symbol in pc: org/springframework/beans/factory/annotation/Value#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 91
uri: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/config/WebConfig.java
text:
```scala
package org.unimate.unimate.config;

import org.springframework.beans.factory.annotation.@@Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.path}")
    String uploadPath;

    @Value("${cors.allowed-origins:*}")
    String allowedOrigins;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = allowedOrigins.split(",");
        registry.addMapping("/api/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
        registry.addMapping("/uploads/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: org/springframework/beans/factory/annotation/Value#