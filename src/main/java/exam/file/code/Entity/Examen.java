package exam.file.code.Entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "examen")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Examen {
  @Id @GeneratedValue private UUID id;
  private String name;
  private Instant dateTime;

  @ManyToOne
  @JoinColumn(name = "cours_id")
  private Cours cours;
}
