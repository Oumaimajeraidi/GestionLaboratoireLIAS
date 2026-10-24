package ma.fsbm.lias.repository;

import java.util.List;
import ma.fsbm.lias.entity.Evenement;
import ma.fsbm.lias.entity.enums.TypeEvenement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    List<Evenement> findAllByOrderByDateDebutDesc();

    List<Evenement> findByTypeOrderByDateDebutDesc(TypeEvenement type);
}
