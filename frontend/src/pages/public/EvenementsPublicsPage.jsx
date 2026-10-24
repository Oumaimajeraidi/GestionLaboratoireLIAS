import { useEffect, useState } from 'react'
import { getEvenements } from '../../api/evenementApi'
import { TYPE_EVENEMENT, formatDate } from '../../constants'
import Badge from '../../components/ui/Badge'
import Icon from '../../components/ui/Icon'
import { PageLoader } from '../../components/ui/Spinner'
import EmptyState from '../../components/ui/EmptyState'

const MONTHS = ['JAN', 'FÉV', 'MAR', 'AVR', 'MAI', 'JUI', 'JUIL', 'AOÛ', 'SEP', 'OCT', 'NOV', 'DÉC']

export default function EvenementsPublicsPage() {
  const [evenements, setEvenements] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    getEvenements().then(setEvenements).finally(() => setLoading(false))
  }, [])

  return (
    <div className="section">
      <h1 className="section-title">Événements scientifiques</h1>
      <p className="section-sub">Conférences, séminaires et workshops organisés par le laboratoire.</p>

      {loading ? (
        <PageLoader />
      ) : evenements.length === 0 ? (
        <div className="card"><EmptyState icon="calendar" title="Aucun événement" /></div>
      ) : (
        <div className="grid grid-3">
          {evenements.map((e) => {
            const t = TYPE_EVENEMENT[e.type] || { label: e.type, color: 'grey' }
            const d = e.dateDebut ? new Date(e.dateDebut) : null
            return (
              <div className="event-card" key={e.id}>
                <div className="ec-top">
                  <div className="ec-date">
                    <div className="d">{d ? d.getDate() : '—'}</div>
                    <div className="m">{d ? MONTHS[d.getMonth()] : ''}</div>
                  </div>
                  <Badge color={t.color}>{t.label}</Badge>
                </div>
                <div className="ec-body">
                  <h4>{e.titre}</h4>
                  <p className="muted" style={{ fontSize: 13.5, margin: 0 }}>{e.description}</p>
                </div>
                <div className="ec-foot">
                  <Icon name="mapPin" size={14} /> {e.lieu || 'Lieu à confirmer'}
                  {e.edition && <span>· {e.edition}</span>}
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
