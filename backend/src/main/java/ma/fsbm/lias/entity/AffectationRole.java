package ma.fsbm.lias.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.fsbm.lias.entity.enums.TypeResponsabilite;

/**
 * Affectation d'un role (responsabilite) a un membre sur une periode donnee.
 * Permet l'historisation complete des roles : un changement de role cree une nouvelle
 * affectation et clot la precedente, sans jamais supprimer de donnees.
 */
@Entity
@Table(name = "affectations_role")
@Getter
@Setter
@NoArgsConstructor
public class AffectationRole extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "membre_id")
    private Membre membre;

    @Enumerated(EnumType.STRING)
    private TypeResponsabilite responsabilite;

    /** Equipe concernee (pour chef / membre d'equipe). */
    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @ManyToOne
    @JoinColumn(name = "mandat_id")
    private Mandat mandat;

    private LocalDate dateDebut;

    /** null = role toujours en cours. */
    private LocalDate dateFin;
}
