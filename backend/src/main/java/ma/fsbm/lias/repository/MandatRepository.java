package ma.fsbm.lias.repository;

import java.util.Optional;
import ma.fsbm.lias.entity.Mandat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MandatRepository extends JpaRepository<Mandat, Long> {

    Optional<Mandat> findFirstByActifTrue();
}
