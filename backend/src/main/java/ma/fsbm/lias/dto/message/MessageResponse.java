package ma.fsbm.lias.dto.message;

import java.time.LocalDateTime;

public record MessageResponse(
    Long id,
    Long expediteurId,
    String expediteurNom,
    String expediteurEmail,
    Long destinataireId,
    String destinataireNom,
    Long equipeId,
    String equipeNom,
    String sujet,
    String contenu,
    boolean lu,
    LocalDateTime createdAt
) {}
