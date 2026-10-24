import { Link } from 'react-router-dom'
import Icon from '../components/ui/Icon'
import { useAuth } from '../context/AuthContext'

const FEATURES = [
  { icon: 'users', title: 'Membres & équipes', text: 'Profils complets, statuts, rôles et historique des affiliations.' },
  { icon: 'book', title: 'Publications', text: 'Production scientifique classée par auteur, année et équipe.' },
  { icon: 'calendar', title: 'Événements', text: 'Conférences, séminaires et workshops avec archivage par édition.' },
  { icon: 'folder', title: 'Archivage documentaire', text: 'Programmes, attestations et rapports centralisés et recherchables.' },
  { icon: 'inbox', title: "Demandes d'adhésion", text: 'Soumission en ligne et validation par la direction.' },
  { icon: 'shield', title: 'Traçabilité', text: 'Toute action importante est historisée : une mémoire institutionnelle.' },
]

export default function HomePage() {
  const { user } = useAuth()

  return (
    <>
      <section className="hero">
        <div className="hero-inner">
          <span className="eyebrow"><Icon name="flask" size={15} /> Faculté des Sciences Ben M'Sik</span>
          <h1>La plateforme de gestion du laboratoire LIAS</h1>
          <p>
            Centralisez la gestion des membres, des activités scientifiques et de la mémoire
            du laboratoire dans une plateforme unique, traçable et collaborative.
          </p>
          <div className="hero-actions">
            <Link to={user ? '/dashboard' : '/login'} className="btn btn-primary btn-lg">
              <Icon name="dashboard" /> {user ? 'Mon espace' : 'Se connecter'}
            </Link>
            <Link to="/adhesion" className="btn btn-ghost btn-lg">
              <Icon name="mail" /> Demander une adhésion
            </Link>
          </div>
        </div>
      </section>

      <section className="section">
        <h2 className="section-title">Un système complet</h2>
        <p className="section-sub">
          Tous les modules nécessaires à la vie scientifique et administrative du laboratoire.
        </p>
        <div className="grid grid-3">
          {FEATURES.map((f) => (
            <div className="feature" key={f.title}>
              <div className="f-ico"><Icon name={f.icon} size={24} /></div>
              <h3>{f.title}</h3>
              <p>{f.text}</p>
            </div>
          ))}
        </div>
      </section>

      <div className="stat-band">
        <section className="section">
          <div className="sb-item"><div className="sb-val">6</div><div className="sb-label">Modules clés</div></div>
          <div className="sb-item"><div className="sb-val">2</div><div className="sb-label">Équipes de recherche</div></div>
          <div className="sb-item"><div className="sb-val">100%</div><div className="sb-label">Activités tracées</div></div>
          <div className="sb-item"><div className="sb-val">∞</div><div className="sb-label">Mémoire conservée</div></div>
        </section>
      </div>
    </>
  )
}
