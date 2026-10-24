package ma.fsbm.lias.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.fsbm.lias.entity.enums.StatutMembre;

/**
 * Profil metier d'un membre du laboratoire LIAS.
 * Regle : aucun membre ne perd ses donnees ; le statut est historise via {@link Affiliation}
 * et les roles via {@link AffectationRole}.
 */
@Entity
@Table(name = "membres")
@Getter
@Setter
@NoArgsConstructor
public class Membre extends BaseEntity {

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String email;

    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutMembre statut;

    private String etablissementOrigine;

    private String laboratoireOrigine;

    @Column(length = 4000)
    private String biographie;

    /** Centres d'interet (liste libre separee par des virgules). */
    @Column(length = 1000)
    private String centresInteret;

    /** Confidentielle : visible uniquement par l'interesse et l'administration. */
    private LocalDate dateNaissance;

    private LocalDate dateEmbauche;

    /** Date d'affiliation au laboratoire : cle metier. */
    private LocalDate dateAffiliation;

    /** Compte d'authentification associe (null pour un profil non encore active). */
    @OneToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipeActuelle;
}
