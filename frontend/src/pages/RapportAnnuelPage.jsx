import { useState, useEffect } from 'react'
import { getRapportAnnuel } from '../api/rapportApi'
import { useToast } from '../context/ToastContext'
import { STATUT_MEMBRE, TYPE_PUBLICATION, TYPE_EVENEMENT } from '../constants'
import Button from '../components/ui/Button'
import Icon from '../components/ui/Icon'
import { PageLoader } from '../components/ui/Spinner'

const LABEL = (map, key) => map[key]?.label || key

function StatCard({ icon, value, label, color = 'indigo' }) {
  return (
    <div className="stat-card">
      <div className={`stat-icon bg-${color}`}><Icon name={icon} size={22} /></div>
      <div className="stat-value">{value}</div>
      <div className="stat-label">{label}</div>
    </div>
  )
}

function BarChart({ data, labelKey = 'key', valueKey = 'value', colorFn }) {
  if (!data || data.length === 0) return <div className="muted" style={{ textAlign: 'center', padding: 20 }}>Aucune donnée</div>
  const max = Math.max(...data.map(d => d[valueKey]), 1)
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
      {data.map((d, i) => (
        <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <div style={{ width: 160, fontSize: 13, textAlign: 'right', color: 'var(--text-soft)', flexShrink: 0, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{d[labelKey]}</div>
          <div style={{ flex: 1, height: 24, background: 'var(--slate-100)', borderRadius: 6, overflow: 'hidden' }}>
            <div style={{ width: `${(d[valueKey] / max) * 100}%`, height: '100%', background: colorFn ? colorFn(i) : 'var(--primary)', borderRadius: 6, transition: 'width .6s ease', minWidth: d[valueKey] > 0 ? 24 : 0, display: 'flex', alignItems: 'center', justifyContent: 'flex-end', paddingRight: 8, color: '#fff', fontSize: 12, fontWeight: 600 }}>
              {d[valueKey] > 0 && d[valueKey]}
            </div>
          </div>
        </div>
      ))}
    </div>
  )
}

export default function RapportAnnuelPage() {
  const toast = useToast()
  const currentYear = new Date().getFullYear()
  const [annee, setAnnee] = useState(currentYear)
  const [rapport, setRapport] = useState(null)
  const [loading, setLoading] = useState(false)

  useEffect(() => { charger() }, [annee])

  const charger = () => {
    setLoading(true)
    getRapportAnnuel(annee)
      .then(setRapport)
      .catch(() => toast.error('Erreur de chargement'))
      .finally(() => setLoading(false))
  }

  const imprimer = () => window.print()

  const COLORS = ['#6366f1', '#8b5cf6', '#3b82f6', '#10b981', '#f59e0b', '#ef4444']

  const membreData = rapport ? Object.entries(rapport.membresByStatut || {}).map(([k, v]) => ({ key: LABEL(STATUT_MEMBRE, k), value: v })) : []
  const pubData = rapport ? Object.entries(rapport.publicationsByType || {}).map(([k, v]) => ({ key: LABEL(TYPE_PUBLICATION, k), value: v })) : []
  const evData = rapport ? Object.entries(rapport.evenementsByType || {}).map(([k, v]) => ({ key: LABEL(TYPE_EVENEMENT, k), value: v })) : []

  return (
    <div className="rapport-page">
      <div className="page-header no-print">
        <div>
          <div className="ph-title">Rapport d'activité annuel</div>
          <div className="ph-sub">Données agrégées de l'année {annee}</div>
        </div>
        <div className="flex gap-2 items-center">
          <select className="select-filter" value={annee} onChange={e => setAnnee(+e.target.value)} style={{ minWidth: 100 }}>
            {Array.from({ length: 6 }, (_, i) => currentYear - i).map(y => (
              <option key={y} value={y}>{y}</option>
            ))}
          </select>
          <Button icon="printer" variant="ghost" onClick={imprimer}>Imprimer / PDF</Button>
        </div>
      </div>

      {/* Print header (only visible on print) */}
      <div className="print-only" style={{ textAlign: 'center', marginBottom: 24 }}>
        <h1 style={{ fontFamily: 'var(--font-display)', color: 'var(--indigo-900)' }}>Laboratoire LIAS</h1>
        <h2>Rapport d'activité — {annee}</h2>
        <p className="muted">Faculté des Sciences Ben M'Sik, Casablanca</p>
        <hr style={{ margin: '16px 0' }} />
      </div>

      {loading ? <PageLoader /> : !rapport ? null : (
        <>
          {/* KPI cards */}
          <div className="stats-grid mb-6">
            <StatCard icon="users" value={rapport.totalMembres} label="Membres au total" />
            <StatCard icon="plus" value={rapport.nouveauxMembres} label={`Nouveaux membres ${annee}`} color="green" />
            <StatCard icon="book" value={rapport.publicationsAnnee} label={`Publications ${annee}`} color="violet" />
            <StatCard icon="calendar" value={rapport.evenementsAnnee} label={`Événements ${annee}`} color="blue" />
            <StatCard icon="folder" value={rapport.documentsAnnee} label={`Documents ${annee}`} color="amber" />
            <StatCard icon="globe" value={rapport.conventionsActives} label="Conventions actives" color="green" />
            <StatCard icon="users" value={rapport.reunionsAnnee} label={`Réunions ${annee}`} color="indigo" />
            <StatCard icon="book" value={rapport.totalPublications} label="Publications totales" color="grey" />
          </div>

          <div className="grid grid-2 mb-6">
            {/* Membres par statut */}
            <div className="card">
              <h3 style={{ marginBottom: 16 }}>Membres par statut</h3>
              <BarChart data={membreData} colorFn={i => COLORS[i % COLORS.length]} />
            </div>

            {/* Publications de l'année par type */}
            <div className="card">
              <h3 style={{ marginBottom: 16 }}>Publications {annee} par type</h3>
              {pubData.length === 0
                ? <div className="muted" style={{ textAlign: 'center', padding: 20 }}>Aucune publication cette année</div>
                : <BarChart data={pubData} colorFn={i => COLORS[i % COLORS.length]} />
              }
            </div>

            {/* Événements de l'année par type */}
            <div className="card">
              <h3 style={{ marginBottom: 16 }}>Événements {annee} par type</h3>
              {evData.length === 0
                ? <div className="muted" style={{ textAlign: 'center', padding: 20 }}>Aucun événement cette année</div>
                : <BarChart data={evData} colorFn={i => COLORS[i % COLORS.length]} />
              }
            </div>

            {/* Top auteurs */}
            {rapport.topAuteurs && rapport.topAuteurs.length > 0 && (
              <div className="card">
                <h3 style={{ marginBottom: 16 }}>Top auteurs {annee}</h3>
                <BarChart data={rapport.topAuteurs.map(a => ({ key: a.nom, value: a.publications }))} colorFn={() => '#8b5cf6'} />
              </div>
            )}
          </div>

          {/* Summary table for print */}
          <div className="card print-summary">
            <h3 style={{ marginBottom: 12 }}>Résumé — {annee}</h3>
            <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 14 }}>
              <tbody>
                {[
                  ['Membres total', rapport.totalMembres],
                  [`Nouveaux membres ${annee}`, rapport.nouveauxMembres],
                  [`Publications ${annee}`, rapport.publicationsAnnee],
                  ['Total publications (historique)', rapport.totalPublications],
                  [`Événements ${annee}`, rapport.evenementsAnnee],
                  [`Documents ${annee}`, rapport.documentsAnnee],
                  ['Conventions actives', rapport.conventionsActives],
                  [`Réunions ${annee}`, rapport.reunionsAnnee],
                ].map(([k, v]) => (
                  <tr key={k} style={{ borderBottom: '1px solid var(--border)' }}>
                    <td style={{ padding: '8px 0', color: 'var(--text-soft)' }}>{k}</td>
                    <td style={{ padding: '8px 0', fontWeight: 600, textAlign: 'right' }}>{v}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  )
}
