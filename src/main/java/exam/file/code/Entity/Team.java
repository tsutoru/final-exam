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
@Table(name = "teams")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Team {
  @Id @GeneratedValue private UUID id;
  private String ref;

  @OneToMany private Set<Student> studentSet = new HashSet<>();
}
