package exam.file.code.service;

import exam.file.code.Entity.Cours;
import exam.file.code.Entity.Note;
import exam.file.code.Entity.Student;
import exam.file.code.dto.BulletinDto;
import exam.file.code.dto.CoursBulletinDto;
import exam.file.code.repository.NoteRepository;
import exam.file.code.repository.StudentRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class BulletinService {
  private final NoteRepository noteRepository;
  private final StudentRepository studentRepository;

  public BulletinService(NoteRepository noteRepository, StudentRepository studentRepository) {
    this.noteRepository = noteRepository;
    this.studentRepository = studentRepository;
  }

  public BulletinDto getBulletin(String studentId) {

    Student student =
        studentRepository
            .findById(studentId)
            .orElseThrow(() -> new NoSuchElementException("Étudiant introuvable : " + studentId));

    List<Note> notes = noteRepository.findByStudentId(studentId);

    Map<Cours, List<Note>> notesParCours =
        notes.stream().collect(Collectors.groupingBy(note -> note.getExamen().getCours()));

    List<CoursBulletinDto> resultatsCours =
        notesParCours.entrySet().stream()
            .map(
                entry -> {
                  Cours cours = entry.getKey();
                  List<Note> notesCours = entry.getValue();

                  BigDecimal moyenne = calculerMoyenne(notesCours);

                  boolean valide = moyenne.compareTo(BigDecimal.TEN) >= 0;

                  return new CoursBulletinDto(
                      cours.getId(), cours.getRef(), cours.getCredit(), moyenne, valide);
                })
            .toList();

    int creditsTotaux = resultatsCours.stream().mapToInt(CoursBulletinDto::credit).sum();

    int creditsObtenus =
        resultatsCours.stream()
            .filter(CoursBulletinDto::valide)
            .mapToInt(CoursBulletinDto::credit)
            .sum();

    BigDecimal moyenneGenerale = calculerMoyenneGenerale(resultatsCours);

    // 7. Retourner le bulletin
    return new BulletinDto(
        student.getId(), resultatsCours, moyenneGenerale, creditsObtenus, creditsTotaux);
  }

  private BigDecimal calculerMoyenne(List<Note> notes) {

    if (notes.isEmpty()) {
      return BigDecimal.ZERO;
    }

    BigDecimal somme =
        notes.stream()
            .map(note -> note.getValeur().multiply(note.getExamen().getCoefficient()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    return somme.setScale(2, RoundingMode.HALF_UP);
  }

  private BigDecimal calculerMoyenneGenerale(List<CoursBulletinDto> cours) {

    if (cours.isEmpty()) {
      return BigDecimal.ZERO;
    }

    BigDecimal sommePonderee =
        cours.stream()
            .map(c -> c.moyenne().multiply(BigDecimal.valueOf(c.credit())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    int totalCredits = cours.stream().mapToInt(CoursBulletinDto::credit).sum();

    if (totalCredits == 0) {
      return BigDecimal.ZERO;
    }

    return sommePonderee.divide(BigDecimal.valueOf(totalCredits), 2, RoundingMode.HALF_UP);
  }
}
