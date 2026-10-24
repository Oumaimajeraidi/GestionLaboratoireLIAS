package ma.fsbm.lias.repository;

import java.util.List;
import ma.fsbm.lias.entity.Publication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicationRepository extends JpaRepository<Publication, Long> {

    List<Publication> findAllByOrderByAnneeDescIdDesc();

    List<Publication> findByAnneeOrderByIdDesc(Integer annee);

    List<Publication> findByAuteurs_IdOrderByAnneeDesc(Long membreId);

    List<Publication> findByEquipe_IdOrderByAnneeDesc(Long equipeId);
}
