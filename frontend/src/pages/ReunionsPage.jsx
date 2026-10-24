import { useEffect, useState, useCallback } from 'react'
import { getReunions, createReunion, updateReunion, deleteReunion, uploadPv } from '../api/reunionApi'
import { getEquipes } from '../api/equipeApi'
import { useToast } from '../context/ToastContext'
import { useAuth } from '../context/AuthContext'
import { TYPE_REUNION, enumOptions, formatDate } from '../constants'
import Button from '../components/ui/Button'
import Badge from '../components/ui/Badge'
import Icon from '../components/ui/Icon'
import Modal from '../components/ui/Modal'
import EmptyState from '../components/ui/EmptyState'
import { PageLoader } from '../components/ui/Spinner'

const EMPTY = { titre: '', type: 'CONSEIL_LABO', dateReunion: '', lieu: '', ordreJour: '', equipeId: '' }

export default function ReunionsPage() {
  const toast = useToast()
  const { hasRole } = useAuth()
  const canEdit = hasRole('ADMIN') || hasRole('DIRECTEUR')

  const [reunions, setReunions] = useState([])
  const [equipes, setEquipes] = useState([])
  const [loading, setLoading] = useState(true)
  const [modal, setModal] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState(EMPTY)
  const [pvModal, setPvModal] = useState(null)
  const [pvFile, setPvFile] = useState(null)
  const [saving, setSaving] = useState(false)

  const load = useCallback(() => {
    setLoading(true)
    getReunions().then(setReunions).catch(() => toast.error('Erreur')).finally(() => setLoading(false))
  }, [toast])

  useEffect(() => { load() }, [load])
  useEffect(() => { getEquipes().then(setEquipes).catch(() => {}) }, [])

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  const openEdit = (r) => {
    setEditing(r)
    setForm({ titre: r.titre, type: r.type, dateReunion: r.dateReunion || '', lieu: r.lieu || '', ordreJour: r.ordreJour || '', equipeId: r.equipeId || '' })
    setModal(true)
  }

  const save = async (e) => {
    e.preventDefault()
    if (!form.titre.trim() || !form.dateReunion) { toast.error('Titre et date requis'); return }
    setSaving(true)
    const payload = { ...form, equipeId: form.equipeId || null }
    try {
      if (editing) await updateReunion(editing.id, payload)
      else await createReunion(payload)
      toast.success(editing ? 'Réunion mise à jour' : 'Réunion créée')
      setModal(false); setEditing(null); load()
    } catch { toast.error('Erreur') } finally { setSaving(false) }
  }

  const handleUploadPv = async () => {
    if (!pvFile) { toast.error('Sélectionner un fichier'); return }
    setSaving(true)
    try { await uploadPv(pvModal.id, pvFile); toast.success('PV uploadé'); setPvModal(null); setPvFile(null); load() }
    catch { toast.error('Erreur upload') } finally { setSaving(false) }
  }

  const remove = async (r) => {
    if (!window.confirm(`Supprimer "${r.titre}" ?`)) return
    try { await deleteReunion(r.id); toast.success('Supprimée'); load() }
    catch { toast.error('Erreur') }
  }

  const now = new Date()

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="ph-title">Procès-verbaux & Réunions</div>
          <div className="ph-sub">{reunions.length} réunion(s)</div>
        </div>
        {canEdit && <Button icon="plus" onClick={() => { setEditing(null); setForm(EMPTY); setModal(true) }}>Nouvelle réunion</Button>}
      </div>

      {loading ? <PageLoader /> : reunions.length === 0 ? (
        <div className="card"><EmptyState icon="users" title="Aucune réunion" /></div>
      ) : (
        <div className="grid" style={{ gap: 10 }}>
          {reunions.map(r => {
            const tp = TYPE_REUNION[r.type] || { label: r.type, color: 'grey' }
            const isPast = r.dateReunion && new Date(r.dateReunion) < now
            return (
              <div key={r.id} className="doc-row" style={{ alignItems: 'flex-start', flexDirection: 'column', gap: 8 }}>
                <div className="flex items-center gap-3" style={{ width: '100%' }}>
                  <div className="doc-ico" style={{ background: isPast ? 'var(--slate-100)' : 'var(--indigo-50)', color: isPast ? 'var(--slate-400)' : 'var(--primary)', flexShrink: 0 }}>
                    <Icon name="users" size={20} />
                  </div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div className="doc-name">{r.titre}</div>
                    <div className="doc-meta">
                      <Icon name="calendar" size={13} /> {formatDate(r.dateReunion)}
                      {r.lieu && ` · ${r.lieu}`}
                      {r.equipeNom && ` · Équipe: ${r.equipeNom}`}
                    </div>
                  </div>
                  <div className="flex gap-2 items-center">
                    <Badge color={tp.color}>{tp.label}</Badge>
                    {r.hasPv && <Badge color="green" dot>PV disponible</Badge>}
                  </div>
                </div>
                {r.ordreJour && (
                  <div style={{ padding: '8px 12px', background: 'var(--slate-50)', borderRadius: 8, fontSize: 13, color: 'var(--text-soft)', width: '100%', whiteSpace: 'pre-line' }}>
                    <strong style={{ color: 'var(--text)' }}>Ordre du jour :</strong> {r.ordreJour}
                  </div>
                )}
                {canEdit && (
                  <div className="flex gap-2">
                    {isPast && !r.hasPv && <Button variant="primary" size="sm" icon="upload" onClick={() => { setPvModal(r); setPvFile(null) }}>Uploader PV</Button>}
                    <Button variant="ghost" size="sm" icon="edit" onClick={() => openEdit(r)}>Modifier</Button>
                    <Button variant="danger-soft" size="sm" icon="trash" onClick={() => remove(r)} />
                  </div>
                )}
              </div>
            )
          })}
        </div>
      )}

      {modal && (
        <Modal title={editing ? 'Modifier la réunion' : 'Nouvelle réunion'} onClose={() => setModal(false)}
          footer={<><Button variant="ghost" onClick={() => setModal(false)}>Annuler</Button><Button onClick={save} disabled={saving}>{saving ? '...' : 'Enregistrer'}</Button></>}>
          <form onSubmit={save}>
            <div className="field"><label>Titre *</label><input value={form.titre} onChange={set('titre')} required /></div>
            <div className="field-row">
              <div className="field"><label>Type</label><select value={form.type} onChange={set('type')}>{enumOptions(TYPE_REUNION).map(o => <option key={o.value} value={o.value}>{o.label}</option>)}</select></div>
              <div className="field"><label>Date *</label><input type="date" value={form.dateReunion} onChange={set('dateReunion')} required /></div>
            </div>
            <div className="field-row">
              <div className="field"><label>Lieu</label><input value={form.lieu} onChange={set('lieu')} /></div>
              <div className="field"><label>Équipe</label>
                <select value={form.equipeId} onChange={set('equipeId')}>
                  <option value="">— Toutes équipes —</option>
                  {equipes.map(e => <option key={e.id} value={e.id}>{e.nom}</option>)}
                </select>
              </div>
            </div>
            <div className="field"><label>Ordre du jour</label><textarea value={form.ordreJour} onChange={set('ordreJour')} rows={4} /></div>
          </form>
        </Modal>
      )}

      {pvModal && (
        <Modal title={`Uploader le PV — ${pvModal.titre}`} onClose={() => setPvModal(null)}
          footer={<><Button variant="ghost" onClick={() => setPvModal(null)}>Annuler</Button><Button onClick={handleUploadPv} disabled={saving}>{saving ? 'Envoi...' : 'Uploader'}</Button></>}>
          <label className="dropzone" style={{ display: 'block' }}>
            <Icon name="upload" size={26} />
            <div className="mt-2" style={{ fontWeight: 600 }}>{pvFile ? pvFile.name : 'Cliquez pour choisir le PV (PDF, Word…)'}</div>
            <input type="file" hidden onChange={e => setPvFile(e.target.files[0])} accept=".pdf,.doc,.docx" />
          </label>
        </Modal>
      )}
    </div>
  )
}
