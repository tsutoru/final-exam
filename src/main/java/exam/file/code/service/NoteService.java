package exam.file.code.service;

import exam.file.code.Entity.Examen;
import exam.file.code.Entity.Note;
import exam.file.code.Entity.NoteHistorique;
import exam.file.code.Entity.Student;
import exam.file.code.dto.NoteCreateDto;
import exam.file.code.dto.NoteDto;
import exam.file.code.dto.NoteUpdateDto;
import exam.file.code.repository.ExamenRepository;
import exam.file.code.repository.NoteHistoriqueRepository;
import exam.file.code.repository.NoteRepository;
import exam.file.code.repository.StudentRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoteService {

  private final NoteRepository noteRepository;
  private final NoteHistoriqueRepository noteHistoriqueRepository;
  private final ExamenRepository examenRepository;
  private final StudentRepository studentRepository;

  public NoteService(
          NoteRepository noteRepository,
          NoteHistoriqueRepository noteHistoriqueRepository,
          ExamenRepository examenRepository,
          StudentRepository studentRepository) {
    this.noteRepository = noteRepository;
    this.noteHistoriqueRepository = noteHistoriqueRepository;
    this.examenRepository = examenRepository;
    this.studentRepository = studentRepository;
  }

  public List<NoteDto> findByStudent(String studentId) {
    return noteRepository.findByStudentId(studentId).stream().map(this::toDto).toList();
  }

  public NoteDto create(NoteCreateDto dto) {
    Examen examen =
            examenRepository
                    .findById(dto.examenId())
                    .orElseThrow(() -> new NoSuchElementException("Examen introuvable : " + dto.examenId()));
    Student student =
            studentRepository
                    .findById(dto.studentId())
                    .orElseThrow(() -> new NoSuchElementException("Étudiant introuvable : " + dto.studentId()));
    Note note = new Note(null, student, examen, dto.valeur());
    return toDto(noteRepository.save(note));
  }

  
  @Transactional
  public NoteDto update(UUID noteId, NoteUpdateDto dto) {
    if (dto.raison() == null || dto.raison().isBlank()) {
      throw new IllegalArgumentException("Une raison est obligatoire pour modifier une note.");
    }

    Note note =
            noteRepository
                    .findById(noteId)
                    .orElseThrow(() -> new NoSuchElementException("Note introuvable : " + noteId));

    NoteHistorique historique = new NoteHistorique();
    historique.setNote(note);
    historique.setAncienneValeur(note.getValeur());
    historique.setNouvelleValeur(dto.nouvelleValeur());
    historique.setRaison(dto.raison());
    historique.setModifiePar("system");
    noteHistoriqueRepository.save(historique);

    note.setValeur(dto.nouvelleValeur());
    return toDto(noteRepository.save(note));
  }

  private NoteDto toDto(Note note) {
    return new NoteDto(
            note.getId(), note.getStudent().getId(), note.getExamen().getId(), note.getValeur());
  }
}