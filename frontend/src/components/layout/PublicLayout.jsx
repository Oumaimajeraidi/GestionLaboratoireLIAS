import { Outlet, NavLink, Link } from 'react-router-dom'
import Icon from '../ui/Icon'
import { useAuth } from '../../context/AuthContext'

export default function PublicLayout() {
  const { user } = useAuth()

  return (
    <div className="public-shell">
      <header className="public-header">
        <Link to="/" className="brand-inline">
          <div className="logo">L</div>
          <span className="brand-name">LIAS</span>
        </Link>
        <nav className="links">
          <NavLink to="/" end>Accueil</NavLink>
          <NavLink to="/publications-publiques">Publications</NavLink>
          <NavLink to="/evenements-publics">Événements</NavLink>
          <NavLink to="/adhesion">Adhésion</NavLink>
        </nav>
        {user ? (
          <Link to="/dashboard" className="btn btn-primary btn-sm">
            <Icon name="dashboard" /> Espace de travail
          </Link>
        ) : (
          <Link to="/login" className="btn btn-primary btn-sm">
            <Icon name="user" /> Connexion
          </Link>
        )}
      </header>
      <main className="public-main">
        <Outlet />
      </main>
      <footer className="public-footer">
        Laboratoire LIAS &middot; Faculté des Sciences Ben M'Sik &middot; Casablanca — © 2026
      </footer>
    </div>
  )
}
