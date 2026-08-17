package exam.file.code.Entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("STUDENT")
public class Student extends Users {
  @OneToMany(mappedBy = "student")
  private Set<TeamMembership> teamHistory = new HashSet<>();
}
