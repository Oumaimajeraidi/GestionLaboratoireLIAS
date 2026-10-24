package ma.fsbm.lias.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ma.fsbm.lias.entity.enums.RoleSecurite;

/**
 * Compte d'authentification. Separe du profil metier {@link Membre}.
 * Un administrateur systeme peut posseder un Utilisateur sans Membre associe.
 * Les comptes geles (retraite) ou desactives (ancien) ont {@code actif = false}.
 */
@Entity
@Table(name = "utilisateurs")
@Getter
@Setter
@NoArgsConstructor
public class Utilisateur extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    /** false = compte gele (retraite) ou desactive (ancien membre). */
    @Column(nullable = false)
    private boolean actif = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "utilisateur_roles", joinColumns = @JoinColumn(name = "utilisateur_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<RoleSecurite> roles = new HashSet<>();
}
