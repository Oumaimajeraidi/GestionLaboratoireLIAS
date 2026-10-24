package ma.fsbm.lias.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Equipe de recherche du laboratoire.
 */
@Entity
@Table(name = "equipes")
@Getter
@Setter
@NoArgsConstructor
public class Equipe extends BaseEntity {

    @Column(nullable = false)
    private String nom;

    @Column(length = 1000)
    private String description;

    /** Thematique principale de l'equipe. */
    private String thematique;
}
