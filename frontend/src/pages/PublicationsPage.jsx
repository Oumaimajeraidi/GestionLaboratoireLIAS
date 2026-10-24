import { useEffect, useState, useCallback } from 'react'
import {
  getPublications, createPublication, updatePublication, deletePublication,
} from '../api/publicationApi'
import { getMembres } from '../api/membreApi'
import { getEquipes } from '../api/equipeApi'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { TYPE_PUBLICATION, enumOptions } from '../constants'
import Button from '../components/ui/Button'
import Badge from '../components/ui/Badge'
import Icon from '../components/ui/Icon'
import Card from '../components/ui/Card'
import Modal from '../components/ui/Modal'
import MultiSelect from '../components/ui/MultiSelect'
import EmptyState from '../components/ui/EmptyState'
import { PageLoader } from '../components/ui/Spinner'

const EMPTY = {
  titre: '', type: 'ARTICLE_REVUE', annee: '', venue: '', doi: '', url: '', resume: '',
  auteurIds: [], equipeId: '',
}

export default function PublicationsPage() {
  const { hasRole } = useAuth()
  const toast = useToast()
  const canEdit = hasRole('ADMIN') || hasRole('DIRECTEUR') || hasRole('MEMBRE') || hasRole('DOCTORANT')
  const canDelete = hasRole('ADMIN') || hasRole('DIRECTEUR')

  const [publications, setPublications] = useState([])
  const [membres, setMembres] = useState([])
  const [equipes, setEquipes] = useState([])
  const [loading, setLoading] = useState(true)
  const [filterEquipe, setFilterEquipe] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  const [form, setForm] = useState(EMPTY)
  const [saving, setSaving] = useState(false)

  const load = useCallback((equipeId) => {
    setLoading(true)
    const params = equipeId ? { equipeId } : {}
    getPublications(params).then(setPublications).catch(() => toast.error('Erreur de chargement')).finally(() => setLoading(false))
  }, [toast])

  useEffect(() => { load('') }, [load])
  useEffect(() => {
    getMembres().then(setMembres).catch(() => {})
    getEquipes().then(setEquipes).catch(() => {})
  }, [])

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  const openCreate = () => { setEditing(null); setForm(EMPTY); setModalOpen(true) }
  const openEdit = (p) => {
    setEditing(p)
    setForm({
      titre: p.titre || '', type: p.type || 'ARTICLE_REVUE', annee: p.annee || '', venue: p.venue || '',
      doi: p.doi || '', url: p.url || '', resume: p.resume || '',
      auteurIds: p.auteurs?.map((a) => a.id) || [], equipeId: p.equipeId || '',
    })
    setModalOpen(true)
  }

  const save = async (e) => {
    e.preventDefault()
    setSaving(true)
    const payload = {
      ...form,
      annee: form.annee ? Number(form.annee) : null,
      equipeId: form.equipeId ? Number(form.equipeId) : null,
    }
    try {
      if (editing) { await updatePublication(editing.id, payload); toast.success('Publication mise à jour') }
      else { await createPublication(payload); toast.success('Publication ajoutée') }
      setModalOpen(false)
      load(filterEquipe)
    } catch (err) {
      toast.error(err.response?.data?.message || "Échec de l'enregistrement")
    } finally { setSaving(false) }
  }

  const remove = async (p) => {
    if (!window.confirm(`Supprimer « ${p.titre} » ?`)) return
    try { await deletePublication(p.id); toast.success('Publication supprimée'); load(filterEquipe) }
    catch (err) { toast.error(err.response?.data?.message || 'Échec de la suppression') }
  }

  const onFilter = (e) => { setFilterEquipe(e.target.value); load(e.target.value) }

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="ph-title">Publications</div>
          <div className="ph-sub">{publications.length} publication(s)</div>
        </div>
        {canEdit && <Button icon="plus" onClick={openCreate}>Ajouter</Button>}
      </div>

      <div className="toolbar">
        <select className="select-filter" value={filterEquipe} onChange={onFilter}>
          <option value="">Toutes les équipes</option>
          {equipes.map((eq) => <option key={eq.id} value={eq.id}>{eq.nom}</option>)}
        </select>
      </div>

      {loading ? (
        <PageLoader />
      ) : publications.length === 0 ? (
        <div className="card"><EmptyState icon="book" title="Aucune publication" /></div>
      ) : (
        <Card>
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
                    {p.url && <a href={p.url} target="_blank" rel="noreferrer"><Icon name="link" size={13} /> Lien</a>}
                  </div>
                </div>
                {canEdit && (
                  <div className="row-actions">
                    <button className="icon-btn" title="Modifier" onClick={() => openEdit(p)}><Icon name="edit" size={16} /></button>
                    {canDelete && <button className="icon-btn" title="Supprimer" onClick={() => remove(p)}><Icon name="trash" size={16} /></button>}
                  </div>
                )}
              </div>
            )
          })}
        </Card>
      )}

      {modalOpen && (
        <Modal
          title={editing ? 'Modifier la publication' : 'Nouvelle publication'}
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
                  {enumOptions(TYPE_PUBLICATION).map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
                </select>
              </div>
              <div className="field"><label>Année</label><input type="number" value={form.annee} onChange={set('annee')} placeholder="2025" /></div>
            </div>
            <div className="field"><label>Revue / Conférence</label><input value={form.venue} onChange={set('venue')} /></div>
            <div className="field-row">
              <div className="field"><label>DOI</label><input value={form.doi} onChange={set('doi')} /></div>
              <div className="field"><label>URL</label><input value={form.url} onChange={set('url')} placeholder="https://" /></div>
            </div>
            <div className="field">
              <label>Équipe</label>
              <select value={form.equipeId} onChange={set('equipeId')}>
                <option value="">— Aucune —</option>
                {equipes.map((eq) => <option key={eq.id} value={eq.id}>{eq.nom}</option>)}
              </select>
            </div>
            <div className="field">
              <label>Auteurs</label>
              <MultiSelect
                options={membres.map((m) => ({ id: m.id, label: `${m.prenom} ${m.nom}` }))}
                value={form.auteurIds}
                onChange={(ids) => setForm({ ...form, auteurIds: ids })}
                placeholder="Ajouter un auteur..."
              />
            </div>
            <div className="field"><label>Résumé</label><textarea value={form.resume} onChange={set('resume')} /></div>
          </form>
        </Modal>
      )}
    </div>
  )
}
