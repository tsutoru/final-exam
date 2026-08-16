package exam.file.code.repository;

import exam.file.code.Entity.NoteHistorique;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteHistoriqueRepository extends JpaRepository<NoteHistorique, UUID> {

  List<NoteHistorique> findByNoteIdOrderByDateModificationDesc(UUID noteId);
}
