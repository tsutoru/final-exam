package exam.file.code.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ExamenCreateDto(
    String name, Instant dateTime, BigDecimal coefficient, UUID coursId) {}
