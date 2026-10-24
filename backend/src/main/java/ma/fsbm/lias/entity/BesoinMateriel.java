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
import ma.fsbm.lias.entity.enums.StatutBesoin;

@Entity
@Table(name = "besoins_materiel")
@Getter
@Setter
@NoArgsConstructor
public class BesoinMateriel extends BaseEntity {

    @Column(nullable = false)
    private String intitule;

    @Column(length = 2000)
    private String description;

    private Integer quantiteDemandee = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutBesoin statut = StatutBesoin.EN_ATTENTE;

    @Column(length = 1000)
    private String commentaireDecision;

    private LocalDate dateValidation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "demandeur_id")
    private Membre demandeur;

    @ManyToOne
    @JoinColumn(name = "valideur_id")
    private Membre valideur;
}
