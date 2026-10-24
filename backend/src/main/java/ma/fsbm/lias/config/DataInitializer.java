package ma.fsbm.lias.config;

import java.time.LocalDate;
import java.util.Set;
import ma.fsbm.lias.entity.Convention;
import ma.fsbm.lias.entity.DemandeAdhesion;
import ma.fsbm.lias.entity.Equipe;
import ma.fsbm.lias.entity.Evenement;
import ma.fsbm.lias.entity.Mandat;
import ma.fsbm.lias.entity.Materiel;
import ma.fsbm.lias.entity.Membre;
import ma.fsbm.lias.entity.Partenaire;
import ma.fsbm.lias.entity.Publication;
import ma.fsbm.lias.entity.Reunion;
import ma.fsbm.lias.entity.Utilisateur;
import ma.fsbm.lias.entity.enums.EtatMateriel;
import ma.fsbm.lias.entity.enums.RoleSecurite;
import ma.fsbm.lias.entity.enums.StatutConvention;
import ma.fsbm.lias.entity.enums.StatutDemande;
import ma.fsbm.lias.entity.enums.StatutMembre;
import ma.fsbm.lias.entity.enums.TypeConvention;
import ma.fsbm.lias.entity.enums.TypeEvenement;
import ma.fsbm.lias.entity.enums.TypePublication;
import ma.fsbm.lias.entity.enums.TypeReunion;
import ma.fsbm.lias.repository.ConventionRepository;
import ma.fsbm.lias.repository.DemandeAdhesionRepository;
import ma.fsbm.lias.repository.EquipeRepository;
import ma.fsbm.lias.repository.EvenementRepository;
import ma.fsbm.lias.repository.MandatRepository;
import ma.fsbm.lias.repository.MaterielRepository;
import ma.fsbm.lias.repository.MembreRepository;
import ma.fsbm.lias.repository.PartenaireRepository;
import ma.fsbm.lias.repository.PublicationRepository;
import ma.fsbm.lias.repository.ReunionRepository;
import ma.fsbm.lias.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initialise des donnees de demonstration au demarrage (profil dev / H2 uniquement).
 * Comptes :
 *   - admin@lias.ma     / admin123     (ADMIN)
 *   - directeur@lias.ma / directeur123 (DIRECTEUR + MEMBRE)
 *   - membre@lias.ma    / membre123    (MEMBRE)
 *   - doctorant@lias.ma / doctorant123 (DOCTORANT)
 */
@Component
@Profile({"dev", "prod"})
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final MembreRepository membreRepository;
    private final EquipeRepository equipeRepository;
    private final PublicationRepository publicationRepository;
    private final EvenementRepository evenementRepository;
    private final DemandeAdhesionRepository demandeRepository;
    private final MandatRepository mandatRepository;
    private final MaterielRepository materielRepository;
    private final PartenaireRepository partenaireRepository;
    private final ConventionRepository conventionRepository;
    private final ReunionRepository reunionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UtilisateurRepository utilisateurRepository,
                           MembreRepository membreRepository,
                           EquipeRepository equipeRepository,
                           PublicationRepository publicationRepository,
                           EvenementRepository evenementRepository,
                           DemandeAdhesionRepository demandeRepository,
                           MandatRepository mandatRepository,
                           MaterielRepository materielRepository,
                           PartenaireRepository partenaireRepository,
                           ConventionRepository conventionRepository,
                           ReunionRepository reunionRepository,
                           PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.membreRepository = membreRepository;
        this.equipeRepository = equipeRepository;
        this.publicationRepository = publicationRepository;
        this.evenementRepository = evenementRepository;
        this.demandeRepository = demandeRepository;
        this.mandatRepository = mandatRepository;
        this.materielRepository = materielRepository;
        this.partenaireRepository = partenaireRepository;
        this.conventionRepository = conventionRepository;
        this.reunionRepository = reunionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (utilisateurRepository.count() > 0) {
            return;
        }

        // --- Equipes ---
        Equipe dataScience = creerEquipe("Data Science & IA",
                "Apprentissage automatique, deep learning et traitement du langage naturel.",
                "Intelligence Artificielle");
        Equipe bigData = creerEquipe("Big Data & Systemes Distribues",
                "Architectures de donnees massives, calcul distribue et cloud.",
                "Big Data");

        // --- Administrateur systeme ---
        creerUtilisateur("admin@lias.ma", "admin123", Set.of(RoleSecurite.ADMIN));

        // --- Directeur (compte + profil membre) ---
        Utilisateur dirUser = creerUtilisateur("directeur@lias.ma", "directeur123",
                Set.of(RoleSecurite.DIRECTEUR, RoleSecurite.MEMBRE));
        Membre directeur = creerMembre("Alaoui", "Karim", "directeur@lias.ma", StatutMembre.PERMANENT,
                dirUser, dataScience, LocalDate.of(2010, 9, 1));

        // --- Membre permanent ---
        Utilisateur membreUser = creerUtilisateur("membre@lias.ma", "membre123",
                Set.of(RoleSecurite.MEMBRE));
        Membre membre = creerMembre("Bennani", "Salma", "membre@lias.ma", StatutMembre.PERMANENT,
                membreUser, bigData, LocalDate.of(2018, 1, 15));

        // --- Doctorant ---
        Utilisateur doctorantUser = creerUtilisateur("doctorant@lias.ma", "doctorant123",
                Set.of(RoleSecurite.DOCTORANT));
        Membre doctorant = creerMembre("Idrissi", "Youssef", "doctorant@lias.ma", StatutMembre.DOCTORANT,
                doctorantUser, dataScience, LocalDate.of(2023, 10, 1));

        // --- Publications ---
        creerPublication("Deep Learning pour la detection d'anomalies reseau",
                TypePublication.ARTICLE_REVUE, 2024, "IEEE Transactions on Neural Networks",
                dataScience, Set.of(directeur, doctorant));
        creerPublication("Optimisation du calcul distribue sur clusters heterogenes",
                TypePublication.CONFERENCE, 2023, "ACM SIGMOD", bigData, Set.of(membre));
        creerPublication("Une approche hybride pour le NLP arabe",
                TypePublication.ARTICLE_REVUE, 2025, "Journal of AI Research",
                dataScience, Set.of(directeur));

        // --- Evenements ---
        creerEvenement("Conference Internationale sur l'IA et les Donnees",
                TypeEvenement.CONFERENCE, "Edition annuelle reunissant chercheurs et industriels.",
                LocalDate.of(2025, 11, 12), LocalDate.of(2025, 11, 14),
                "Faculte des Sciences Ben M'Sik", "2025", Set.of(directeur, membre));
        creerEvenement("Seminaire : Methodes avancees de Machine Learning",
                TypeEvenement.SEMINAIRE, "Presentation des travaux recents de l'equipe Data Science.",
                LocalDate.of(2026, 3, 5), LocalDate.of(2026, 3, 5),
                "Amphi A", "3eme edition", Set.of(directeur));
        creerEvenement("Workshop Big Data & Cloud",
                TypeEvenement.WORKSHOP, "Ateliers pratiques sur Spark et Kubernetes.",
                LocalDate.of(2026, 5, 20), LocalDate.of(2026, 5, 21),
                "Salle informatique", "2026", Set.of(membre));

        // --- Demande d'adhesion en attente ---
        DemandeAdhesion demande = new DemandeAdhesion();
        demande.setNom("Tazi");
        demande.setPrenom("Imane");
        demande.setEmail("imane.tazi@example.com");
        demande.setEtablissementOrigine("ENSIAS, Rabat");
        demande.setMotivation("Je souhaite rejoindre le laboratoire LIAS pour mes travaux en IA.");
        demande.setStatut(StatutDemande.EN_ATTENTE);
        demandeRepository.save(demande);

        // --- Mandat actif ---
        Mandat mandat = new Mandat();
        mandat.setIntitule("Mandat 2022-2026");
        mandat.setDateDebut(LocalDate.of(2022, 9, 1));
        mandat.setDateFin(LocalDate.of(2026, 8, 31));
        mandat.setActif(true);
        mandat.setDirecteur(directeur);
        mandat.setViceDirecteur(membre);
        mandatRepository.save(mandat);

        // --- Matériel ---
        creerMateriel("Serveur Dell PowerEdge R740", "Serveur de calcul haute performance", "SRV-001",
                2, EtatMateriel.DISPONIBLE, LocalDate.of(2023, 3, 15), null);
        creerMateriel("Laptop HP EliteBook 850", "Poste de travail mobile", "LPT-002",
                5, EtatMateriel.DISPONIBLE, LocalDate.of(2024, 1, 10), null);
        creerMateriel("GPU NVIDIA A100", "Accélérateur GPU pour deep learning", "GPU-003",
                1, EtatMateriel.ATTRIBUE, LocalDate.of(2023, 6, 20), directeur);

        // --- Partenaire & Convention ---
        Partenaire um5 = new Partenaire();
        um5.setNom("Université Mohammed V Rabat");
        um5.setPays("Maroc");
        um5.setTypeInstitution("Université");
        um5.setContactNom("Pr. Hassan Amrani");
        um5.setContactEmail("h.amrani@um5.ac.ma");
        partenaireRepository.save(um5);

        Partenaire uca = new Partenaire();
        uca.setNom("Université Côte d'Azur");
        uca.setPays("France");
        uca.setTypeInstitution("Université");
        uca.setContactNom("Prof. Marie Dupont");
        uca.setContactEmail("marie.dupont@uca.fr");
        partenaireRepository.save(uca);

        Convention conv1 = new Convention();
        conv1.setIntitule("Co-tutelle de thèse LIAS-UM5");
        conv1.setType(TypeConvention.CO_TUTELLE);
        conv1.setStatut(StatutConvention.ACTIVE);
        conv1.setPartenaire(um5);
        conv1.setDateDebut(LocalDate.of(2023, 9, 1));
        conv1.setDateFin(LocalDate.of(2027, 8, 31));
        conv1.setObjectif("Encadrement conjoint de doctorants en intelligence artificielle.");
        conventionRepository.save(conv1);

        Convention conv2 = new Convention();
        conv2.setIntitule("Partenariat de Recherche LIAS-UCA");
        conv2.setType(TypeConvention.PARTENARIAT_RECHERCHE);
        conv2.setStatut(StatutConvention.ACTIVE);
        conv2.setPartenaire(uca);
        conv2.setDateDebut(LocalDate.of(2024, 1, 1));
        conv2.setDateFin(LocalDate.of(2026, 12, 31));
        conv2.setObjectif("Collaboration sur les projets Big Data et IA distribuée.");
        conventionRepository.save(conv2);

        // --- Réunions ---
        Reunion r1 = new Reunion();
        r1.setTitre("Conseil de laboratoire — Bilan 2024");
        r1.setType(TypeReunion.CONSEIL_LABO);
        r1.setDateReunion(LocalDate.of(2025, 1, 20));
        r1.setLieu("Salle de conférences A");
        r1.setOrdreJour("1. Bilan des activités 2024\n2. Plan d'action 2025\n3. Questions diverses");
        reunionRepository.save(r1);

        Reunion r2 = new Reunion();
        r2.setTitre("Réunion équipe Data Science");
        r2.setType(TypeReunion.REUNION_EQUIPE);
        r2.setDateReunion(LocalDate.of(2026, 3, 10));
        r2.setLieu("Salle informatique");
        r2.setOrdreJour("1. Avancement projets en cours\n2. Nouvelles soumissions");
        r2.setEquipe(dataScience);
        reunionRepository.save(r2);
    }

    private Equipe creerEquipe(String nom, String description, String thematique) {
        Equipe equipe = new Equipe();
        equipe.setNom(nom);
        equipe.setDescription(description);
        equipe.setThematique(thematique);
        return equipeRepository.save(equipe);
    }

    private Utilisateur creerUtilisateur(String email, String motDePasse, Set<RoleSecurite> roles) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(passwordEncoder.encode(motDePasse));
        utilisateur.setActif(true);
        utilisateur.setRoles(roles);
        return utilisateurRepository.save(utilisateur);
    }

    private Membre creerMembre(String nom, String prenom, String email, StatutMembre statut,
                               Utilisateur utilisateur, Equipe equipe, LocalDate dateAffiliation) {
        Membre membre = new Membre();
        membre.setNom(nom);
        membre.setPrenom(prenom);
        membre.setEmail(email);
        membre.setStatut(statut);
        membre.setUtilisateur(utilisateur);
        membre.setEquipeActuelle(equipe);
        membre.setDateAffiliation(dateAffiliation);
        membre.setDateEmbauche(dateAffiliation);
        membre.setBiographie("Membre du laboratoire LIAS, equipe " + equipe.getNom() + ".");
        membre.setEtablissementOrigine("Faculte des Sciences Ben M'Sik");
        membre.setCentresInteret("Recherche, Intelligence Artificielle, Donnees");
        return membreRepository.save(membre);
    }

    private void creerPublication(String titre, TypePublication type, Integer annee, String venue,
                                  Equipe equipe, Set<Membre> auteurs) {
        Publication publication = new Publication();
        publication.setTitre(titre);
        publication.setType(type);
        publication.setAnnee(annee);
        publication.setVenue(venue);
        publication.setEquipe(equipe);
        publication.setAuteurs(new java.util.HashSet<>(auteurs));
        publicationRepository.save(publication);
    }

    private Materiel creerMateriel(String nom, String description, String ref, int qte, EtatMateriel etat, LocalDate dateArrivee, Membre attribueA) {
        Materiel m = new Materiel();
        m.setNom(nom); m.setDescription(description); m.setReference(ref);
        m.setQuantite(qte); m.setEtat(etat); m.setDateArrivee(dateArrivee);
        m.setAttribueA(attribueA);
        return materielRepository.save(m);
    }

    private void creerEvenement(String titre, TypeEvenement type, String description,
                                LocalDate dateDebut, LocalDate dateFin, String lieu, String edition,
                                Set<Membre> organisateurs) {
        Evenement evenement = new Evenement();
        evenement.setTitre(titre);
        evenement.setType(type);
        evenement.setDescription(description);
        evenement.setDateDebut(dateDebut);
        evenement.setDateFin(dateFin);
        evenement.setLieu(lieu);
        evenement.setEdition(edition);
        evenement.setOrganisateurs(new java.util.HashSet<>(organisateurs));
        evenementRepository.save(evenement);
    }
}
