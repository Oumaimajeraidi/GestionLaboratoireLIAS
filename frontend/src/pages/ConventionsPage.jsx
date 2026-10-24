import { useEffect, useState, useCallback } from 'react'
import {
  getConventions, createConvention, updateConvention, deleteConvention,
  getPartenaires, createPartenaire
} from '../api/conventionApi'
import { useToast } from '../context/ToastContext'
import { useAuth } from '../context/AuthContext'
import { STATUT_CONVENTION, TYPE_CONVENTION, enumOptions, formatDate } from '../constants'
import Button from '../components/ui/Button'
import Badge from '../components/ui/Badge'
import Icon from '../components/ui/Icon'
import Modal from '../components/ui/Modal'
import EmptyState from '../components/ui/EmptyState'
import { PageLoader } from '../components/ui/Spinner'

const EMPTY_CONV = { intitule: '', type: 'PARTENARIAT_RECHERCHE', statut: 'EN_NEGOCIATION', partenaireId: '', dateDebut: '', dateFin: '', objectif: '' }
const EMPTY_PART = { nom: '', pays: '', typeInstitution: '', contactNom: '', contactEmail: '' }

export default function ConventionsPage() {
  const toast = useToast()
  const { hasRole } = useAuth()
  const canEdit = hasRole('ADMIN') || hasRole('DIRECTEUR')

  const [tab, setTab] = useState('conventions')
  const [conventions, setConventions] = useState([])
  const [partenaires, setPartenaires] = useState([])
  const [loading, setLoading] = useState(true)

  const [convModal, setConvModal] = useState(false)
  const [editingConv, setEditingConv] = useState(null)
  const [convForm, setConvForm] = useState(EMPTY_CONV)

  const [partModal, setPartModal] = useState(false)
  const [partForm, setPartForm] = useState(EMPTY_PART)
  const [saving, setSaving] = useState(false)

  const load = useCallback(() => {
    setLoading(true)
    Promise.all([getConventions(), getPartenaires()])
      .then(([c, p]) => { setConventions(c); setPartenaires(p) })
      .catch(() => toast.error('Erreur de chargement'))
      .finally(() => setLoading(false))
  }, [toast])

  useEffect(() => { load() }, [load])

  const setConv = (k) => (e) => setConvForm({ ...convForm, [k]: e.target.value })
  const setPart = (k) => (e) => setPartForm({ ...partForm, [k]: e.target.value })

  const openEditConv = (c) => {
    setEditingConv(c)
    setConvForm({ intitule: c.intitule, type: c.type, statut: c.statut, partenaireId: c.partenaireId || '', dateDebut: c.dateDebut || '', dateFin: c.dateFin || '', objectif: c.objectif || '' })
    setConvModal(true)
  }

  const saveConv = async (e) => {
    e.preventDefault()
    setSaving(true)
    const payload = { ...convForm, partenaireId: convForm.partenaireId || null }
    try {
      if (editingConv) await updateConvention(editingConv.id, payload)
      else await createConvention(payload)
      toast.success(editingConv ? 'Convention mise à jour' : 'Convention créée')
      setConvModal(false); setEditingConv(null); load()
    } catch { toast.error('Erreur') } finally { setSaving(false) }
  }

  const savePart = async (e) => {
    e.preventDefault()
    setSaving(true)
    try {
      await createPartenaire(partForm)
      toast.success('Partenaire ajouté')
      setPartModal(false); setPartForm(EMPTY_PART); load()
    } catch { toast.error('Erreur') } finally { setSaving(false) }
  }

  const removeConv = async (c) => {
    if (!window.confirm(`Supprimer "${c.intitule}" ?`)) return
    try { await deleteConvention(c.id); toast.success('Supprimée'); load() }
    catch { toast.error('Erreur') }
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="ph-title">Conventions & Partenariats</div>
          <div className="ph-sub">{conventions.length} convention(s) · {partenaires.length} partenaire(s)</div>
        </div>
        {canEdit && (
          <div className="flex gap-2">
            <Button variant="ghost" icon="plus" onClick={() => { setPartForm(EMPTY_PART); setPartModal(true) }}>Partenaire</Button>
            <Button icon="plus" onClick={() => { setEditingConv(null); setConvForm(EMPTY_CONV); setConvModal(true) }}>Convention</Button>
          </div>
        )}
      </div>

      <div className="tab-bar mb-4">
        <button className={`tab-btn${tab === 'conventions' ? ' active' : ''}`} onClick={() => setTab('conventions')}>
          <Icon name="handshake" size={15} /> Conventions ({conventions.length})
        </button>
        <button className={`tab-btn${tab === 'partenaires' ? ' active' : ''}`} onClick={() => setTab('partenaires')}>
          <Icon name="globe" size={15} /> Partenaires ({partenaires.length})
        </button>
      </div>

      {loading ? <PageLoader /> : tab === 'conventions' ? (
        conventions.length === 0 ? <div className="card"><EmptyState icon="handshake" title="Aucune convention" /></div> : (
          <div className="grid grid-2">
            {conventions.map(c => {
              const st = STATUT_CONVENTION[c.statut] || { label: c.statut, color: 'grey' }
              const tp = TYPE_CONVENTION[c.type] || { label: c.type, color: 'grey' }
              return (
                <div key={c.id} className="card">
                  <div className="flex items-center gap-2 mb-2">
                    <div style={{ flex: 1 }}>
                      <h3 style={{ fontSize: 15, margin: 0 }}>{c.intitule}</h3>
                      {c.partenaireNom && <div className="muted" style={{ fontSize: 13 }}><Icon name="globe" size={12} /> {c.partenaireNom}{c.partenairePays ? ` — ${c.partenairePays}` : ''}</div>}
                    </div>
                    <div className="flex flex-col gap-1" style={{ alignItems: 'flex-end' }}>
                      <Badge color={st.color} dot>{st.label}</Badge>
                      <Badge color={tp.color}>{tp.label}</Badge>
                    </div>
                  </div>
                  <div className="pub-meta mb-2">
                    {c.dateDebut && <span><Icon name="calendar" size={13} /> {formatDate(c.dateDebut)} — {c.dateFin ? formatDate(c.dateFin) : 'En cours'}</span>}
                  </div>
                  {c.objectif && <p className="muted" style={{ fontSize: 13.5 }}>{c.objectif}</p>}
                  {canEdit && (
                    <div className="flex gap-2 mt-3">
                      <Button variant="ghost" size="sm" icon="edit" onClick={() => openEditConv(c)}>Modifier</Button>
                      <Button variant="danger-soft" size="sm" icon="trash" onClick={() => removeConv(c)} />
                    </div>
                  )}
                </div>
              )
            })}
          </div>
        )
      ) : (
        partenaires.length === 0 ? <div className="card"><EmptyState icon="globe" title="Aucun partenaire" /></div> : (
          <div className="grid grid-3">
            {partenaires.map(p => (
              <div key={p.id} className="card text-center">
                <div style={{ width: 48, height: 48, borderRadius: '50%', background: 'var(--indigo-100)', display: 'grid', placeItems: 'center', color: 'var(--primary)', margin: '0 auto 12px' }}>
                  <Icon name="globe" size={22} />
                </div>
                <h3 style={{ fontSize: 15 }}>{p.nom}</h3>
                {p.pays && <div className="muted" style={{ fontSize: 13 }}>{p.pays}</div>}
                {p.typeInstitution && <div className="muted" style={{ fontSize: 13 }}>{p.typeInstitution}</div>}
                {p.contactNom && <div style={{ fontSize: 13, marginTop: 8 }}><Icon name="user" size={12} /> {p.contactNom}</div>}
                {p.contactEmail && <div style={{ fontSize: 12 }} className="muted">{p.contactEmail}</div>}
              </div>
            ))}
          </div>
        )
      )}

      {convModal && (
        <Modal title={editingConv ? 'Modifier la convention' : 'Nouvelle convention'} onClose={() => setConvModal(false)}
          footer={<><Button variant="ghost" onClick={() => setConvModal(false)}>Annuler</Button><Button onClick={saveConv} disabled={saving}>{saving ? '...' : 'Enregistrer'}</Button></>}>
          <form onSubmit={saveConv}>
            <div className="field"><label>Intitulé *</label><input value={convForm.intitule} onChange={setConv('intitule')} required /></div>
            <div className="field-row">
              <div className="field"><label>Type</label><select value={convForm.type} onChange={setConv('type')}>{enumOptions(TYPE_CONVENTION).map(o => <option key={o.value} value={o.value}>{o.label}</option>)}</select></div>
              <div className="field"><label>Statut</label><select value={convForm.statut} onChange={setConv('statut')}>{enumOptions(STATUT_CONVENTION).map(o => <option key={o.value} value={o.value}>{o.label}</option>)}</select></div>
            </div>
            <div className="field"><label>Partenaire</label>
              <select value={convForm.partenaireId} onChange={setConv('partenaireId')}>
                <option value="">— Aucun —</option>
                {partenaires.map(p => <option key={p.id} value={p.id}>{p.nom} ({p.pays})</option>)}
              </select>
            </div>
            <div className="field-row">
              <div className="field"><label>Date de début</label><input type="date" value={convForm.dateDebut} onChange={setConv('dateDebut')} /></div>
              <div className="field"><label>Date de fin</label><input type="date" value={convForm.dateFin} onChange={setConv('dateFin')} /></div>
            </div>
            <div className="field"><label>Objectif</label><textarea value={convForm.objectif} onChange={setConv('objectif')} /></div>
          </form>
        </Modal>
      )}

      {partModal && (
        <Modal title="Nouveau partenaire" onClose={() => setPartModal(false)}
          footer={<><Button variant="ghost" onClick={() => setPartModal(false)}>Annuler</Button><Button onClick={savePart} disabled={saving}>{saving ? '...' : 'Ajouter'}</Button></>}>
          <form onSubmit={savePart}>
            <div className="field"><label>Nom *</label><input value={partForm.nom} onChange={setPart('nom')} required /></div>
            <div className="field-row">
              <div className="field"><label>Pays</label><input value={partForm.pays} onChange={setPart('pays')} /></div>
              <div className="field"><label>Type d'institution</label><input value={partForm.typeInstitution} onChange={setPart('typeInstitution')} placeholder="Université, Entreprise..." /></div>
            </div>
            <div className="field-row">
              <div className="field"><label>Contact (nom)</label><input value={partForm.contactNom} onChange={setPart('contactNom')} /></div>
              <div className="field"><label>Contact (email)</label><input type="email" value={partForm.contactEmail} onChange={setPart('contactEmail')} /></div>
            </div>
          </form>
        </Modal>
      )}
    </div>
  )
}
