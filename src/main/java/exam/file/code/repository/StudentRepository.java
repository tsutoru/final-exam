package exam.file.code.repository;

import exam.file.code.Entity.Student;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, String> {

  List<Student> findByPromotionId(UUID promotionId);
}
