import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { getMembres } from '../api/membreApi'
import { getPublications } from '../api/publicationApi'
import { getEvenements } from '../api/evenementApi'
import { getDocuments } from '../api/documentApi'
import { ROLE_LABELS, TYPE_EVENEMENT, formatDate } from '../constants'
import Icon from '../components/ui/Icon'
import Badge from '../components/ui/Badge'
import Card from '../components/ui/Card'
import { PageLoader } from '../components/ui/Spinner'

const STATS = [
  { key: 'membres', label: 'Membres', icon: 'users', color: 'bg-indigo', to: '/membres' },
  { key: 'publications', label: 'Publications', icon: 'book', color: 'bg-violet', to: '/publications' },
  { key: 'evenements', label: 'Événements', icon: 'calendar', color: 'bg-blue', to: '/evenements' },
  { key: 'documents', label: 'Documents', icon: 'folder', color: 'bg-green', to: '/documents' },
]

export default function DashboardPage() {
  const { user } = useAuth()
  const [data, setData] = useState(null)

  useEffect(() => {
    Promise.all([getMembres(), getPublications(), getEvenements(), getDocuments().catch(() => [])])
      .then(([membres, publications, evenements, documents]) =>
        setData({ membres, publications, evenements, documents }),
      )
      .catch(() => setData({ membres: [], publications: [], evenements: [], documents: [] }))
  }, [])

  if (!data) return <PageLoader />

  const counts = {
    membres: data.membres.length,
    publications: data.publications.length,
    evenements: data.evenements.length,
    documents: data.documents.length,
  }

  const today = new Date().toISOString().slice(0, 10)
  const upcoming = [...data.evenements]
    .filter((e) => e.dateDebut && e.dateDebut >= today)
    .sort((a, b) => a.dateDebut.localeCompare(b.dateDebut))
    .slice(0, 4)
  const recentPubs = [...data.publications].slice(0, 4)

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="ph-title">Bonjour, {user.nomComplet?.split(' ')[0]} 👋</div>
          <div className="ph-sub">Voici l'activité du laboratoire LIAS.</div>
        </div>
        <Badge color="indigo" dot>{user.roles?.map((r) => ROLE_LABELS[r]).join(' · ')}</Badge>
      </div>

      <div className="grid grid-4 mb-4">
        {STATS.map((s) => (
          <Link to={s.to} key={s.key} style={{ textDecoration: 'none' }}>
            <div className="stat">
              <div className={`stat-icon ${s.color}`}><Icon name={s.icon} /></div>
              <div>
                <div className="stat-val">{counts[s.key]}</div>
                <div className="stat-label">{s.label}</div>
              </div>
            </div>
          </Link>
        ))}
      </div>

      <div className="grid grid-2">
        <Card>
          <div className="card-head">
            <h3>Événements à venir</h3>
            <Link to="/calendrier" className="btn btn-soft btn-sm">Calendrier <Icon name="chevronRight" /></Link>
          </div>
          {upcoming.length === 0 ? (
            <p className="muted">Aucun événement à venir.</p>
          ) : (
            upcoming.map((e) => {
              const t = TYPE_EVENEMENT[e.type] || { label: e.type, color: 'grey' }
              return (
                <div className="pub-item" key={e.id}>
                  <div style={{ flex: 1 }}>
                    <h4>{e.titre}</h4>
                    <div className="pub-meta">
                      <Badge color={t.color}>{t.label}</Badge>
                      <span><Icon name="clock" size={13} /> {formatDate(e.dateDebut)}</span>
                      {e.lieu && <span><Icon name="mapPin" size={13} /> {e.lieu}</span>}
                    </div>
                  </div>
                </div>
              )
            })
          )}
        </Card>

        <Card>
          <div className="card-head">
            <h3>Dernières publications</h3>
            <Link to="/publications" className="btn btn-soft btn-sm">Tout voir <Icon name="chevronRight" /></Link>
          </div>
          {recentPubs.length === 0 ? (
            <p className="muted">Aucune publication.</p>
          ) : (
            recentPubs.map((p) => (
              <div className="pub-item" key={p.id}>
                <div className="pub-year">{p.annee || '—'}</div>
                <div style={{ flex: 1 }}>
                  <h4>{p.titre}</h4>
                  <div className="pub-meta">
                    {p.auteurs?.length > 0 && <span>{p.auteurs.map((a) => a.nomComplet).join(', ')}</span>}
                  </div>
                </div>
              </div>
            ))
          )}
        </Card>
      </div>
    </div>
  )
}
