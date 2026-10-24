package ma.fsbm.lias.service;

import java.time.LocalDate;
import java.util.List;
import ma.fsbm.lias.dto.materiel.BesoinRequest;
import ma.fsbm.lias.dto.materiel.BesoinResponse;
import ma.fsbm.lias.dto.materiel.MaterielRequest;
import ma.fsbm.lias.dto.materiel.MaterielResponse;
import ma.fsbm.lias.entity.BesoinMateriel;
import ma.fsbm.lias.entity.Materiel;
import ma.fsbm.lias.entity.Membre;
import ma.fsbm.lias.entity.enums.EtatMateriel;
import ma.fsbm.lias.entity.enums.StatutBesoin;
import ma.fsbm.lias.exception.ResourceNotFoundException;
import ma.fsbm.lias.repository.BesoinMaterielRepository;
import ma.fsbm.lias.repository.MaterielRepository;
import ma.fsbm.lias.repository.MembreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MaterielService {

    private final MaterielRepository materielRepository;
    private final BesoinMaterielRepository besoinRepository;
    private final MembreRepository membreRepository;

    public MaterielService(MaterielRepository materielRepository,
                           BesoinMaterielRepository besoinRepository,
                           MembreRepository membreRepository) {
        this.materielRepository = materielRepository;
        this.besoinRepository = besoinRepository;
        this.membreRepository = membreRepository;
    }

    // ---- Matériel ----

    @Transactional(readOnly = true)
    public List<MaterielResponse> findAll() {
        return materielRepository.findAll().stream().map(this::toMaterielResponse).toList();
    }

    public MaterielResponse create(MaterielRequest request) {
        Materiel materiel = new Materiel();
        applyMateriel(materiel, request);
        return toMaterielResponse(materielRepository.save(materiel));
    }

    public MaterielResponse update(Long id, MaterielRequest request) {
        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Materiel", id));
        applyMateriel(materiel, request);
        return toMaterielResponse(materielRepository.save(materiel));
    }

    public void delete(Long id) {
        materielRepository.delete(materielRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Materiel", id)));
    }

    public MaterielResponse attribuer(Long id, Long membreId) {
        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Materiel", id));
        Membre membre = membreRepository.findById(membreId)
                .orElseThrow(() -> ResourceNotFoundException.of("Membre", membreId));
        materiel.setAttribueA(membre);
        materiel.setEtat(EtatMateriel.ATTRIBUE);
        return toMaterielResponse(materielRepository.save(materiel));
    }

    public MaterielResponse retourner(Long id) {
        Materiel materiel = materielRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Materiel", id));
        materiel.setAttribueA(null);
        materiel.setEtat(EtatMateriel.DISPONIBLE);
        return toMaterielResponse(materielRepository.save(materiel));
    }

    private void applyMateriel(Materiel m, MaterielRequest request) {
        m.setNom(request.nom());
        m.setDescription(request.description());
        m.setReference(request.reference());
        m.setQuantite(request.quantite() != null ? request.quantite() : 1);
        m.setEtat(request.etat() != null ? request.etat() : EtatMateriel.DISPONIBLE);
        m.setDateArrivee(request.dateArrivee());
        if (request.attribueAId() != null) {
            m.setAttribueA(membreRepository.findById(request.attribueAId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Membre", request.attribueAId())));
        } else {
            m.setAttribueA(null);
        }
    }

    private MaterielResponse toMaterielResponse(Materiel m) {
        Membre a = m.getAttribueA();
        return new MaterielResponse(m.getId(), m.getNom(), m.getDescription(), m.getReference(),
                m.getQuantite(), m.getEtat(), m.getDateArrivee(),
                a != null ? a.getId() : null,
                a != null ? a.getPrenom() + " " + a.getNom() : null,
                m.getCreatedAt());
    }

    // ---- Besoins ----

    @Transactional(readOnly = true)
    public List<BesoinResponse> findAllBesoins() {
        return besoinRepository.findAll().stream().map(this::toBesoinResponse).toList();
    }

    public BesoinResponse createBesoin(BesoinRequest request) {
        Membre demandeur = membreRepository.findById(request.demandeurId())
                .orElseThrow(() -> ResourceNotFoundException.of("Membre", request.demandeurId()));
        BesoinMateriel besoin = new BesoinMateriel();
        besoin.setIntitule(request.intitule());
        besoin.setDescription(request.description());
        besoin.setQuantiteDemandee(request.quantiteDemandee() != null ? request.quantiteDemandee() : 1);
        besoin.setDemandeur(demandeur);
        return toBesoinResponse(besoinRepository.save(besoin));
    }

    public BesoinResponse validerBesoin(Long id, Long valideurId, String commentaire) {
        BesoinMateriel besoin = besoinRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("BesoinMateriel", id));
        Membre valideur = membreRepository.findById(valideurId)
                .orElseThrow(() -> ResourceNotFoundException.of("Membre", valideurId));
        besoin.setStatut(StatutBesoin.VALIDE);
        besoin.setValideur(valideur);
        besoin.setCommentaireDecision(commentaire);
        besoin.setDateValidation(LocalDate.now());
        return toBesoinResponse(besoinRepository.save(besoin));
    }

    public BesoinResponse refuserBesoin(Long id, Long valideurId, String commentaire) {
        BesoinMateriel besoin = besoinRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("BesoinMateriel", id));
        Membre valideur = membreRepository.findById(valideurId)
                .orElseThrow(() -> ResourceNotFoundException.of("Membre", valideurId));
        besoin.setStatut(StatutBesoin.REFUSE);
        besoin.setValideur(valideur);
        besoin.setCommentaireDecision(commentaire);
        besoin.setDateValidation(LocalDate.now());
        return toBesoinResponse(besoinRepository.save(besoin));
    }

    private BesoinResponse toBesoinResponse(BesoinMateriel b) {
        Membre d = b.getDemandeur();
        Membre v = b.getValideur();
        return new BesoinResponse(b.getId(), b.getIntitule(), b.getDescription(),
                b.getQuantiteDemandee(), b.getStatut(), b.getCommentaireDecision(),
                b.getDateValidation(),
                d.getId(), d.getPrenom() + " " + d.getNom(),
                v != null ? v.getId() : null,
                v != null ? v.getPrenom() + " " + v.getNom() : null,
                b.getCreatedAt());
    }
}
