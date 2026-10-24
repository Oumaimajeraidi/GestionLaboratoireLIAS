import { NavLink } from 'react-router-dom'
import Icon from '../ui/Icon'
import Avatar from '../ui/Avatar'
import { useAuth } from '../../context/AuthContext'
import { ROLE_LABELS } from '../../constants'

const MAIN_NAV = [
  { to: '/dashboard', icon: 'dashboard', label: 'Tableau de bord', end: true },
  { to: '/membres', icon: 'users', label: 'Membres' },
  { to: '/publications', icon: 'book', label: 'Publications' },
  { to: '/evenements', icon: 'calendar', label: 'Événements' },
  { to: '/calendrier', icon: 'clock', label: 'Calendrier' },
  { to: '/documents', icon: 'folder', label: 'Documents' },
  { to: '/messages', icon: 'messageCircle', label: 'Messages' },
  { to: '/recherche', icon: 'search', label: 'Recherche' },
]

const LABO_NAV = [
  { to: '/mandats', icon: 'shield', label: 'Gouvernance' },
  { to: '/conventions', icon: 'globe', label: 'Conventions' },
  { to: '/reunions', icon: 'fileText', label: 'Réunions / PV' },
  { to: '/materiels', icon: 'cpu', label: 'Matériel' },
  { to: '/rapport', icon: 'barChart', label: 'Rapport annuel' },
]

const ADMIN_NAV = [
  { to: '/demandes', icon: 'inbox', label: "Demandes d'adhésion" },
]

export default function Sidebar({ open }) {
  const { user, hasRole, logout } = useAuth()
  const isAdmin = hasRole('ADMIN') || hasRole('DIRECTEUR')
  const roleLabel = user?.roles?.length ? ROLE_LABELS[user.roles[0]] || 'Membre' : ''

  return (
    <aside className={`sidebar ${open ? 'open' : ''}`}>
      <div className="sidebar-brand">
        <div className="logo">L</div>
        <div>
          <div className="brand-name">LIAS</div>
          <div className="brand-sub">Laboratoire</div>
        </div>
      </div>

      <nav className="sidebar-nav">
        <div className="nav-section-label">Espace de travail</div>
        {MAIN_NAV.map((item) => (
          <NavLink key={item.to} to={item.to} end={item.end} className="nav-item">
            <Icon name={item.icon} />
            <span>{item.label}</span>
          </NavLink>
        ))}

        <div className="nav-section-label">Laboratoire</div>
        {LABO_NAV.map((item) => (
          <NavLink key={item.to} to={item.to} className="nav-item">
            <Icon name={item.icon} />
            <span>{item.label}</span>
          </NavLink>
        ))}

        {isAdmin && (
          <>
            <div className="nav-section-label">Administration</div>
            {ADMIN_NAV.map((item) => (
              <NavLink key={item.to} to={item.to} className="nav-item">
                <Icon name={item.icon} />
                <span>{item.label}</span>
              </NavLink>
            ))}
          </>
        )}
      </nav>

      <div className="sidebar-foot">
        <div className="sidebar-user">
          <Avatar name={user?.nomComplet} size="sm" />
          <div style={{ flex: 1, minWidth: 0 }}>
            <div className="u-name">{user?.nomComplet}</div>
            <div className="u-role">{roleLabel}</div>
          </div>
          <button className="icon-btn" onClick={logout} title="Déconnexion"
            style={{ width: 34, height: 34, background: 'transparent', borderColor: 'rgba(255,255,255,0.15)', color: '#c7d2fe' }}>
            <Icon name="logout" size={17} />
          </button>
        </div>
      </div>
    </aside>
  )
}
