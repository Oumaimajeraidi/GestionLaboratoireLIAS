import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav className="navbar">
      <Link to="/" className="navbar-brand">
        <span className="brand-mark">LIAS</span>
        <span className="brand-sub">Laboratoire</span>
      </Link>

      <div className="navbar-links">
        <NavLink to="/" end>Accueil</NavLink>
        {user && <NavLink to="/dashboard">Tableau de bord</NavLink>}
        {user && <NavLink to="/membres">Membres</NavLink>}
      </div>

      <div className="navbar-auth">
        {user ? (
          <>
            <span className="navbar-user">{user.nomComplet}</span>
            <button className="btn btn-outline" onClick={handleLogout}>
              Deconnexion
            </button>
          </>
        ) : (
          <Link to="/login" className="btn btn-primary">
            Connexion
          </Link>
        )}
      </div>
    </nav>
  )
}
