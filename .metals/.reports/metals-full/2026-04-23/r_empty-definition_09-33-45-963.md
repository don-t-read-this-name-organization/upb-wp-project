error id: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/domain/entities/Subtask.java:org/hibernate/annotations/UpdateTimestamp#
file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/domain/entities/Subtask.java
empty definition using pc, found symbol in pc: org/hibernate/annotations/UpdateTimestamp#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 226
uri: file:///F:/uni/projects/upb-wp-project/unimate_backend/src/main/java/org/unimate/unimate/domain/entities/Subtask.java
text:
```scala
package org.unimate.unimate.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.@@UpdateTimestamp;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "subtasks")
@FieldDefaults(level = PRIVATE)
public class Subtask {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "task_id", nullable = false)
  Task task;

  @Column(nullable = false)
  String title;

  @Column(columnDefinition = "TINYINT(1)", nullable = false)
  @Builder.Default
  Boolean completed = false;

  @Column(name = "created_at")
  @CreationTimestamp
  LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  LocalDateTime updatedAt;
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: org/hibernate/annotations/UpdateTimestamp#