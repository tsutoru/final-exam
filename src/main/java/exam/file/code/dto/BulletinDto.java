package exam.file.code.dto;

import java.math.BigDecimal;
import java.util.List;

public record BulletinDto(
    String studentId,
    List<CoursBulletinDto> cours,
    BigDecimal moyenneGenerale,
    int creditsObtenus,
    int creditsTotaux) {}
