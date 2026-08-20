package exam.file.code.Entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("STUDENT")
public class Student extends Users {
  @OneToMany(mappedBy = "student")
  private Set<TeamMembership> teamHistory = new HashSet<>();

  @Getter
  @Setter
  @ManyToOne
  @JoinColumn(name = "promotion_id")
  private Promotion promotion;
}
