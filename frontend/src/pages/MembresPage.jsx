import { useEffect, useState, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { getMembres, createMembre, updateMembre, deleteMembre } from '../api/membreApi'
import { getEquipes } from '../api/equipeApi'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { STATUT_MEMBRE, enumOptions } from '../constants'
import Button from '../components/ui/Button'
import Badge from '../components/ui/Badge'
import Avatar from '../components/ui/Avatar'
import Icon from '../components/ui/Icon'
import Modal from '../components/ui/Modal'
import EmptyState from '../components/ui/EmptyState'
import { PageLoader } from '../components/ui/Spinner'

const EMPTY = {
  nom: '', prenom: '', email: '', statut: 'PERMANENT', etablissementOrigine: '',
  laboratoireOrigine: '', equipeId: '', biographie: '', centresInteret: '',
  dateNaissance: '', dateEmbauche: '', dateAffiliation: '',
}

export default function MembresPage() {
  const { hasRole } = useAuth()
  const toast = useToast()
  const canEdit = hasRole('ADMIN') || hasRole('DIRECTEUR')
  const canDelete = hasRole('ADMIN')

  const [membres, setMembres] = useState([])
  const [equipes, setEquipes] = useState([])
  const [loading, setLoading] = useState(true)
  const [query, setQuery] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState(EMPTY)
  const [saving, setSaving] = useState(false)

  const load = useCallback((q) => {
    setLoading(true)
    getMembres(q).then(setMembres).catch(() => toast.error('Erreur de chargement')).finally(() => setLoading(false))
  }, [toast])

  useEffect(() => { load('') }, [load])
  useEffect(() => { getEquipes().then(setEquipes).catch(() => {}) }, [])

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  const openCreate = () => { setEditing(null); setForm(EMPTY); setModalOpen(true) }
  const openEdit = (m) => {
    setEditing(m)
    setForm({
      nom: m.nom || '', prenom: m.prenom || '', email: m.email || '', statut: m.statut || 'PERMANENT',
      etablissementOrigine: m.etablissementOrigine || '', laboratoireOrigine: m.laboratoireOrigine || '',
      equipeId: m.equipeId || '', biographie: m.biographie || '', centresInteret: m.centresInteret || '',
      dateNaissance: '', dateEmbauche: m.dateEmbauche || '', dateAffiliation: m.dateAffiliation || '',
    })
    setModalOpen(true)
  }

  const save = async (e) => {
    e.preventDefault()
    setSaving(true)
    const payload = {
      ...form,
      equipeId: form.equipeId ? Number(form.equipeId) : null,
      dateNaissance: form.dateNaissance || null,
      dateEmbauche: form.dateEmbauche || null,
      dateAffiliation: form.dateAffiliation || null,
    }
    try {
      if (editing) {
        await updateMembre(editing.id, payload)
        toast.success('Membre mis à jour')
      } else {
        await createMembre(payload)
        toast.success('Membre créé')
      }
      setModalOpen(false)
      load(query)
    } catch (err) {
      toast.error(err.response?.data?.message || "Échec de l'enregistrement")
    } finally {
      setSaving(false)
    }
  }

  const remove = async (m) => {
    if (!window.confirm(`Supprimer ${m.prenom} ${m.nom} ?`)) return
    try {
      await deleteMembre(m.id)
      toast.success('Membre supprimé')
      load(query)
    } catch (err) {
      toast.error(err.response?.data?.message || 'Échec de la suppression')
    }
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="ph-title">Membres</div>
          <div className="ph-sub">{membres.length} membre(s) du laboratoire</div>
        </div>
        {canEdit && <Button icon="plus" onClick={openCreate}>Nouveau membre</Button>}
      </div>

      <div className="toolbar">
        <div className="search-input">
          <Icon name="search" size={17} />
          <input
            placeholder="Rechercher par nom ou prénom..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && load(query)}
          />
        </div>
        <Button variant="ghost" onClick={() => load(query)}>Rechercher</Button>
      </div>

      {loading ? (
        <PageLoader />
      ) : membres.length === 0 ? (
        <div className="card"><EmptyState icon="users" title="Aucun membre" /></div>
      ) : (
        <div className="table-wrap">
          <table className="data-table">
            <thead>
              <tr><th>Membre</th><th>Statut</th><th>Équipe</th><th>Email</th><th></th></tr>
            </thead>
            <tbody>
              {membres.map((m) => {
                const st = STATUT_MEMBRE[m.statut] || { label: m.statut, color: 'grey' }
                return (
                  <tr key={m.id}>
                    <td>
                      <div className="flex items-center gap-3">
                        <Avatar name={`${m.prenom} ${m.nom}`} size="sm" />
                        <Link to={`/membres/${m.id}`} style={{ fontWeight: 600, color: 'var(--slate-800)' }}>
                          {m.prenom} {m.nom}
                        </Link>
                      </div>
                    </td>
                    <td><Badge color={st.color} dot>{st.label}</Badge></td>
                    <td>{m.equipeNom || '—'}</td>
                    <td className="muted">{m.email}</td>
                    <td>
                      <div className="row-actions">
                        <Link to={`/membres/${m.id}`} className="icon-btn" title="Voir"><Icon name="user" size={16} /></Link>
                        {canEdit && <button className="icon-btn" title="Modifier" onClick={() => openEdit(m)}><Icon name="edit" size={16} /></button>}
                        {canDelete && <button className="icon-btn" title="Supprimer" onClick={() => remove(m)}><Icon name="trash" size={16} /></button>}
                      </div>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      )}

      {modalOpen && (
        <Modal
          title={editing ? 'Modifier le membre' : 'Nouveau membre'}
          onClose={() => setModalOpen(false)}
          lg
          footer={
            <>
              <Button variant="ghost" onClick={() => setModalOpen(false)}>Annuler</Button>
              <Button onClick={save} disabled={saving}>{saving ? 'Enregistrement...' : 'Enregistrer'}</Button>
            </>
          }
        >
          <form onSubmit={save}>
            <div className="field-row">
              <div className="field"><label>Nom *</label><input value={form.nom} onChange={set('nom')} required /></div>
              <div className="field"><label>Prénom *</label><input value={form.prenom} onChange={set('prenom')} required /></div>
            </div>
            <div className="field-row">
              <div className="field"><label>Email *</label><input type="email" value={form.email} onChange={set('email')} required /></div>
              <div className="field">
                <label>Statut *</label>
                <select value={form.statut} onChange={set('statut')}>
                  {enumOptions(STATUT_MEMBRE).map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
                </select>
              </div>
            </div>
            <div className="field-row">
              <div className="field"><label>Établissement d'origine</label><input value={form.etablissementOrigine} onChange={set('etablissementOrigine')} /></div>
              <div className="field"><label>Laboratoire d'origine</label><input value={form.laboratoireOrigine} onChange={set('laboratoireOrigine')} /></div>
            </div>
            <div className="field-row">
              <div className="field">
                <label>Équipe</label>
                <select value={form.equipeId} onChange={set('equipeId')}>
                  <option value="">— Aucune —</option>
                  {equipes.map((eq) => <option key={eq.id} value={eq.id}>{eq.nom}</option>)}
                </select>
              </div>
              <div className="field"><label>Date d'affiliation</label><input type="date" value={form.dateAffiliation} onChange={set('dateAffiliation')} /></div>
            </div>
            <div className="field"><label>Centres d'intérêt</label><input value={form.centresInteret} onChange={set('centresInteret')} placeholder="IA, Big Data, ..." /></div>
            <div className="field"><label>Biographie</label><textarea value={form.biographie} onChange={set('biographie')} /></div>
          </form>
        </Modal>
      )}
    </div>
  )
}
