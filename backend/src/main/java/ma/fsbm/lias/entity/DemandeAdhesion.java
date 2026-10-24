package ma.fsbm.lias.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.fsbm.lias.entity.enums.StatutDemande;

/**
 * Demande d'adhesion soumise en ligne. La decision (acceptee/refusee) est prise
 * uniquement par le directeur en mandat. Acceptee => creation de compte.
 */
@Entity
@Table(name = "demandes_adhesion")
@Getter
@Setter
@NoArgsConstructor
public class DemandeAdhesion extends BaseEntity {

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String email;

    private String etablissementOrigine;

    @Column(length = 2000)
    private String motivation;

    private String cvUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutDemande statut = StatutDemande.EN_ATTENTE;

    @Column(length = 1000)
    private String commentaireDecision;

    private LocalDateTime dateDecision;
}
