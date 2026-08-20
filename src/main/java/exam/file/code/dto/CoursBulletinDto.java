package exam.file.code.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CoursBulletinDto(
    UUID coursId, String ref, int credit, BigDecimal moyenne, boolean valide) {}
