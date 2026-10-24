package ma.fsbm.lias.dto.gouvernance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import ma.fsbm.lias.entity.enums.TypeResponsabilite;

public record AffectationRoleResponse(
    Long id,
    Long membreId,
    String membreNom,
    TypeResponsabilite responsabilite,
    Long equipeId,
    String equipeNom,
    Long mandatId,
    LocalDate dateDebut,
    LocalDate dateFin,
    LocalDateTime createdAt
) {}
