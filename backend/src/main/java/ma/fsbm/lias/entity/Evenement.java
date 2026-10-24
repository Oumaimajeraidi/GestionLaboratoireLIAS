package ma.fsbm.lias.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.fsbm.lias.entity.enums.TypeEvenement;

/**
 * Evenement scientifique : conference, seminaire ou workshop.
 * Archivage par edition (champ {@code edition}).
 */
@Entity
@Table(name = "evenements")
@Getter
@Setter
@NoArgsConstructor
public class Evenement extends BaseEntity {

    @Column(nullable = false)
    private String titre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEvenement type;

    @Column(length = 4000)
    private String description;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private String lieu;

    /** Edition (ex : "2025", "3eme edition") pour l'archivage. */
    private String edition;

    @ManyToMany
    @JoinTable(
            name = "evenement_organisateurs",
            joinColumns = @JoinColumn(name = "evenement_id"),
            inverseJoinColumns = @JoinColumn(name = "membre_id"))
    private Set<Membre> organisateurs = new HashSet<>();
}
