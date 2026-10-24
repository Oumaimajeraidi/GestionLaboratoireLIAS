import { useEffect, useState, useCallback } from 'react'
import { getMandats, createMandat, updateMandat, activerMandat, deleteMandat } from '../api/mandatApi'
import { getMembres } from '../api/membreApi'
import { useToast } from '../context/ToastContext'
import { useAuth } from '../context/AuthContext'
import { formatDate } from '../constants'
import Button from '../components/ui/Button'
import Badge from '../components/ui/Badge'
import Icon from '../components/ui/Icon'
import Modal from '../components/ui/Modal'
import EmptyState from '../components/ui/EmptyState'
import { PageLoader } from '../components/ui/Spinner'

const EMPTY = { intitule: '', dateDebut: '', dateFin: '', actif: false, directeurId: '', viceDirecteurId: '' }

export default function MandatsPage() {
  const toast = useToast()
  const { hasRole } = useAuth()
  const canEdit = hasRole('ADMIN') || hasRole('DIRECTEUR')

  const [mandats, setMandats] = useState([])
  const [membres, setMembres] = useState([])
  const [loading, setLoading] = useState(true)
  const [modal, setModal] = useState(null) // null | 'create' | 'edit'
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState(EMPTY)
  const [saving, setSaving] = useState(false)

  const load = useCallback(() => {
    setLoading(true)
    getMandats().then(setMandats).catch(() => toast.error('Erreur de chargement')).finally(() => setLoading(false))
  }, [toast])

  useEffect(() => { load() }, [load])
  useEffect(() => { getMembres().then(setMembres).catch(() => {}) }, [])

  const openCreate = () => { setEditing(null); setForm(EMPTY); setModal('open') }
  const openEdit = (m) => {
    setEditing(m)
    setForm({
      intitule: m.intitule || '',
      dateDebut: m.dateDebut || '',
      dateFin: m.dateFin || '',
      actif: m.actif || false,
      directeurId: m.directeurId || '',
      viceDirecteurId: m.viceDirecteurId || '',
    })
    setModal('open')
  }

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.type === 'checkbox' ? e.target.checked : e.target.value })

  const save = async (e) => {
    e.preventDefault()
    if (!form.intitule.trim()) { toast.error('Intitulé requis'); return }
    setSaving(true)
    const payload = {
      ...form,
      directeurId: form.directeurId || null,
      viceDirecteurId: form.viceDirecteurId || null,
    }
    try {
      if (editing) await updateMandat(editing.id, payload)
      else await createMandat(payload)
      toast.success(editing ? 'Mandat mis à jour' : 'Mandat créé')
      setModal(null)
      load()
    } catch (err) {
      toast.error(err.response?.data?.message || 'Erreur')
    } finally { setSaving(false) }
  }

  const activer = async (id) => {
    try { await activerMandat(id); toast.success('Mandat activé'); load() }
    catch { toast.error('Erreur') }
  }

  const remove = async (m) => {
    if (!window.confirm(`Supprimer le mandat "${m.intitule}" ?`)) return
    try { await deleteMandat(m.id); toast.success('Mandat supprimé'); load() }
    catch { toast.error('Erreur') }
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="ph-title">Gouvernance & Mandats</div>
          <div className="ph-sub">{mandats.length} mandat(s) enregistré(s)</div>
        </div>
        {canEdit && <Button icon="plus" onClick={openCreate}>Nouveau mandat</Button>}
      </div>

      {loading ? <PageLoader /> : mandats.length === 0 ? (
        <div className="card"><EmptyState icon="shield" title="Aucun mandat" description="Créez le premier mandat de gouvernance du laboratoire." /></div>
      ) : (
        <div className="grid grid-2">
          {mandats.map(m => (
            <div key={m.id} className={`card ${m.actif ? 'card-highlight' : ''}`} style={m.actif ? { borderColor: 'var(--primary)', borderWidth: 2 } : {}}>
              <div className="flex items-center gap-3 mb-3">
                <div style={{ width: 40, height: 40, borderRadius: '50%', background: m.actif ? 'var(--indigo-100)' : 'var(--slate-100)', display: 'grid', placeItems: 'center', color: m.actif ? 'var(--primary)' : 'var(--slate-400)', flexShrink: 0 }}>
                  <Icon name="shield" size={20} />
                </div>
                <div style={{ flex: 1 }}>
                  <h3 style={{ fontSize: 16, margin: 0 }}>{m.intitule}</h3>
                  <div className="muted" style={{ fontSize: 13 }}>
                    {formatDate(m.dateDebut)} — {m.dateFin ? formatDate(m.dateFin) : 'En cours'}
                  </div>
                </div>
                {m.actif && <Badge color="green" dot>Actif</Badge>}
              </div>
              {(m.directeurNom || m.viceDirecteurNom) && (
                <div style={{ display: 'flex', flexDirection: 'column', gap: 4, marginBottom: 12 }}>
                  {m.directeurNom && (
                    <div className="flex items-center gap-2 muted" style={{ fontSize: 13.5 }}>
                      <Icon name="user" size={14} />
                      <span>Directeur : <strong style={{ color: 'var(--text)' }}>{m.directeurNom}</strong></span>
                    </div>
                  )}
                  {m.viceDirecteurNom && (
                    <div className="flex items-center gap-2 muted" style={{ fontSize: 13.5 }}>
                      <Icon name="user" size={14} />
                      <span>Vice-directeur : <strong style={{ color: 'var(--text)' }}>{m.viceDirecteurNom}</strong></span>
                    </div>
                  )}
                </div>
              )}
              {canEdit && (
                <div className="flex gap-2 mt-3">
                  {!m.actif && <Button variant="primary" size="sm" icon="check" onClick={() => activer(m.id)}>Activer</Button>}
                  <Button variant="ghost" size="sm" icon="edit" onClick={() => openEdit(m)}>Modifier</Button>
                  <Button variant="danger-soft" size="sm" icon="trash" onClick={() => remove(m)} />
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {modal && (
        <Modal
          title={editing ? 'Modifier le mandat' : 'Nouveau mandat'}
          onClose={() => setModal(null)}
          footer={
            <>
              <Button variant="ghost" onClick={() => setModal(null)}>Annuler</Button>
              <Button onClick={save} disabled={saving}>{saving ? '...' : editing ? 'Enregistrer' : 'Créer'}</Button>
            </>
          }
        >
          <form onSubmit={save}>
            <div className="field"><label>Intitulé *</label><input value={form.intitule} onChange={set('intitule')} placeholder="ex: Mandat 2022-2026" required /></div>
            <div className="field-row">
              <div className="field"><label>Date de début</label><input type="date" value={form.dateDebut} onChange={set('dateDebut')} /></div>
              <div className="field"><label>Date de fin</label><input type="date" value={form.dateFin} onChange={set('dateFin')} /></div>
            </div>
            <div className="field-row">
              <div className="field">
                <label>Directeur</label>
                <select value={form.directeurId} onChange={set('directeurId')}>
                  <option value="">— Non défini —</option>
                  {membres.map(m => <option key={m.id} value={m.id}>{m.prenom} {m.nom}</option>)}
                </select>
              </div>
              <div className="field">
                <label>Vice-directeur</label>
                <select value={form.viceDirecteurId} onChange={set('viceDirecteurId')}>
                  <option value="">— Non défini —</option>
                  {membres.map(m => <option key={m.id} value={m.id}>{m.prenom} {m.nom}</option>)}
                </select>
              </div>
            </div>
            <div className="field" style={{ flexDirection: 'row', alignItems: 'center', gap: 10 }}>
              <input type="checkbox" id="actif" checked={form.actif} onChange={set('actif')} />
              <label htmlFor="actif" style={{ marginBottom: 0 }}>Mandat actif (désactive les autres)</label>
            </div>
          </form>
        </Modal>
      )}
    </div>
  )
}
