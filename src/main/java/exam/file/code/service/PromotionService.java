package exam.file.code.service;

import exam.file.code.Entity.Note;
import exam.file.code.Entity.Promotion;
import exam.file.code.Entity.Student;
import exam.file.code.dto.MoyenneDto;
import exam.file.code.dto.PromotionDto;
import exam.file.code.repository.NoteRepository;
import exam.file.code.repository.PromotionRepository;
import exam.file.code.repository.StudentRepository;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PromotionService {

  // Seuil de validation d'une matière, appliqué individuellement à CHAQUE cours (L1 à L3).
  private static final BigDecimal SEUIL_VALIDATION_MATIERE = new BigDecimal("10");

  private final PromotionRepository promotionRepository;
  private final StudentRepository studentRepository;
  private final NoteRepository noteRepository;
  private final ExamenService examenService;

  public PromotionService(
      PromotionRepository promotionRepository,
      StudentRepository studentRepository,
      NoteRepository noteRepository,
      ExamenService examenService) {
    this.promotionRepository = promotionRepository;
    this.studentRepository = studentRepository;
    this.noteRepository = noteRepository;
    this.examenService = examenService;
  }

  public List<PromotionDto> findAll() {
    return promotionRepository.findAll().stream().map(this::toDto).toList();
  }

  public PromotionDto create(PromotionDto dto) {
    Promotion promotion = new Promotion(null, dto.libelle(), dto.anneeEntree());
    return toDto(promotionRepository.save(promotion));
  }

  // Moyenne pondérée (par coefficient d'examen) pour UNE année du parcours (1, 2 ou 3).
  // Utile pour affichage/relevé, mais n'intervient PAS dans la décision de diplôme.
  public BigDecimal moyenneAnnuelle(String studentId, int anneeEtude) {
    List<Note> notes =
        noteRepository.findByStudentId(studentId).stream()
            .filter(note -> note.getAnneeEtude() == anneeEtude)
            .toList();
    return moyennePonderee(notes);
  }

  // Moyenne pondérée pour UNE matière précise, toutes années confondues.
  public BigDecimal moyenneParMatiere(String studentId, UUID coursId) {
    List<Note> notes =
        noteRepository.findByStudentId(studentId).stream()
            .filter(note -> note.getExamen().getCours().getId().equals(coursId))
            .toList();
    return moyennePonderee(notes);
  }

  // Diplômé si et seulement si, pour CHAQUE matière suivie de la L1 à la L3 :
  //  - la matière est terminée (les coefficients de ses examens somment à 1)
  //  - ET sa moyenne pondérée est >= 10
  // Une seule matière non terminée ou < 10 bloque le diplôme, peu importe le reste.
  public boolean estDiplome(String studentId) {
    List<Note> toutesLesNotes = noteRepository.findByStudentId(studentId);
    if (toutesLesNotes.isEmpty()) {
      return false;
    }

    Map<UUID, List<Note>> notesParMatiere =
        toutesLesNotes.stream()
            .collect(Collectors.groupingBy(note -> note.getExamen().getCours().getId()));

    return notesParMatiere.entrySet().stream()
        .allMatch(
            entry -> {
              UUID coursId = entry.getKey();
              List<Note> notesDeLaMatiere = entry.getValue();
              boolean terminee = examenService.matiereEstTerminee(coursId);
              boolean moyenneOk =
                  moyennePonderee(notesDeLaMatiere).compareTo(SEUIL_VALIDATION_MATIERE) >= 0;
              return terminee && moyenneOk;
            });
  }

  // Vue détaillée : la moyenne de chaque matière suivie par l'étudiant (par référence de cours),
  // utile pour comprendre pourquoi il est diplômé ou non.
  public Map<String, BigDecimal> detailMoyennesParMatiere(String studentId) {
    List<Note> toutesLesNotes = noteRepository.findByStudentId(studentId);

    Map<UUID, List<Note>> notesParMatiere =
        toutesLesNotes.stream()
            .collect(Collectors.groupingBy(note -> note.getExamen().getCours().getId()));

    return notesParMatiere.values().stream()
        .collect(
            Collectors.toMap(
                notesDeLaMatiere -> notesDeLaMatiere.get(0).getExamen().getCours().getRef(),
                this::moyennePonderee));
  }

  // Tous les étudiants d'une promotion avec leur moyenne générale et leur statut diplômé.
  public List<MoyenneDto> listeResultatsPromotion(UUID promotionId) {
    if (!promotionRepository.existsById(promotionId)) {
      throw new NoSuchElementException("Promotion introuvable : " + promotionId);
    }

    List<Student> etudiants = studentRepository.findByPromotionId(promotionId);

    return etudiants.stream()
        .map(
            etudiant -> {
              BigDecimal moyenneGenerale =
                  moyennePonderee(noteRepository.findByStudentId(etudiant.getId()));
              boolean diplome = estDiplome(etudiant.getId());
              return new MoyenneDto(etudiant.getId(), moyenneGenerale, diplome);
            })
        .toList();
  }

  // Ne garde que les diplômés — utilisé pour l'export Excel demandé par l'énoncé.
  public List<MoyenneDto> listeDiplomes(UUID promotionId) {
    return listeResultatsPromotion(promotionId).stream().filter(MoyenneDto::diplome).toList();
  }

  private BigDecimal moyennePonderee(List<Note> notes) {
    if (notes.isEmpty()) {
      return BigDecimal.ZERO;
    }

    BigDecimal sommePonderee = BigDecimal.ZERO;
    BigDecimal sommeCoefficients = BigDecimal.ZERO;

    for (Note note : notes) {
      BigDecimal coefficient = note.getExamen().getCoefficient();
      sommePonderee = sommePonderee.add(note.getValeur().multiply(coefficient));
      sommeCoefficients = sommeCoefficients.add(coefficient);
    }

    if (sommeCoefficients.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    }

    return sommePonderee.divide(sommeCoefficients, MathContext.DECIMAL64);
  }

  private PromotionDto toDto(Promotion promotion) {
    return new PromotionDto(promotion.getId(), promotion.getLibelle(), promotion.getAnneeEntree());
  }
}
