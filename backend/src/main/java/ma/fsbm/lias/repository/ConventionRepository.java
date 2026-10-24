package ma.fsbm.lias.repository;

import java.util.List;
import ma.fsbm.lias.entity.Convention;
import ma.fsbm.lias.entity.enums.StatutConvention;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConventionRepository extends JpaRepository<Convention, Long> {
    List<Convention> findByStatut(StatutConvention statut);
}
