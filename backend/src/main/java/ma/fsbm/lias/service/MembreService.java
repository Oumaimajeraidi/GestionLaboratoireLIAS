package ma.fsbm.lias.service;

import java.util.List;
import ma.fsbm.lias.dto.membre.MembreRequest;
import ma.fsbm.lias.dto.membre.MembreResponse;
import ma.fsbm.lias.entity.Equipe;
import ma.fsbm.lias.entity.Membre;
import ma.fsbm.lias.entity.enums.StatutMembre;
import ma.fsbm.lias.exception.ResourceNotFoundException;
import ma.fsbm.lias.repository.EquipeRepository;
import ma.fsbm.lias.repository.MembreRepository;
import ma.fsbm.lias.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MembreService {

    private final MembreRepository membreRepository;
    private final EquipeRepository equipeRepository;
    private final UtilisateurRepository utilisateurRepository;

    public MembreService(MembreRepository membreRepository, EquipeRepository equipeRepository,
                         UtilisateurRepository utilisateurRepository) {
        this.membreRepository = membreRepository;
        this.equipeRepository = equipeRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional(readOnly = true)
    public List<MembreResponse> findAll() {
        return membreRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MembreResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public List<MembreResponse> search(String query) {
        return membreRepository
                .findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(query, query)
                .stream().map(this::toResponse).toList();
    }

    public MembreResponse create(MembreRequest request) {
        Membre membre = new Membre();
        applyRequest(membre, request);
        return toResponse(membreRepository.save(membre));
    }

    public MembreResponse update(Long id, MembreRequest request) {
        Membre membre = getEntity(id);
        applyRequest(membre, request);
        return toResponse(membreRepository.save(membre));
    }

    public void delete(Long id) {
        Membre membre = getEntity(id);
        membreRepository.delete(membre);
    }

    public MembreResponse desactiver(Long id) {
        Membre membre = getEntity(id);
        membre.setStatut(StatutMembre.ANCIEN);
        if (membre.getUtilisateur() != null) {
            membre.getUtilisateur().setActif(false);
            utilisateurRepository.save(membre.getUtilisateur());
        }
        return toResponse(membreRepository.save(membre));
    }

    public MembreResponse mettreEnRetraite(Long id) {
        Membre membre = getEntity(id);
        membre.setStatut(StatutMembre.RETRAITE);
        if (membre.getUtilisateur() != null) {
            membre.getUtilisateur().setActif(false);
            utilisateurRepository.save(membre.getUtilisateur());
        }
        return toResponse(membreRepository.save(membre));
    }

    public MembreResponse reactiver(Long id) {
        Membre membre = getEntity(id);
        membre.setStatut(StatutMembre.PERMANENT);
        if (membre.getUtilisateur() != null) {
            membre.getUtilisateur().setActif(true);
            utilisateurRepository.save(membre.getUtilisateur());
        }
        return toResponse(membreRepository.save(membre));
    }

    private Membre getEntity(Long id) {
        return membreRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Membre", id));
    }

    private void applyRequest(Membre membre, MembreRequest request) {
        membre.setNom(request.nom());
        membre.setPrenom(request.prenom());
        membre.setEmail(request.email());
        membre.setStatut(request.statut());
        membre.setPhotoUrl(request.photoUrl());
        membre.setEtablissementOrigine(request.etablissementOrigine());
        membre.setLaboratoireOrigine(request.laboratoireOrigine());
        membre.setBiographie(request.biographie());
        membre.setCentresInteret(request.centresInteret());
        membre.setDateNaissance(request.dateNaissance());
        membre.setDateEmbauche(request.dateEmbauche());
        membre.setDateAffiliation(request.dateAffiliation());

        if (request.equipeId() != null) {
            Equipe equipe = equipeRepository.findById(request.equipeId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Equipe", request.equipeId()));
            membre.setEquipeActuelle(equipe);
        } else {
            membre.setEquipeActuelle(null);
        }
    }

    private MembreResponse toResponse(Membre m) {
        Equipe equipe = m.getEquipeActuelle();
        return new MembreResponse(
                m.getId(),
                m.getNom(),
                m.getPrenom(),
                m.getEmail(),
                m.getPhotoUrl(),
                m.getStatut(),
                m.getEtablissementOrigine(),
                m.getLaboratoireOrigine(),
                m.getBiographie(),
                m.getCentresInteret(),
                m.getDateEmbauche(),
                m.getDateAffiliation(),
                equipe != null ? equipe.getId() : null,
                equipe != null ? equipe.getNom() : null,
                m.getUtilisateur() != null ? m.getUtilisateur().getId() : null,
                m.getCreatedAt(),
                m.getUpdatedAt());
    }
}
