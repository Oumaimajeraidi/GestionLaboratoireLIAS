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
import ma.fsbm.lias.entity.enums.TypeReunion;

@Entity
@Table(name = "reunions")
@Getter
@Setter
@NoArgsConstructor
public class Reunion extends BaseEntity {

    @Column(nullable = false)
    private String titre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeReunion type;

    @Column(nullable = false)
    private LocalDate dateReunion;

    private String lieu;

    @Column(length = 3000)
    private String ordreJour;

    /** Fichier PV uploadé après la réunion. */
    private String pvFichierPath;

    private String pvFichierNom;

    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;
}
