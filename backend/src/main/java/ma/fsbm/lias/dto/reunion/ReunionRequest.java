package ma.fsbm.lias.dto.reunion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import ma.fsbm.lias.entity.enums.TypeReunion;

public record ReunionRequest(
    @NotBlank String titre,
    @NotNull TypeReunion type,
    @NotNull LocalDate dateReunion,
    String lieu,
    String ordreJour,
    Long equipeId
) {}
