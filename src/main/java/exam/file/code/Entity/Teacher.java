package exam.file.code.Entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("TEACHER")
public class Teacher extends Users {

  @ManyToMany(mappedBy = "teachers")
  private Set<Cours> courses = new HashSet<>();
}
