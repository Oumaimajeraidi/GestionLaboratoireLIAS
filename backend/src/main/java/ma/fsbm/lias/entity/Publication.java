package ma.fsbm.lias.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.fsbm.lias.entity.enums.TypePublication;

/**
 * Publication scientifique. Peut etre ajoutee par les membres et doctorants,
 * consultee globalement et classee par auteur / annee / equipe.
 */
@Entity
@Table(name = "publications")
@Getter
@Setter
@NoArgsConstructor
public class Publication extends BaseEntity {

    @Column(nullable = false, length = 500)
    private String titre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypePublication type;

    private Integer annee;

    /** Revue ou conference de publication. */
    private String venue;

    private String doi;

    private String url;

    @Column(length = 4000)
    private String resume;

    @ManyToMany
    @JoinTable(
            name = "publication_auteurs",
            joinColumns = @JoinColumn(name = "publication_id"),
            inverseJoinColumns = @JoinColumn(name = "membre_id"))
    private Set<Membre> auteurs = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;
}
