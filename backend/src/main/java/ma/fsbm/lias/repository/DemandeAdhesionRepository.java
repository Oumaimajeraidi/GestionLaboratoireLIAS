package ma.fsbm.lias.repository;

import java.util.List;
import ma.fsbm.lias.entity.DemandeAdhesion;
import ma.fsbm.lias.entity.enums.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemandeAdhesionRepository extends JpaRepository<DemandeAdhesion, Long> {

    List<DemandeAdhesion> findAllByOrderByCreatedAtDesc();

    List<DemandeAdhesion> findByStatutOrderByCreatedAtDesc(StatutDemande statut);
}
