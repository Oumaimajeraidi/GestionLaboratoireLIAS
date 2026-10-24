package ma.fsbm.lias.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Periode d'affiliation d'un membre au laboratoire. Un membre peut quitter et revenir :
 * chaque periode est historisee (date debut / date fin). dateFin = null => periode en cours.
 */
@Entity
@Table(name = "affiliations")
@Getter
@Setter
@NoArgsConstructor
public class Affiliation extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "membre_id")
    private Membre membre;

    private LocalDate dateDebut;

    /** null = affiliation toujours active. */
    private LocalDate dateFin;

    private String motif;
}
