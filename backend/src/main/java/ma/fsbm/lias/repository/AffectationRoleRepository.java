package ma.fsbm.lias.repository;

import java.util.List;
import ma.fsbm.lias.entity.AffectationRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AffectationRoleRepository extends JpaRepository<AffectationRole, Long> {

    List<AffectationRole> findByMembreIdOrderByDateDebutDesc(Long membreId);

    List<AffectationRole> findByMembreIdAndDateFinIsNull(Long membreId);
}
