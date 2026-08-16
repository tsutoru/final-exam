package exam.file.code.repository;

import exam.file.code.Entity.Examen;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamenRepository extends JpaRepository<Examen, UUID> {

  List<Examen> findByCoursId(UUID coursId);
}
