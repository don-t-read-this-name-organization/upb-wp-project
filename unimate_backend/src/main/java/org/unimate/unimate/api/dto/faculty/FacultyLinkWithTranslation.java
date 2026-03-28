package org.unimate.unimate.api.dto.faculty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.unimate.unimate.domain.entities.FacultyLink;
import org.unimate.unimate.domain.entities.FacultyLinkTranslation;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@Builder
public class FacultyLinkWithTranslation {
    Integer id;
    String key;
    String title;
    String description;
    String url;
    String icon;
    String colorClass;

    public static FacultyLinkWithTranslation fromEntity(FacultyLink link, FacultyLinkTranslation translation) {
        return FacultyLinkWithTranslation.builder()
                .id(link.getId())
                .key(translation != null && translation.getTitle() != null 
                    ? translation.getTitle().toLowerCase().replace(" ", "-") 
                    : "")
                .title(translation != null ? translation.getTitle() : "")
                .description(translation != null ? translation.getDescription() : null)
                .url(link.getUrl())
                .icon(link.getIcon())
                .colorClass(link.getColorClass())
                .build();
    }
}
