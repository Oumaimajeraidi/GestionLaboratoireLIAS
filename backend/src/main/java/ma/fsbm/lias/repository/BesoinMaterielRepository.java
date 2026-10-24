package ma.fsbm.lias.repository;

import java.util.List;
import ma.fsbm.lias.entity.BesoinMateriel;
import ma.fsbm.lias.entity.enums.StatutBesoin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BesoinMaterielRepository extends JpaRepository<BesoinMateriel, Long> {
    List<BesoinMateriel> findByStatut(StatutBesoin statut);
    List<BesoinMateriel> findByDemandeurId(Long demandeurId);
}
