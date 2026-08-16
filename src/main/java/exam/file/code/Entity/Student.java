package exam.file.code.Entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("STUDENT")
public class Student extends Users {
  private Set<Team> teams = new HashSet<>();
}
