package exam.file.code.Entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(
    name = "course_team_assignment",
    uniqueConstraints = @UniqueConstraint(columnNames = {"cours_id", "teacher_id", "team_id"}))
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

  @ManyToOne(optional = false)
  @JoinColumn(name = "team_id")
  private Team team;
}
