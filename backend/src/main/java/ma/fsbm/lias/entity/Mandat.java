package ma.fsbm.lias.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Mandat de gouvernance : definit une periode (directeur, vice-directeur, chefs d'equipe).
 * L'historique complet des mandats est conserve.
 */
@Entity
@Table(name = "mandats")
@Getter
@Setter
@NoArgsConstructor
public class Mandat extends BaseEntity {

    @Column(nullable = false)
    private String intitule;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    /** Un seul mandat actif a la fois. */
    @Column(nullable = false)
    private boolean actif = false;

    @ManyToOne
    @JoinColumn(name = "directeur_id")
    private Membre directeur;

    @ManyToOne
    @JoinColumn(name = "vice_directeur_id")
    private Membre viceDirecteur;
}
