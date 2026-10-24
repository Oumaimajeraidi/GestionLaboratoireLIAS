// Libelles + couleurs de badges, partages dans toute l'application.

export const STATUT_MEMBRE = {
  PERMANENT: { label: 'Permanent', color: 'green' },
  ASSOCIE: { label: 'Associé', color: 'blue' },
  DOCTORANT: { label: 'Doctorant', color: 'amber' },
  RETRAITE: { label: 'Retraité', color: 'grey' },
  ANCIEN: { label: 'Ancien', color: 'grey' },
}

export const TYPE_PUBLICATION = {
  ARTICLE_REVUE: { label: 'Article de revue', color: 'indigo' },
  CONFERENCE: { label: 'Conférence', color: 'violet' },
  CHAPITRE_LIVRE: { label: 'Chapitre de livre', color: 'blue' },
  LIVRE: { label: 'Livre', color: 'green' },
  THESE: { label: 'Thèse', color: 'amber' },
  RAPPORT: { label: 'Rapport', color: 'grey' },
  AUTRE: { label: 'Autre', color: 'grey' },
}

export const TYPE_EVENEMENT = {
  CONFERENCE: { label: 'Conférence', color: 'indigo' },
  SEMINAIRE: { label: 'Séminaire', color: 'violet' },
  WORKSHOP: { label: 'Workshop', color: 'blue' },
}

export const TYPE_DOCUMENT = {
  DEMANDE_FINANCEMENT: { label: 'Demande de financement', color: 'amber' },
  PROGRAMME: { label: 'Programme', color: 'blue' },
  ATTESTATION: { label: 'Attestation', color: 'green' },
  RAPPORT: { label: 'Rapport', color: 'indigo' },
  ADMINISTRATIF: { label: 'Administratif', color: 'grey' },
  AUTRE: { label: 'Autre', color: 'grey' },
}

export const STATUT_DEMANDE = {
  EN_ATTENTE: { label: 'En attente', color: 'amber' },
  ACCEPTEE: { label: 'Acceptée', color: 'green' },
  REFUSEE: { label: 'Refusée', color: 'red' },
}

export const STATUT_BESOIN = {
  EN_ATTENTE: { label: 'En attente', color: 'amber' },
  VALIDE: { label: 'Validé', color: 'green' },
  REFUSE: { label: 'Refusé', color: 'red' },
}

export const ETAT_MATERIEL = {
  DISPONIBLE: { label: 'Disponible', color: 'green' },
  ATTRIBUE: { label: 'Attribué', color: 'indigo' },
  EN_MAINTENANCE: { label: 'En maintenance', color: 'amber' },
  HORS_SERVICE: { label: 'Hors service', color: 'red' },
}

export const STATUT_CONVENTION = {
  EN_NEGOCIATION: { label: 'En négociation', color: 'amber' },
  ACTIVE: { label: 'Active', color: 'green' },
  SUSPENDUE: { label: 'Suspendue', color: 'grey' },
  EXPIREE: { label: 'Expirée', color: 'red' },
}

export const TYPE_CONVENTION = {
  CO_TUTELLE: { label: 'Co-tutelle', color: 'violet' },
  PARTENARIAT_RECHERCHE: { label: 'Partenariat de recherche', color: 'indigo' },
  ECHANGE_ETUDIANT: { label: 'Échange étudiant', color: 'blue' },
  MOBILITE: { label: 'Mobilité', color: 'green' },
  ACCORD_CADRE: { label: 'Accord-cadre', color: 'amber' },
}

export const TYPE_REUNION = {
  CONSEIL_LABO: { label: 'Conseil de laboratoire', color: 'indigo' },
  REUNION_EQUIPE: { label: 'Réunion d\'équipe', color: 'violet' },
  COMMISSION: { label: 'Commission', color: 'blue' },
  AUTRE: { label: 'Autre', color: 'grey' },
}

export const TYPE_NOTIFICATION = {
  NOUVEAU_MEMBRE: { label: 'Nouveau membre', color: 'green', icon: 'users' },
  EVENEMENT: { label: 'Événement', color: 'indigo', icon: 'calendar' },
  DOCUMENT: { label: 'Document', color: 'amber', icon: 'file' },
  DEMANDE: { label: 'Demande', color: 'violet', icon: 'inbox' },
  MATERIEL: { label: 'Matériel', color: 'blue', icon: 'cpu' },
  CONVENTION: { label: 'Convention', color: 'green', icon: 'globe' },
  MESSAGE: { label: 'Message', color: 'grey', icon: 'mail' },
  REUNION: { label: 'Réunion', color: 'amber', icon: 'users' },
}

export const TYPE_RESPONSABILITE = {
  DIRECTEUR: { label: 'Directeur', color: 'indigo' },
  VICE_DIRECTEUR: { label: 'Vice-directeur', color: 'violet' },
  CHEF_EQUIPE: { label: 'Chef d\'équipe', color: 'blue' },
  MEMBRE_EQUIPE: { label: 'Membre d\'équipe', color: 'grey' },
}

export const ROLE_LABELS = {
  ROLE_ADMIN: 'Administrateur',
  ROLE_DIRECTEUR: 'Directeur',
  ROLE_MEMBRE: 'Membre',
  ROLE_ASSOCIE: 'Associé',
  ROLE_DOCTORANT: 'Doctorant',
}

export const enumOptions = (map) =>
  Object.entries(map).map(([value, { label }]) => ({ value, label }))

export const formatDate = (iso) => {
  if (!iso) return '—'
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return iso
  return d.toLocaleDateString('fr-FR', { day: '2-digit', month: 'short', year: 'numeric' })
}

export const formatSize = (bytes) => {
  if (bytes == null) return '—'
  if (bytes < 1024) return `${bytes} o`
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} Ko`
  return `${(bytes / (1024 * 1024)).toFixed(1)} Mo`
}
