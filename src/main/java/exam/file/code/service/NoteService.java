package exam.file.code.service;

import exam.file.code.Entity.Examen;
import exam.file.code.Entity.Note;
import exam.file.code.Entity.NoteHistorique;
import exam.file.code.Entity.Student;
import exam.file.code.dto.NoteCreateDto;
import exam.file.code.dto.NoteDto;
import exam.file.code.dto.NoteUpdateDto;
import exam.file.code.repository.CourseTeamAssignmentRepository;
import exam.file.code.repository.ExamenRepository;
import exam.file.code.repository.NoteHistoriqueRepository;
import exam.file.code.repository.NoteRepository;
import exam.file.code.repository.StudentRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoteService {

  private final NoteRepository noteRepository;
  private final NoteHistoriqueRepository noteHistoriqueRepository;
  private final ExamenRepository examenRepository;
  private final StudentRepository studentRepository;
  private final CourseTeamAssignmentRepository courseTeamAssignmentRepository;

  public NoteService(
      NoteRepository noteRepository,
      NoteHistoriqueRepository noteHistoriqueRepository,
      ExamenRepository examenRepository,
      StudentRepository studentRepository,
      CourseTeamAssignmentRepository courseTeamAssignmentRepository) {
    this.noteRepository = noteRepository;
    this.noteHistoriqueRepository = noteHistoriqueRepository;
    this.examenRepository = examenRepository;
    this.studentRepository = studentRepository;
    this.courseTeamAssignmentRepository = courseTeamAssignmentRepository;
  }

  public List<NoteDto> findByStudent(String studentId) {
    return noteRepository.findByStudentId(studentId).stream().map(this::toDto).toList();
  }

  public List<NoteDto> findByCoursForTeacher(UUID coursId, String teacherId) {
    verifyTeacherTeachesCours(teacherId, coursId);
    return noteRepository.findByExamenCoursId(coursId).stream().map(this::toDto).toList();
  }

  public List<NoteDto> findByCoursForAdmin(UUID coursId) {
    return noteRepository.findByExamenCoursId(coursId).stream().map(this::toDto).toList();
  }

  public NoteDto create(NoteCreateDto dto, String teacherId) {
    Examen examen =
        examenRepository
            .findById(dto.examenId())
            .orElseThrow(
                () -> new NoSuchElementException("Examen introuvable : " + dto.examenId()));

    verifyTeacherTeachesCours(teacherId, examen.getCours().getId());

    Student student =
        studentRepository
            .findById(dto.studentId())
            .orElseThrow(
                () -> new NoSuchElementException("Étudiant introuvable : " + dto.studentId()));

    Note note = new Note(null, student, examen, dto.valeur(), dto.anneeEtude());
    return toDto(noteRepository.save(note));
  }

  @Transactional
  public NoteDto update(UUID noteId, NoteUpdateDto dto, String teacherId) {
    if (dto.raison() == null || dto.raison().isBlank()) {
      throw new IllegalArgumentException("Une raison est obligatoire pour modifier une note.");
    }

    Note note =
        noteRepository
            .findById(noteId)
            .orElseThrow(() -> new NoSuchElementException("Note introuvable : " + noteId));

    verifyTeacherTeachesCours(teacherId, note.getExamen().getCours().getId());

    NoteHistorique historique = new NoteHistorique();
    historique.setNote(note);
    historique.setAncienneValeur(note.getValeur());
    historique.setNouvelleValeur(dto.nouvelleValeur());
    historique.setRaison(dto.raison());
    historique.setModifiePar(teacherId);
    noteHistoriqueRepository.save(historique);

    note.setValeur(dto.nouvelleValeur());
    return toDto(noteRepository.save(note));
  }

  private void verifyTeacherTeachesCours(String teacherId, UUID coursId) {
    boolean enseigneCeCours =
        courseTeamAssignmentRepository.findByCoursId(coursId).stream()
            .anyMatch(assignment -> assignment.getTeacher().getId().equals(teacherId));
    if (!enseigneCeCours) {
      throw new AccessDeniedException("Cet enseignant ne donne pas ce cours.");
    }
  }

  private NoteDto toDto(Note note) {
    return new NoteDto(
        note.getId(), note.getStudent().getId(), note.getExamen().getId(), note.getValeur());
  }
}
