package ma.fsbm.lias.repository;

import java.util.List;
import java.util.Optional;
import ma.fsbm.lias.entity.Membre;
import ma.fsbm.lias.entity.enums.StatutMembre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembreRepository extends JpaRepository<Membre, Long> {

    Optional<Membre> findByUtilisateurId(Long utilisateurId);

    Optional<Membre> findByEmail(String email);

    List<Membre> findByStatut(StatutMembre statut);

    List<Membre> findByEquipeActuelleId(Long equipeId);

    List<Membre> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);
}
