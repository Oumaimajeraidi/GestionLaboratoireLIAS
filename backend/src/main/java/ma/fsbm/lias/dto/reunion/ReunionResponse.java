package ma.fsbm.lias.dto.reunion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import ma.fsbm.lias.entity.enums.TypeReunion;

public record ReunionResponse(
    Long id,
    String titre,
    TypeReunion type,
    LocalDate dateReunion,
    String lieu,
    String ordreJour,
    Long equipeId,
    String equipeNom,
    String pvFichierNom,
    boolean hasPv,
    LocalDateTime createdAt
) {}
