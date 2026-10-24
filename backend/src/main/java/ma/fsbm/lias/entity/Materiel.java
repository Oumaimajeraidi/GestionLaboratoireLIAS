package ma.fsbm.lias.entity;

import jakarta.persistence.Column;
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
import ma.fsbm.lias.entity.enums.EtatMateriel;

@Entity
@Table(name = "materiels")
@Getter
@Setter
@NoArgsConstructor
public class Materiel extends BaseEntity {

    @Column(nullable = false)
    private String nom;

    @Column(length = 2000)
    private String description;

    private String reference;

    private Integer quantite = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatMateriel etat = EtatMateriel.DISPONIBLE;

    private LocalDate dateArrivee;

    /** Membre actuellement responsable du matériel (null = en stock). */
    @ManyToOne
    @JoinColumn(name = "attribue_a_id")
    private Membre attribueA;
}
