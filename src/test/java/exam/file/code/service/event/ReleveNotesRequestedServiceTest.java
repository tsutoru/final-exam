package exam.file.code.service.event;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import exam.file.code.Entity.Note;
import exam.file.code.Entity.Student;
import exam.file.code.endpoint.event.model.ReleveNotesRequested;
import exam.file.code.file.bucket.BucketComponent;
import exam.file.code.file.hash.FileHash;
import exam.file.code.mail.Email;
import exam.file.code.mail.Mailer;
import exam.file.code.repository.NoteRepository;
import exam.file.code.repository.StudentRepository;
import exam.file.code.service.PdfService;
import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReleveNotesRequestedServiceTest {

  @Mock private StudentRepository studentRepository;
  @Mock private NoteRepository noteRepository;
  @Mock private PdfService pdfService;
  @Mock private BucketComponent bucketComponent;
  @Mock private Mailer mailer;

  @InjectMocks private ReleveNotesRequestedService releveNotesRequestedService;

  private Student student;
  private File fakePdf;

  @BeforeEach
  void setUp() throws Exception {
    student = new Student();
    student.setId("student-1");
    student.setUsername("Jean Dupont");

    fakePdf = File.createTempFile("releve-test", ".pdf");
    fakePdf.deleteOnExit();
  }

  @Test
  void accept_should_throw_when_student_does_not_exist() {
    ReleveNotesRequested event = new ReleveNotesRequested();
    event.setStudentId("unknown-id");
    event.setRecipientEmail("test@school.com");

    when(studentRepository.findById("unknown-id")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> releveNotesRequestedService.accept(event))
        .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void accept_should_generate_pdf_upload_to_bucket_and_send_email() throws Exception {
    ReleveNotesRequested event = new ReleveNotesRequested();
    event.setStudentId("student-1");
    event.setRecipientEmail("jean.dupont@school.com");

    List<Note> notes = List.of();
    URL fakePresignedUrl = new URL("https://bucket.s3.amazonaws.com/releves/fake.pdf?sig=abc");

    when(studentRepository.findById("student-1")).thenReturn(Optional.of(student));
    when(noteRepository.findByStudentId("student-1")).thenReturn(notes);
    when(pdfService.generateReleveDeNotes(student, notes)).thenReturn(fakePdf);
    when(bucketComponent.upload(any(File.class), anyString())).thenReturn(mock(FileHash.class));
    when(bucketComponent.presign(anyString(), any(Duration.class))).thenReturn(fakePresignedUrl);

    releveNotesRequestedService.accept(event);

    verify(pdfService).generateReleveDeNotes(student, notes);
    verify(bucketComponent).upload(any(File.class), anyString());
    verify(bucketComponent).presign(anyString(), any(Duration.class));
    verify(mailer)
        .accept(
            org.mockito.ArgumentMatchers.argThat(
                (Email email) ->
                    email.subject().equals("Votre relevé de notes")
                        && email.htmlBody().contains(fakePresignedUrl.toString())
                        && email.to().getAddress().equals("jean.dupont@school.com")));
  }
}
