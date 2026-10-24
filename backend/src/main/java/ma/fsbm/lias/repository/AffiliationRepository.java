package ma.fsbm.lias.repository;

import java.util.List;
import ma.fsbm.lias.entity.Affiliation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AffiliationRepository extends JpaRepository<Affiliation, Long> {

    List<Affiliation> findByMembreIdOrderByDateDebutDesc(Long membreId);
}
