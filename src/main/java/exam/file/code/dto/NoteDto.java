package exam.file.code.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record NoteDto(UUID id, String studentId, UUID examenId, BigDecimal valeur) {}
