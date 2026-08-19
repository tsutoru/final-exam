package exam.file.code.service.event;

import exam.file.code.Entity.Note;
import exam.file.code.Entity.Student;
import exam.file.code.endpoint.event.model.ReleveNotesRequested;
import exam.file.code.file.bucket.BucketComponent;
import exam.file.code.mail.Email;
import exam.file.code.mail.Mailer;
import exam.file.code.repository.NoteRepository;
import exam.file.code.repository.StudentRepository;
import exam.file.code.service.PdfService;
import jakarta.mail.internet.InternetAddress;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReleveNotesRequestedService implements Consumer<ReleveNotesRequested> {

  private final StudentRepository studentRepository;
  private final NoteRepository noteRepository;
  private final PdfService pdfService;
  private final BucketComponent bucketComponent;
  private final Mailer mailer;

  @SneakyThrows
  @Override
  public void accept(ReleveNotesRequested event) {
    Student student =
        studentRepository
            .findById(event.getStudentId())
            .orElseThrow(
                () -> new NoSuchElementException("Étudiant introuvable : " + event.getStudentId()));

    List<Note> notes = noteRepository.findByStudentId(event.getStudentId());

    File pdf = pdfService.generateReleveDeNotes(student, notes);

    var bucketKey = "releves/" + student.getId() + "-" + System.currentTimeMillis() + ".pdf";
    bucketComponent.upload(pdf, bucketKey);
    var presignedUri = bucketComponent.presign(bucketKey, Duration.ofDays(7));

    var recipientAddress = new InternetAddress(event.getRecipientEmail());
    mailer.accept(
        new Email(
            recipientAddress,
            List.of(),
            List.of(),
            "Votre relevé de notes",
            "Bonjour,\n\nVoici le lien vers votre relevé de notes (valable 7 jours) :\n"
                + presignedUri
                + "\n\nCordialement.",
            List.of()));
  }
}
