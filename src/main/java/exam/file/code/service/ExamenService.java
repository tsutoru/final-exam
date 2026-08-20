package exam.file.code.service;

import exam.file.code.Entity.Cours;
import exam.file.code.Entity.Examen;
import exam.file.code.dto.ExamenCreateDto;
import exam.file.code.dto.ExamenDto;
import exam.file.code.repository.CoursRepository;
import exam.file.code.repository.ExamenRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ExamenService {

  private final ExamenRepository examenRepository;
  private final CoursRepository coursRepository;

  public ExamenService(ExamenRepository examenRepository, CoursRepository coursRepository) {
    this.examenRepository = examenRepository;
    this.coursRepository = coursRepository;
  }

  public List<ExamenDto> findByCours(UUID coursId) {
    return examenRepository.findByCoursId(coursId).stream().map(this::toDto).toList();
  }

  // Un cours peut avoir plusieurs examens (ex: 4 examens à 25% chacun), mais la somme
  // de leurs coefficients ne doit jamais dépasser 1 (100% de la matière).
  public ExamenDto create(ExamenCreateDto dto) {
    Cours cours =
        coursRepository
            .findById(dto.coursId())
            .orElseThrow(() -> new NoSuchElementException("Cours introuvable : " + dto.coursId()));

    BigDecimal sommeActuelle = sommeCoefficients(dto.coursId());
    BigDecimal nouvelleSomme = sommeActuelle.add(dto.coefficient());

    if (nouvelleSomme.compareTo(BigDecimal.ONE) > 0) {
      throw new IllegalArgumentException(
          "La somme des coefficients des examens de ce cours dépasserait 1 (actuellement "
              + sommeActuelle
              + ", +"
              + dto.coefficient()
              + ")");
    }

    Examen examen = new Examen(null, dto.name(), dto.dateTime(), dto.coefficient(), cours);
    return toDto(examenRepository.save(examen));
  }

  // La somme totale des coefficients des examens déjà créés pour ce cours.
  public BigDecimal sommeCoefficients(UUID coursId) {
    return examenRepository.findByCoursId(coursId).stream()
        .map(Examen::getCoefficient)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  // Une matière est "terminée" quand la somme de ses coefficients d'examens vaut exactement 1.
  // Tant que ce n'est pas le cas, sa moyenne ne doit pas être considérée comme définitive.
  public boolean matiereEstTerminee(UUID coursId) {
    return sommeCoefficients(coursId).compareTo(BigDecimal.ONE) == 0;
  }

  private ExamenDto toDto(Examen examen) {
    return new ExamenDto(
        examen.getId(),
        examen.getName(),
        examen.getDateTime(),
        examen.getCoefficient(),
        examen.getCours().getId());
  }
}
