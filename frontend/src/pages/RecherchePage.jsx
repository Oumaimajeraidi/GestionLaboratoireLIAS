import { useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { search } from '../api/searchApi'
import { useToast } from '../context/ToastContext'
import Icon from '../components/ui/Icon'
import Badge from '../components/ui/Badge'
import { PageLoader } from '../components/ui/Spinner'
import EmptyState from '../components/ui/EmptyState'

const TYPE_CONFIG = {
  membre: { label: 'Membre', color: 'indigo', icon: 'user' },
  publication: { label: 'Publication', color: 'violet', icon: 'book' },
  evenement: { label: 'Événement', color: 'blue', icon: 'calendar' },
  document: { label: 'Document', color: 'amber', icon: 'file' },
}

export default function RecherchePage() {
  const toast = useToast()
  const navigate = useNavigate()
  const [query, setQuery] = useState('')
  const [results, setResults] = useState(null)
  const [loading, setLoading] = useState(false)

  const doSearch = useCallback(async (q) => {
    if (!q || q.trim().length < 2) return
    setLoading(true)
    try {
      const data = await search(q.trim())
      setResults(data)
    } catch {
      toast.error('Erreur de recherche')
    } finally {
      setLoading(false)
    }
  }, [toast])

  const handleSubmit = (e) => { e.preventDefault(); doSearch(query) }

  const handleKey = (e) => {
    if (e.key === 'Enter') doSearch(query)
  }

  const goTo = (item) => {
    if (item.url) navigate(item.url)
  }

  const grouped = results ? results.reduce((acc, r) => {
    if (!acc[r.type]) acc[r.type] = []
    acc[r.type].push(r)
    return acc
  }, {}) : null

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="ph-title">Recherche globale</div>
          <div className="ph-sub">Cherchez parmi les membres, publications, événements et documents</div>
        </div>
      </div>

      {/* Search bar */}
      <div className="card mb-6">
        <form onSubmit={handleSubmit} style={{ display: 'flex', gap: 12 }}>
          <div style={{ flex: 1, position: 'relative' }}>
            <Icon name="search" size={18} style={{ position: 'absolute', left: 14, top: '50%', transform: 'translateY(-50%)', color: 'var(--text-soft)' }} />
            <input
              style={{ width: '100%', paddingLeft: 44 }}
              placeholder="Rechercher un membre, publication, événement, document…"
              value={query}
              onChange={e => setQuery(e.target.value)}
              onKeyDown={handleKey}
              autoFocus
            />
          </div>
          <button type="submit" className="btn btn-primary" style={{ minWidth: 120 }}>
            <Icon name="search" size={16} /> Rechercher
          </button>
        </form>
      </div>

      {loading ? <PageLoader /> : results === null ? (
        <div className="card"><EmptyState icon="search" title="Entrez votre recherche" description="Minimum 2 caractères pour lancer la recherche." /></div>
      ) : results.length === 0 ? (
        <div className="card"><EmptyState icon="search" title="Aucun résultat" description={`Aucun résultat pour "${query}"`} /></div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          {Object.entries(grouped).map(([type, items]) => {
            const cfg = TYPE_CONFIG[type] || { label: type, color: 'grey', icon: 'file' }
            return (
              <div key={type}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 10 }}>
                  <Badge color={cfg.color}>{cfg.label}s</Badge>
                  <span className="muted" style={{ fontSize: 13 }}>{items.length} résultat(s)</span>
                </div>
                <div className="grid" style={{ gap: 8 }}>
                  {items.map(item => (
                    <div key={item.id} className="doc-row" onClick={() => goTo(item)} style={{ cursor: item.url ? 'pointer' : 'default' }}>
                      <div className="doc-ico" style={{ background: `var(--indigo-50)`, color: 'var(--primary)' }}>
                        <Icon name={cfg.icon} size={18} />
                      </div>
                      <div style={{ flex: 1 }}>
                        <div className="doc-name">{item.titre}</div>
                        {item.description && <div className="doc-meta">{item.description}</div>}
                      </div>
                      {item.url && <Icon name="chevronRight" size={16} style={{ color: 'var(--slate-400)' }} />}
                    </div>
                  ))}
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}
