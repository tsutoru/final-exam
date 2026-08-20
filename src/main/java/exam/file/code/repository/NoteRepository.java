package exam.file.code.repository;

import exam.file.code.Entity.Note;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, UUID> {

  List<Note> findByStudentId(String studentId);

  List<Note> findByExamenCoursId(UUID coursId);

  List<Note> findByStudentIdAndAnneeEtude(String studentId, int anneeEtude);
}
