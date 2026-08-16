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
@Table(name = "note_historique")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NoteHistorique {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "note_id")
  private Note note;

  @Column(nullable = false)
  private BigDecimal ancienneValeur;

  @Column(nullable = false)
  private BigDecimal nouvelleValeur;

  @Column(nullable = false)
  private String raison;

  @Column(nullable = false)
  private String modifiePar;

  @Column(nullable = false)
  private Instant dateModification = Instant.now();
}
