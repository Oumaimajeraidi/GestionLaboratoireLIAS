package ma.fsbm.lias.dto.gouvernance;

import java.time.LocalDate;
import ma.fsbm.lias.entity.enums.TypeResponsabilite;

public record AffectationRoleRequest(
    Long membreId,
    TypeResponsabilite responsabilite,
    Long equipeId,
    Long mandatId,
    LocalDate dateDebut,
    LocalDate dateFin
) {}
