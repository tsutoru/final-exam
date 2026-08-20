package exam.file.code.endpoint.rest.controller;

import exam.file.code.dto.MoyenneDto;
import exam.file.code.dto.PromotionDto;
import exam.file.code.service.PromotionService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/promotions")
@PreAuthorize("hasRole('ADMIN')")
public class PromotionController {

  private final PromotionService promotionService;

  public PromotionController(PromotionService promotionService) {
    this.promotionService = promotionService;
  }

  @GetMapping
  public List<PromotionDto> findAll() {
    return promotionService.findAll();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PromotionDto create(@RequestBody PromotionDto dto) {
    return promotionService.create(dto);
  }

  // Tous les étudiants de la promotion avec leur moyenne générale sur 3 ans
  // et leur statut diplômé ou non.
  @GetMapping("/{id}/resultats")
  public List<MoyenneDto> resultats(@PathVariable UUID id) {
    return promotionService.listeResultatsPromotion(id);
  }

  // Uniquement les diplômés — utilisé ensuite par l'export Excel.
  @GetMapping("/{id}/diplomes")
  public List<MoyenneDto> diplomes(@PathVariable UUID id) {
    return promotionService.listeDiplomes(id);
  }
}
