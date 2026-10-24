package ma.fsbm.lias.controller;

import jakarta.validation.Valid;
import ma.fsbm.lias.dto.auth.AuthResponse;
import ma.fsbm.lias.dto.auth.LoginRequest;
import ma.fsbm.lias.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /** Renvoie les informations de l'utilisateur authentifie (sans nouveau token). */
    @GetMapping("/me")
    public AuthResponse me(Authentication authentication) {
        return authService.currentUser(authentication.getName());
    }
}
