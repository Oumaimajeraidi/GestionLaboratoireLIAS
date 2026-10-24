import { Link, useLocation } from 'react-router-dom'
import Icon from '../ui/Icon'

const TITLES = [
  { prefix: '/dashboard', title: 'Tableau de bord' },
  { prefix: '/membres', title: 'Membres' },
  { prefix: '/publications', title: 'Publications' },
  { prefix: '/evenements', title: 'Événements' },
  { prefix: '/calendrier', title: 'Calendrier' },
  { prefix: '/documents', title: 'Documents' },
  { prefix: '/demandes', title: "Demandes d'adhésion" },
  { prefix: '/profil', title: 'Mon profil' },
]

export default function Topbar({ onMenu }) {
  const { pathname } = useLocation()
  const match = TITLES.find((t) => pathname.startsWith(t.prefix))

  return (
    <header className="topbar">
      <button className="icon-btn menu-toggle" onClick={onMenu} aria-label="Menu">
        <Icon name="menu" size={20} />
      </button>
      <div className="page-title">{match ? match.title : 'LIAS'}</div>
      <div className="spacer" />
      <Link to="/" className="btn btn-ghost btn-sm">
        <Icon name="link" /> Site public
      </Link>
    </header>
  )
}
