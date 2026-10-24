package ma.fsbm.lias.repository;

import ma.fsbm.lias.entity.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipeRepository extends JpaRepository<Equipe, Long> {

    boolean existsByNomIgnoreCase(String nom);
}
