import { useEffect, useState, useCallback } from 'react'
import {
  getEvenements, createEvenement, updateEvenement, deleteEvenement,
} from '../api/evenementApi'
import { getMembres } from '../api/membreApi'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { TYPE_EVENEMENT, enumOptions, formatDate } from '../constants'
import Button from '../components/ui/Button'
import Badge from '../components/ui/Badge'
import Icon from '../components/ui/Icon'
import Modal from '../components/ui/Modal'
import MultiSelect from '../components/ui/MultiSelect'
import EmptyState from '../components/ui/EmptyState'
import { PageLoader } from '../components/ui/Spinner'

const MONTHS = ['JAN', 'FÉV', 'MAR', 'AVR', 'MAI', 'JUI', 'JUIL', 'AOÛ', 'SEP', 'OCT', 'NOV', 'DÉC']
const EMPTY = {
  titre: '', type: 'CONFERENCE', description: '', dateDebut: '', dateFin: '',
  lieu: '', edition: '', organisateurIds: [],
}

export default function EvenementsPage() {
  const { hasRole } = useAuth()
  const toast = useToast()
  const canEdit = hasRole('ADMIN') || hasRole('DIRECTEUR') || hasRole('MEMBRE')
  const canDelete = hasRole('ADMIN') || hasRole('DIRECTEUR')

  const [evenements, setEvenements] = useState([])
  const [membres, setMembres] = useState([])
  const [loading, setLoading] = useState(true)
  const [filterType, setFilterType] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState(EMPTY)
  const [saving, setSaving] = useState(false)

  const load = useCallback((type) => {
    setLoading(true)
    getEvenements(type ? { type } : {}).then(setEvenements).catch(() => toast.error('Erreur de chargement')).finally(() => setLoading(false))
  }, [toast])

  useEffect(() => { load('') }, [load])
  useEffect(() => { getMembres().then(setMembres).catch(() => {}) }, [])

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  const openCreate = () => { setEditing(null); setForm(EMPTY); setModalOpen(true) }
  const openEdit = (ev) => {
    setEditing(ev)
    setForm({
      titre: ev.titre || '', type: ev.type || 'CONFERENCE', description: ev.description || '',
      dateDebut: ev.dateDebut || '', dateFin: ev.dateFin || '', lieu: ev.lieu || '', edition: ev.edition || '',
      organisateurIds: ev.organisateurs?.map((o) => o.id) || [],
    })
    setModalOpen(true)
  }

  const save = async (e) => {
    e.preventDefault()
    setSaving(true)
    const payload = { ...form, dateDebut: form.dateDebut || null, dateFin: form.dateFin || null }
    try {
      if (editing) { await updateEvenement(editing.id, payload); toast.success('Événement mis à jour') }
      else { await createEvenement(payload); toast.success('Événement créé') }
      setModalOpen(false)
      load(filterType)
    } catch (err) {
      toast.error(err.response?.data?.message || "Échec de l'enregistrement")
    } finally { setSaving(false) }
  }

  const remove = async (ev) => {
    if (!window.confirm(`Supprimer « ${ev.titre} » ?`)) return
    try { await deleteEvenement(ev.id); toast.success('Événement supprimé'); load(filterType) }
    catch (err) { toast.error(err.response?.data?.message || 'Échec de la suppression') }
  }

  const onFilter = (e) => { setFilterType(e.target.value); load(e.target.value) }

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="ph-title">Événements</div>
          <div className="ph-sub">{evenements.length} événement(s)</div>
        </div>
        {canEdit && <Button icon="plus" onClick={openCreate}>Nouvel événement</Button>}
      </div>

      <div className="toolbar">
        <select className="select-filter" value={filterType} onChange={onFilter}>
          <option value="">Tous les types</option>
          {enumOptions(TYPE_EVENEMENT).map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
        </select>
      </div>

      {loading ? (
        <PageLoader />
      ) : evenements.length === 0 ? (
        <div className="card"><EmptyState icon="calendar" title="Aucun événement" /></div>
      ) : (
        <div className="grid grid-3">
          {evenements.map((ev) => {
            const t = TYPE_EVENEMENT[ev.type] || { label: ev.type, color: 'grey' }
            const d = ev.dateDebut ? new Date(ev.dateDebut) : null
            return (
              <div className="event-card" key={ev.id}>
                <div className="ec-top">
                  <div className="ec-date">
                    <div className="d">{d ? d.getDate() : '—'}</div>
                    <div className="m">{d ? MONTHS[d.getMonth()] : ''}</div>
                  </div>
                  <Badge color={t.color}>{t.label}</Badge>
                </div>
                <div className="ec-body">
                  <h4>{ev.titre}</h4>
                  <p className="muted" style={{ fontSize: 13.5, margin: 0 }}>{ev.description}</p>
                  {ev.organisateurs?.length > 0 && (
                    <div className="pub-meta mt-2"><Icon name="users" size={13} /> {ev.organisateurs.map((o) => o.nomComplet).join(', ')}</div>
                  )}
                </div>
                <div className="ec-foot">
                  <Icon name="mapPin" size={14} /> {ev.lieu || '—'}
                  {ev.edition && <span>· {ev.edition}</span>}
                  {canEdit && (
                    <div className="row-actions" style={{ flex: 1 }}>
                      <button className="icon-btn" title="Modifier" onClick={() => openEdit(ev)}><Icon name="edit" size={15} /></button>
                      {canDelete && <button className="icon-btn" title="Supprimer" onClick={() => remove(ev)}><Icon name="trash" size={15} /></button>}
                    </div>
                  )}
                </div>
              </div>
            )
          })}
        </div>
      )}

      {modalOpen && (
        <Modal
          title={editing ? "Modifier l'événement" : 'Nouvel événement'}
          onClose={() => setModalOpen(false)}
          lg
          footer={
            <>
              <Button variant="ghost" onClick={() => setModalOpen(false)}>Annuler</Button>
              <Button onClick={save} disabled={saving}>{saving ? '...' : 'Enregistrer'}</Button>
            </>
          }
        >
          <form onSubmit={save}>
            <div className="field"><label>Titre *</label><input value={form.titre} onChange={set('titre')} required /></div>
            <div className="field-row">
              <div className="field">
                <label>Type *</label>
                <select value={form.type} onChange={set('type')}>
                  {enumOptions(TYPE_EVENEMENT).map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
                </select>
              </div>
              <div className="field"><label>Édition</label><input value={form.edition} onChange={set('edition')} placeholder="2025" /></div>
            </div>
            <div className="field-row">
              <div className="field"><label>Date de début</label><input type="date" value={form.dateDebut} onChange={set('dateDebut')} /></div>
              <div className="field"><label>Date de fin</label><input type="date" value={form.dateFin} onChange={set('dateFin')} /></div>
            </div>
            <div className="field"><label>Lieu</label><input value={form.lieu} onChange={set('lieu')} /></div>
            <div className="field">
              <label>Organisateurs</label>
              <MultiSelect
                options={membres.map((m) => ({ id: m.id, label: `${m.prenom} ${m.nom}` }))}
                value={form.organisateurIds}
                onChange={(ids) => setForm({ ...form, organisateurIds: ids })}
                placeholder="Ajouter un organisateur..."
              />
            </div>
            <div className="field"><label>Description</label><textarea value={form.description} onChange={set('description')} /></div>
          </form>
        </Modal>
      )}
    </div>
  )
}
