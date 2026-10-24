package ma.fsbm.lias.dto.message;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(
    Long destinataireId,
    Long equipeId,
    @NotBlank String sujet,
    @NotBlank String contenu
) {}
