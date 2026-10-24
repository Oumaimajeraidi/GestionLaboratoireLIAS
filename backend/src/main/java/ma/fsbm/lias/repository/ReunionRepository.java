package ma.fsbm.lias.repository;

import java.util.List;
import ma.fsbm.lias.entity.Reunion;
import ma.fsbm.lias.entity.enums.TypeReunion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReunionRepository extends JpaRepository<Reunion, Long> {
    List<Reunion> findByType(TypeReunion type);
    List<Reunion> findByOrderByDateReunionDesc();
}
