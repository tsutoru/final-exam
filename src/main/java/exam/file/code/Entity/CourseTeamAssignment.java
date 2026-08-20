package exam.file.code.Entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Représente : "ce prof donne ce cours, à cette année du parcours (1/2/3), à ce groupe".
// team = null signifie que le cours est COMMUN : toute la promotion de cette année-là
// le suit, sans distinction EL/TN (cas systématique en L1, possible aussi en L2/L3
// pour les matières partagées entre les deux parcours).
@Entity
@Table(name = "course_team_assignment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseTeamAssignment {

  @Id @GeneratedValue private UUID id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "cours_id")
  private Cours cours;

  @ManyToOne(optional = false)
  @JoinColumn(name = "teacher_id")
  private Teacher teacher;

  @ManyToOne(optional = true)
  @JoinColumn(name = "team_id")
  private Team team;

  @Column(nullable = false)
  private int anneeEtude;
}
