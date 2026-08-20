package exam.file.code.Entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
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

  @Column(nullable = false, precision = 5, scale = 4)
  private BigDecimal coefficient;

  @ManyToOne
  @JoinColumn(name = "cours_id")
  private Cours cours;
}
