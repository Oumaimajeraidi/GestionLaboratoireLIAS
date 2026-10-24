package ma.fsbm.lias.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.fsbm.lias.entity.enums.TypeDocument;

/**
 * Document archive (programme, attestation, rapport...). Le fichier est stocke sur disque ;
 * cette entite ne conserve que les metadonnees. Structuration par evenement et par date (createdAt).
 */
@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
public class Document extends BaseEntity {

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeDocument typeDocument;

    @Column(length = 1000)
    private String description;

    /** Nom unique du fichier sur le disque. */
    @Column(nullable = false)
    private String fileName;

    /** Nom original fourni par l'utilisateur. */
    private String originalName;

    private String contentType;

    private long taille;

    @ManyToOne
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    @ManyToOne
    @JoinColumn(name = "uploade_par_id")
    private Membre uploadePar;
}
