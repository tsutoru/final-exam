package exam.file.code.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "team_membership")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeamMembership {

  @Id @GeneratedValue private UUID id;

  @ManyToOne
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @ManyToOne
  @JoinColumn(name = "team_id", nullable = false)
  private Team team;

  private LocalDate startDate;

  private LocalDate endDate;
}
