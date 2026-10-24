package ma.fsbm.lias.service;

import java.util.Set;
import java.util.stream.Collectors;
import ma.fsbm.lias.dto.auth.AuthResponse;
import ma.fsbm.lias.dto.auth.LoginRequest;
import ma.fsbm.lias.entity.Membre;
import ma.fsbm.lias.entity.Utilisateur;
import ma.fsbm.lias.exception.ResourceNotFoundException;
import ma.fsbm.lias.repository.MembreRepository;
import ma.fsbm.lias.repository.UtilisateurRepository;
import ma.fsbm.lias.security.JwtService;
import ma.fsbm.lias.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository utilisateurRepository;
    private final MembreRepository membreRepository;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       UtilisateurRepository utilisateurRepository,
                       MembreRepository membreRepository,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.utilisateurRepository = utilisateurRepository;
        this.membreRepository = membreRepository;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.motDePasse()));

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);
        return buildResponse(principal.getUtilisateur(), token);
    }

    @Transactional(readOnly = true)
    public AuthResponse currentUser(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + email));
        return buildResponse(utilisateur, null);
    }

    private AuthResponse buildResponse(Utilisateur utilisateur, String token) {
        Set<String> roles = utilisateur.getRoles().stream()
                .map(r -> "ROLE_" + r.name())
                .collect(Collectors.toSet());

        Membre membre = membreRepository.findByUtilisateurId(utilisateur.getId()).orElse(null);
        Long membreId = membre != null ? membre.getId() : null;
        String nomComplet = membre != null
                ? membre.getPrenom() + " " + membre.getNom()
                : utilisateur.getEmail();

        return AuthResponse.bearer(token, utilisateur.getId(), utilisateur.getEmail(),
                roles, membreId, nomComplet);
    }
}
