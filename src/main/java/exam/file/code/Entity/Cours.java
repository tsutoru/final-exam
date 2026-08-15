package exam.file.code.Entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cours {
  @Id @GeneratedValue private UUID id;
  private String ref;
  private int credit;

  @ManyToMany
  @JoinTable(
      name = "course_teacher",
      joinColumns = @JoinColumn(name = "course_id"),
      inverseJoinColumns = @JoinColumn(name = "teacher_id"))
  private Set<Teacher> teachers = new HashSet<>();

  @OneToMany(mappedBy = "cours")
  private Set<Examen> examens = new HashSet<>();
}
