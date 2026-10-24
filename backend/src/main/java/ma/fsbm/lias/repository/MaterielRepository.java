package ma.fsbm.lias.repository;

import java.util.List;
import ma.fsbm.lias.entity.Materiel;
import ma.fsbm.lias.entity.enums.EtatMateriel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterielRepository extends JpaRepository<Materiel, Long> {
    List<Materiel> findByEtat(EtatMateriel etat);
    List<Materiel> findByNomContainingIgnoreCase(String nom);
}
