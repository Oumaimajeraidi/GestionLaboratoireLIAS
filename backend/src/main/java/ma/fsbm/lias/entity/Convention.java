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
import ma.fsbm.lias.entity.enums.StatutConvention;
import ma.fsbm.lias.entity.enums.TypeConvention;

@Entity
@Table(name = "conventions")
@Getter
@Setter
@NoArgsConstructor
public class Convention extends BaseEntity {

    @Column(nullable = false)
    private String intitule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeConvention type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutConvention statut = StatutConvention.EN_NEGOCIATION;

    @ManyToOne
    @JoinColumn(name = "partenaire_id")
    private Partenaire partenaire;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    @Column(length = 3000)
    private String objectif;

    /** Chemin du fichier de la convention (optionnel). */
    private String fichierPath;

    private String fichierNom;
}
