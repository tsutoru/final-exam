package exam.file.code.Entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "note")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Note {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "student_id")
  private Student student;

  @ManyToOne(optional = false)
  @JoinColumn(name = "examen_id")
  private Examen examen;

  @Column(nullable = false)
  private BigDecimal valeur;

  // Année du parcours au moment de la note : 1, 2 ou 3.
  // Dénormalisé volontairement pour simplifier les calculs de moyenne
  // annuelle / sur 3 ans sans jointures complexes via Team/TeamMembership.
  @Column(nullable = false)
  private int anneeEtude;
}
