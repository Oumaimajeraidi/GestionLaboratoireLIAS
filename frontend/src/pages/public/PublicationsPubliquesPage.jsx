import { useEffect, useState } from 'react'
import { getPublications } from '../../api/publicationApi'
import { TYPE_PUBLICATION } from '../../constants'
import Badge from '../../components/ui/Badge'
import Icon from '../../components/ui/Icon'
import { PageLoader } from '../../components/ui/Spinner'
import EmptyState from '../../components/ui/EmptyState'

export default function PublicationsPubliquesPage() {
  const [publications, setPublications] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getPublications().then(setPublications).finally(() => setLoading(false))
  }, [])

  return (
    <div className="section">
      <h1 className="section-title">Publications du laboratoire</h1>
      <p className="section-sub">La production scientifique du LIAS, en accès libre.</p>

      {loading ? (
        <PageLoader />
      ) : publications.length === 0 ? (
        <div className="card"><EmptyState icon="book" title="Aucune publication" /></div>
      ) : (
        <div className="card">
          {publications.map((p) => {
            const t = TYPE_PUBLICATION[p.type] || { label: p.type, color: 'grey' }
            return (
              <div className="pub-item" key={p.id}>
                <div className="pub-year">{p.annee || '—'}</div>
                <div style={{ flex: 1 }}>
                  <h4>{p.titre}</h4>
                  <div className="pub-meta">
                    <Badge color={t.color}>{t.label}</Badge>
                    {p.venue && <span><Icon name="book" size={13} /> {p.venue}</span>}
                    {p.auteurs?.length > 0 && <span><Icon name="users" size={13} /> {p.auteurs.map((a) => a.nomComplet).join(', ')}</span>}
                    {p.equipeNom && <span>· {p.equipeNom}</span>}
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
