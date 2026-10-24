package ma.fsbm.lias.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
public class Message extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "expediteur_id")
    private Utilisateur expediteur;

    /** null = message envoyé à une équipe. */
    @ManyToOne
    @JoinColumn(name = "destinataire_id")
    private Utilisateur destinataire;

    /** null = message privé. */
    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    @Column(nullable = false)
    private String sujet;

    @Column(nullable = false, length = 5000)
    private String contenu;

    private boolean lu = false;
}
