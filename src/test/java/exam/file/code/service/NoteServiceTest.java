package exam.file.code.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import exam.file.code.Entity.Cours;
import exam.file.code.Entity.CourseTeamAssignment;
import exam.file.code.Entity.Examen;
import exam.file.code.Entity.Note;
import exam.file.code.Entity.Student;
import exam.file.code.Entity.Teacher;
import exam.file.code.dto.NoteCreateDto;
import exam.file.code.dto.NoteDto;
import exam.file.code.dto.NoteUpdateDto;
import exam.file.code.repository.CourseTeamAssignmentRepository;
import exam.file.code.repository.ExamenRepository;
import exam.file.code.repository.NoteHistoriqueRepository;
import exam.file.code.repository.NoteRepository;
import exam.file.code.repository.StudentRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

  @Mock private NoteRepository noteRepository;
  @Mock private NoteHistoriqueRepository noteHistoriqueRepository;
  @Mock private ExamenRepository examenRepository;
  @Mock private StudentRepository studentRepository;
  @Mock private CourseTeamAssignmentRepository courseTeamAssignmentRepository;

  @InjectMocks private NoteService noteService;

  private UUID coursId;
  private UUID examenId;
  private UUID noteId;
  private String studentId;
  private String teacherId;
  private Cours cours;
  private Examen examen;
  private Student student;
  private Teacher teacher;

  @BeforeEach
  void setUp() {
    coursId = UUID.randomUUID();
    examenId = UUID.randomUUID();
    noteId = UUID.randomUUID();
    studentId = "student-1";
    teacherId = "teacher-1";

    cours = new Cours(coursId, "Base de données", 5, null, null);

    examen = new Examen(examenId, "Partiel 1", null, cours);

    student = new Student();
    student.setId(studentId);

    teacher = new Teacher();
    teacher.setId(teacherId);
  }

  @Test
  void create_should_save_note_when_teacher_teaches_the_course() {
    NoteCreateDto dto = new NoteCreateDto(studentId, examenId, new BigDecimal("14.5"));

    when(examenRepository.findById(examenId)).thenReturn(Optional.of(examen));
    when(courseTeamAssignmentRepository.findByCoursId(coursId))
        .thenReturn(List.of(assignmentFor(teacher)));
    when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
    when(noteRepository.save(any(Note.class)))
        .thenAnswer(
            invocation -> {
              Note note = invocation.getArgument(0);
              note.setId(noteId);
              return note;
            });

    NoteDto result = noteService.create(dto, teacherId);

    assertThat(result.id()).isEqualTo(noteId);
    assertThat(result.studentId()).isEqualTo(studentId);
    assertThat(result.examenId()).isEqualTo(examenId);
    assertThat(result.valeur()).isEqualByComparingTo("14.5");
  }

  @Test
  void create_should_throw_when_examen_does_not_exist() {
    NoteCreateDto dto = new NoteCreateDto(studentId, examenId, BigDecimal.TEN);
    when(examenRepository.findById(examenId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> noteService.create(dto, teacherId))
        .isInstanceOf(NoSuchElementException.class);

    verify(noteRepository, never()).save(any());
  }

  @Test
  void create_should_throw_when_teacher_does_not_teach_the_course() {
    NoteCreateDto dto = new NoteCreateDto(studentId, examenId, BigDecimal.TEN);
    when(examenRepository.findById(examenId)).thenReturn(Optional.of(examen));
    when(courseTeamAssignmentRepository.findByCoursId(coursId)).thenReturn(List.of());

    assertThatThrownBy(() -> noteService.create(dto, teacherId))
        .isInstanceOf(AccessDeniedException.class);

    verify(noteRepository, never()).save(any());
  }

  @Test
  void create_should_throw_when_student_does_not_exist() {
    NoteCreateDto dto = new NoteCreateDto(studentId, examenId, BigDecimal.TEN);
    when(examenRepository.findById(examenId)).thenReturn(Optional.of(examen));
    when(courseTeamAssignmentRepository.findByCoursId(coursId))
        .thenReturn(List.of(assignmentFor(teacher)));
    when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> noteService.create(dto, teacherId))
        .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void update_should_throw_when_raison_is_blank() {
    NoteUpdateDto dto = new NoteUpdateDto(BigDecimal.TEN, "   ");

    assertThatThrownBy(() -> noteService.update(noteId, dto, teacherId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("raison");

    verify(noteRepository, never()).findById(any());
  }

  @Test
  void update_should_throw_when_raison_is_null() {
    NoteUpdateDto dto = new NoteUpdateDto(BigDecimal.TEN, null);

    assertThatThrownBy(() -> noteService.update(noteId, dto, teacherId))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void update_should_throw_when_note_does_not_exist() {
    NoteUpdateDto dto = new NoteUpdateDto(BigDecimal.TEN, "Erreur de saisie");
    when(noteRepository.findById(noteId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> noteService.update(noteId, dto, teacherId))
        .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void update_should_throw_when_teacher_does_not_teach_the_course() {
    Note existingNote = new Note(noteId, student, examen, new BigDecimal("8"));
    NoteUpdateDto dto = new NoteUpdateDto(BigDecimal.TEN, "Réclamation étudiant");

    when(noteRepository.findById(noteId)).thenReturn(Optional.of(existingNote));
    when(courseTeamAssignmentRepository.findByCoursId(coursId)).thenReturn(List.of());

    assertThatThrownBy(() -> noteService.update(noteId, dto, teacherId))
        .isInstanceOf(AccessDeniedException.class);

    verify(noteHistoriqueRepository, never()).save(any());
  }

  @Test
  void update_should_record_history_and_change_value_when_authorized() {
    Note existingNote = new Note(noteId, student, examen, new BigDecimal("8"));
    NoteUpdateDto dto = new NoteUpdateDto(new BigDecimal("12"), "Erreur de correction");

    when(noteRepository.findById(noteId)).thenReturn(Optional.of(existingNote));
    when(courseTeamAssignmentRepository.findByCoursId(coursId))
        .thenReturn(List.of(assignmentFor(teacher)));
    when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

    NoteDto result = noteService.update(noteId, dto, teacherId);

    assertThat(result.valeur()).isEqualByComparingTo("12");
    verify(noteHistoriqueRepository)
        .save(
            org.mockito.ArgumentMatchers.argThat(
                historique ->
                    historique.getAncienneValeur().compareTo(new BigDecimal("8")) == 0
                        && historique.getNouvelleValeur().compareTo(new BigDecimal("12")) == 0
                        && historique.getRaison().equals("Erreur de correction")
                        && historique.getModifiePar().equals(teacherId)));
  }

  @Test
  void findByStudent_should_return_only_that_student_notes() {
    Note note = new Note(noteId, student, examen, new BigDecimal("15"));
    when(noteRepository.findByStudentId(studentId)).thenReturn(List.of(note));

    List<NoteDto> result = noteService.findByStudent(studentId);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).studentId()).isEqualTo(studentId);
  }

  @Test
  void findByCoursForTeacher_should_throw_when_teacher_does_not_teach_the_course() {
    when(courseTeamAssignmentRepository.findByCoursId(coursId)).thenReturn(List.of());

    assertThatThrownBy(() -> noteService.findByCoursForTeacher(coursId, teacherId))
        .isInstanceOf(AccessDeniedException.class);

    verify(noteRepository, never()).findByExamenCoursId(any());
  }

  @Test
  void findByCoursForTeacher_should_return_notes_when_authorized() {
    Note note = new Note(noteId, student, examen, new BigDecimal("9"));
    when(courseTeamAssignmentRepository.findByCoursId(coursId))
        .thenReturn(List.of(assignmentFor(teacher)));
    when(noteRepository.findByExamenCoursId(coursId)).thenReturn(List.of(note));

    List<NoteDto> result = noteService.findByCoursForTeacher(coursId, teacherId);

    assertThat(result).hasSize(1);
  }

  @Test
  void findByCoursForAdmin_should_return_notes_without_any_check() {
    Note note = new Note(noteId, student, examen, new BigDecimal("11"));
    when(noteRepository.findByExamenCoursId(coursId)).thenReturn(List.of(note));

    List<NoteDto> result = noteService.findByCoursForAdmin(coursId);

    assertThat(result).hasSize(1);
  }

  private CourseTeamAssignment assignmentFor(Teacher teacher) {
    return new CourseTeamAssignment(UUID.randomUUID(), cours, teacher, null);
  }
}
