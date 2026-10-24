package ma.fsbm.lias.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import ma.fsbm.lias.dto.demande.DecisionRequest;
import ma.fsbm.lias.dto.demande.DemandeAccepteeResponse;
import ma.fsbm.lias.dto.demande.DemandeAdhesionRequest;
import ma.fsbm.lias.dto.demande.DemandeResponse;
import ma.fsbm.lias.entity.DemandeAdhesion;
import ma.fsbm.lias.entity.Membre;
import ma.fsbm.lias.entity.Utilisateur;
import ma.fsbm.lias.entity.enums.RoleSecurite;
import ma.fsbm.lias.entity.enums.StatutDemande;
import ma.fsbm.lias.entity.enums.StatutMembre;
import ma.fsbm.lias.exception.BusinessException;
import ma.fsbm.lias.exception.ResourceNotFoundException;
import ma.fsbm.lias.repository.DemandeAdhesionRepository;
import ma.fsbm.lias.repository.MembreRepository;
import ma.fsbm.lias.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DemandeAdhesionService {

    private static final String ALPHABET =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final DemandeAdhesionRepository demandeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final MembreRepository membreRepository;
    private final PasswordEncoder passwordEncoder;

    public DemandeAdhesionService(DemandeAdhesionRepository demandeRepository,
                                  UtilisateurRepository utilisateurRepository,
                                  MembreRepository membreRepository,
                                  PasswordEncoder passwordEncoder) {
        this.demandeRepository = demandeRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.membreRepository = membreRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Soumission publique d'une demande d'adhesion. */
    public DemandeResponse soumettre(DemandeAdhesionRequest request) {
        DemandeAdhesion demande = new DemandeAdhesion();
        demande.setNom(request.nom());
        demande.setPrenom(request.prenom());
        demande.setEmail(request.email());
        demande.setEtablissementOrigine(request.etablissementOrigine());
        demande.setMotivation(request.motivation());
        demande.setCvUrl(request.cvUrl());
        demande.setStatut(StatutDemande.EN_ATTENTE);
        return toResponse(demandeRepository.save(demande));
    }

    @Transactional(readOnly = true)
    public List<DemandeResponse> findAll(StatutDemande statut) {
        List<DemandeAdhesion> demandes = (statut != null)
                ? demandeRepository.findByStatutOrderByCreatedAtDesc(statut)
                : demandeRepository.findAllByOrderByCreatedAtDesc();
        return demandes.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DemandeResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    /** Acceptation par le directeur : cree le compte + le profil membre. */
    public DemandeAccepteeResponse accepter(Long id, DecisionRequest decision) {
        DemandeAdhesion demande = getEntity(id);
        verifierEnAttente(demande);

        if (utilisateurRepository.existsByEmail(demande.getEmail())) {
            throw new BusinessException("Un compte existe deja avec l'email " + demande.getEmail());
        }

        String motDePasse = genererMotDePasse();
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(demande.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(motDePasse));
        utilisateur.setActif(true);
        utilisateur.setRoles(Set.of(RoleSecurite.MEMBRE));
        utilisateur = utilisateurRepository.save(utilisateur);

        StatutMembre statut = decision != null && decision.statut() != null
                ? decision.statut() : StatutMembre.PERMANENT;

        Membre membre = new Membre();
        membre.setNom(demande.getNom());
        membre.setPrenom(demande.getPrenom());
        membre.setEmail(demande.getEmail());
        membre.setStatut(statut);
        membre.setEtablissementOrigine(demande.getEtablissementOrigine());
        membre.setUtilisateur(utilisateur);
        membre.setDateAffiliation(LocalDate.now());
        membre = membreRepository.save(membre);

        demande.setStatut(StatutDemande.ACCEPTEE);
        demande.setDateDecision(LocalDateTime.now());
        demande.setCommentaireDecision(decision != null ? decision.commentaire() : null);
        demandeRepository.save(demande);

        return new DemandeAccepteeResponse(demande.getId(), membre.getId(),
                utilisateur.getId(), utilisateur.getEmail(), motDePasse);
    }

    /** Refus par le directeur, avec motif communique. */
    public DemandeResponse refuser(Long id, DecisionRequest decision) {
        DemandeAdhesion demande = getEntity(id);
        verifierEnAttente(demande);
        demande.setStatut(StatutDemande.REFUSEE);
        demande.setCommentaireDecision(decision != null ? decision.commentaire() : null);
        demande.setDateDecision(LocalDateTime.now());
        return toResponse(demandeRepository.save(demande));
    }

    private void verifierEnAttente(DemandeAdhesion demande) {
        if (demande.getStatut() != StatutDemande.EN_ATTENTE) {
            throw new BusinessException("Cette demande a deja ete traitee");
        }
    }

    private DemandeAdhesion getEntity(Long id) {
        return demandeRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Demande d'adhesion", id));
    }

    private String genererMotDePasse() {
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private DemandeResponse toResponse(DemandeAdhesion d) {
        return new DemandeResponse(
                d.getId(), d.getNom(), d.getPrenom(), d.getEmail(), d.getEtablissementOrigine(),
                d.getMotivation(), d.getCvUrl(), d.getStatut(), d.getCommentaireDecision(),
                d.getDateDecision(), d.getCreatedAt());
    }
}
