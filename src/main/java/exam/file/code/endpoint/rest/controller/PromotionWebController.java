package exam.file.code.endpoint.rest.controller;
import exam.file.code.dto.MoyenneDto;
import exam.file.code.service.PromotionService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/promotions")
public class PromotionWebController {
    private final PromotionService promotionService;

    public PromotionWebController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping
    public String listPromotions(Model model) {

        model.addAttribute(
                "promotions",
                promotionService.findAll()
        );

        return "promotions";
    }

    @GetMapping("/{id}/diplomes")
    public ResponseEntity<byte[]> downloadDiplomes(
            @PathVariable UUID id) {

        List<MoyenneDto> diplomes =
                promotionService.listeDiplomes(id);

        StringBuilder csv = new StringBuilder();

        csv.append(
                "ID Étudiant,Moyenne générale,Diplômé\n"
        );

        for (MoyenneDto diplome : diplomes) {

            csv.append(diplome.studentId())
                    .append(",")
                    .append(diplome.moyenne())
                    .append(",")
                    .append(diplome.diplome())
                    .append("\n");
        }

        byte[] content =
                csv.toString()
                        .getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=diplomes.csv"
                )
                .contentType(
                        MediaType.parseMediaType("text/csv")
                )
                .body(content);
    }
}
