package exam.file.code.service;

import static org.assertj.core.api.Assertions.assertThat;

import exam.file.code.Entity.Cours;
import exam.file.code.Entity.Examen;
import exam.file.code.Entity.Note;
import exam.file.code.Entity.Student;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PdfServiceTest {

  private final PdfService pdfService = new PdfService();

  @Test
  void generateReleveDeNotes_should_produce_a_non_empty_pdf_file() throws Exception {
    Student student = new Student();
    student.setId("student-1");
    student.setUsername("Jean Dupont");

    Cours cours = new Cours(UUID.randomUUID(), "Base de données", 5, null, null);
    Examen examen = new Examen(UUID.randomUUID(), "Partiel 1", null, BigDecimal.valueOf(0.25), cours);
    Note note = new Note(UUID.randomUUID(), student, examen, new BigDecimal("14.5"), 1);

    File pdf = pdfService.generateReleveDeNotes(student, List.of(note));

    assertThat(pdf).exists();
    assertThat(pdf.length()).isGreaterThan(0);
    
    byte[] header = new byte[4];
    Files.newInputStream(pdf.toPath()).read(header);
    assertThat(new String(header)).isEqualTo("%PDF");

    pdf.deleteOnExit();
  }

  @Test
  void generateReleveDeNotes_should_work_with_an_empty_notes_list() {
    Student student = new Student();
    student.setId("student-2");
    student.setUsername("Sans Notes");

    File pdf = pdfService.generateReleveDeNotes(student, List.of());

    assertThat(pdf).exists();
    assertThat(pdf.length()).isGreaterThan(0);

    pdf.deleteOnExit();
  }
}
