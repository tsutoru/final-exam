package exam.file.code.endpoint.rest.controller;

import exam.file.code.dto.ExamenCreateDto;
import exam.file.code.dto.ExamenDto;
import exam.file.code.service.ExamenService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExamenController {

  private final ExamenService examenService;

  public ExamenController(ExamenService examenService) {
    this.examenService = examenService;
  }

  @GetMapping("/cours/{coursId}/examens")
  public List<ExamenDto> findByCours(@PathVariable UUID coursId) {
    return examenService.findByCours(coursId);
  }

  // Réservé enseignant/admin : créer un examen sans dépasser 100% de coefficient sur le cours.
  @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
  @PostMapping("/examens")
  @ResponseStatus(HttpStatus.CREATED)
  public ExamenDto create(@RequestBody ExamenCreateDto dto) {
    return examenService.create(dto);
  }
}
