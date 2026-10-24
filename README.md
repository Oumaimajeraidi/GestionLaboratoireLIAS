# Gestion du laboratoire LIAS

Application web de gestion du laboratoire **LIAS** (Faculté des Sciences Ben M'Sik, Casablanca).
Projet JEE — Master Data Science & Big Data 2025-2026.

- **Backend** : Spring Boot 3.5.15, Java 17, Spring Security + JWT, Spring Data JPA / Hibernate, H2 (dev) / MySQL (prod).
- **Frontend** : React 19 (Vite), React Router, Axios.

---

## Démarrage rapide

### Prérequis
- JDK 17 (`java -version`)
- Node 18+ et npm (`node -v`)
- Maven : non requis (l'app a été générée avec le Maven Wrapper `mvnw`).

### 1. Backend (port **8081**)
```bash
cd backend
./mvnw spring-boot:run        # Windows : mvnw.cmd spring-boot:run
```
> Le port **8081** est utilisé car 8080 est souvent occupé par Apache/XAMPP.
> Profil par défaut : `dev` → base **H2 en mémoire** + jeu de données de démonstration.
> Console H2 : http://localhost:8081/h2-console (JDBC URL : `jdbc:h2:mem:liasdb`, user `sa`, sans mot de passe).

### 2. Frontend (port **5173**)
```bash
cd frontend
npm install
npm run dev
```
Application : http://localhost:5173 (les appels `/api` sont relayés vers le backend 8081).

### Comptes de démonstration
| Email | Mot de passe | Rôles |
|-------|--------------|-------|
| admin@lias.ma | admin123 | ADMIN |
| directeur@lias.ma | directeur123 | DIRECTEUR, MEMBRE |
| membre@lias.ma | membre123 | MEMBRE |
| doctorant@lias.ma | doctorant123 | DOCTORANT |

---

## Architecture du backend (couches)
```
ma.fsbm.lias
├── entity        (+ entity.enums)   Entités JPA + énumérations
├── repository                       Spring Data JPA
├── service                          Logique métier
├── dto           (auth, membre, demande)  Objets d'échange API
├── controller                       Endpoints REST
├── security                         JWT, UserDetails, filtre d'authentification
├── config                           Sécurité, CORS, audit JPA, données de démo
└── exception                        Gestion centralisée des erreurs
```

### Principes métier respectés (cf. cahier des charges)
- **Séparation stricte Statut / Rôle** : `StatutMembre` (enum) ≠ `TypeResponsabilite` (lié à un mandat).
- **Historisation** : `Affiliation` (départs/retours), `AffectationRole` (changements de rôle), audit `createdAt/updatedAt` sur toutes les entités.
- **Aucune perte de données** : retraités → compte gelé (`actif=false`), anciens → compte désactivé.

---

## État d'avancement par module (cahier des charges)

| # | Module | Backend | Frontend |
|---|--------|:------:|:--------:|
| 2 | Typologie utilisateurs + auth (JWT) | ✅ | ✅ |
| 3 | Profils membres | ✅ CRUD | ✅ liste + détail + CRUD |
| 4 | Données temporelles / affiliations | 🟡 entités | ⬜ |
| 5 | Gouvernance / mandats | 🟡 entités | ⬜ |
| 6 | Demandes d'adhésion | ✅ workflow | ✅ form public + validation |
| 7 | Rôles & responsabilités | 🟡 entités | ⬜ |
| 8 | Départ / retraite | 🟡 modèle | ⬜ |
| 9 | Événements scientifiques | ✅ CRUD | ✅ cartes + CRUD |
| 10 | Archivage documentaire | ✅ upload/download | ✅ upload + liste |
| 11 | Rapport d'activité annuel | ⬜ | ⬜ |
| 12 | Publications | ✅ CRUD | ✅ liste + CRUD |
| 13 | Communication interne | ⬜ | ⬜ |
| 14 | Gestion du matériel | ⬜ | ⬜ |
| 15 | Besoins en matériel | ⬜ | ⬜ |
| 16 | Conventions & collaborations | ⬜ | ⬜ |
| 17 | Procès-verbaux (PV) | ⬜ | ⬜ |
| 18 | Recherche globale | 🟡 membres | 🟡 membres |
| 19 | Notifications | ⬜ | ⬜ |
| 20 | Calendrier | ✅ | ✅ vue mensuelle |

### Design
Interface React professionnelle : design system indigo/violet (`src/index.css`), layout dashboard
(sidebar + topbar), composants réutilisables (`src/components/ui`), modales, toasts, et espace
public distinct de l'espace de travail authentifié.

✅ terminé · 🟡 partiel (modèle prêt, à exposer/UI) · ⬜ à faire

---

## API disponible
| Méthode | URL | Accès |
|---------|-----|-------|
| POST | `/api/auth/login` | public |
| GET  | `/api/auth/me` | authentifié |
| GET  | `/api/membres` (`?q=`) | authentifié |
| GET  | `/api/membres/{id}` | authentifié |
| POST | `/api/membres` | ADMIN, DIRECTEUR |
| PUT  | `/api/membres/{id}` | ADMIN, DIRECTEUR |
| DELETE | `/api/membres/{id}` | ADMIN |

## Production (MySQL)
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
# Variables : DB_USER, DB_PASSWORD, JWT_SECRET
```
