import { useEffect, useState, useCallback } from 'react'
import {
  getDocuments, uploadDocument, deleteDocument, downloadDocument,
} from '../api/documentApi'
import { getEvenements } from '../api/evenementApi'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../context/ToastContext'
import { TYPE_DOCUMENT, enumOptions, formatDate, formatSize } from '../constants'
import Button from '../components/ui/Button'
import Badge from '../components/ui/Badge'
import Icon from '../components/ui/Icon'
import Modal from '../components/ui/Modal'
import EmptyState from '../components/ui/EmptyState'
import { PageLoader } from '../components/ui/Spinner'

const EMPTY = { nom: '', typeDocument: 'RAPPORT', description: '', evenementId: '' }

export default function DocumentsPage() {
  const { hasRole } = useAuth()
  const toast = useToast()
  const canDelete = hasRole('ADMIN') || hasRole('DIRECTEUR')

  const [documents, setDocuments] = useState([])
  const [evenements, setEvenements] = useState([])
  const [loading, setLoading] = useState(true)
  const [filterType, setFilterType] = useState('')
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState(EMPTY)
  const [file, setFile] = useState(null)
  const [saving, setSaving] = useState(false)

  const load = useCallback((type) => {
    setLoading(true)
    getDocuments(type ? { type } : {}).then(setDocuments).catch(() => toast.error('Erreur de chargement')).finally(() => setLoading(false))
  }, [toast])

  useEffect(() => { load('') }, [load])
  useEffect(() => { getEvenements().then(setEvenements).catch(() => {}) }, [])

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  const openUpload = () => { setForm(EMPTY); setFile(null); setModalOpen(true) }

  const save = async (e) => {
    e.preventDefault()
    if (!file) { toast.error('Veuillez sélectionner un fichier'); return }
    setSaving(true)
    const fd = new FormData()
    fd.append('file', file)
    fd.append('nom', form.nom || file.name)
    fd.append('typeDocument', form.typeDocument)
    if (form.description) fd.append('description', form.description)
    if (form.evenementId) fd.append('evenementId', form.evenementId)
    try {
      await uploadDocument(fd)
      toast.success('Document téléversé')
      setModalOpen(false)
      load(filterType)
    } catch (err) {
      toast.error(err.response?.data?.message || "Échec du téléversement")
    } finally { setSaving(false) }
  }

  const remove = async (doc) => {
    if (!window.confirm(`Supprimer « ${doc.nom} » ?`)) return
    try { await deleteDocument(doc.id); toast.success('Document supprimé'); load(filterType) }
    catch (err) { toast.error(err.response?.data?.message || 'Échec de la suppression') }
  }

  const download = async (doc) => {
    try { await downloadDocument(doc.id, doc.originalName) }
    catch { toast.error('Échec du téléchargement') }
  }

  const onFilter = (e) => { setFilterType(e.target.value); load(e.target.value) }

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="ph-title">Documents</div>
          <div className="ph-sub">{documents.length} document(s) archivé(s)</div>
        </div>
        <Button icon="upload" onClick={openUpload}>Téléverser</Button>
      </div>

      <div className="toolbar">
        <select className="select-filter" value={filterType} onChange={onFilter}>
          <option value="">Tous les types</option>
          {enumOptions(TYPE_DOCUMENT).map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
        </select>
      </div>

      {loading ? (
        <PageLoader />
      ) : documents.length === 0 ? (
        <div className="card"><EmptyState icon="folder" title="Aucun document" /></div>
      ) : (
        <div className="grid" style={{ gap: 10 }}>
          {documents.map((doc) => {
            const t = TYPE_DOCUMENT[doc.typeDocument] || { label: doc.typeDocument, color: 'grey' }
            return (
              <div className="doc-row" key={doc.id}>
                <div className="doc-ico"><Icon name="file" size={20} /></div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div className="doc-name">{doc.nom}</div>
                  <div className="doc-meta">
                    {formatSize(doc.taille)} · {formatDate(doc.createdAt)}
                    {doc.evenementTitre && ` · ${doc.evenementTitre}`}
                    {doc.uploadePar && ` · ${doc.uploadePar}`}
                  </div>
                </div>
                <Badge color={t.color}>{t.label}</Badge>
                <button className="icon-btn" title="Télécharger" onClick={() => download(doc)}><Icon name="download" size={17} /></button>
                {canDelete && <button className="icon-btn" title="Supprimer" onClick={() => remove(doc)}><Icon name="trash" size={17} /></button>}
              </div>
            )
          })}
        </div>
      )}

      {modalOpen && (
        <Modal
          title="Téléverser un document"
          onClose={() => setModalOpen(false)}
          footer={
            <>
              <Button variant="ghost" onClick={() => setModalOpen(false)}>Annuler</Button>
              <Button onClick={save} disabled={saving}>{saving ? 'Envoi...' : 'Téléverser'}</Button>
            </>
          }
        >
          <form onSubmit={save}>
            <label className="dropzone" style={{ display: 'block', marginBottom: 16 }}>
              <Icon name="upload" size={26} />
              <div className="mt-2" style={{ fontWeight: 600 }}>{file ? file.name : 'Cliquez pour choisir un fichier'}</div>
              {file && <div className="muted" style={{ fontSize: 12.5 }}>{formatSize(file.size)}</div>}
              <input type="file" hidden onChange={(e) => { setFile(e.target.files[0]); if (e.target.files[0] && !form.nom) setForm((f) => ({ ...f, nom: e.target.files[0].name })) }} />
            </label>
            <div className="field"><label>Nom du document</label><input value={form.nom} onChange={set('nom')} placeholder="Nom affiché" /></div>
            <div className="field-row">
              <div className="field">
                <label>Type</label>
                <select value={form.typeDocument} onChange={set('typeDocument')}>
                  {enumOptions(TYPE_DOCUMENT).map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
                </select>
              </div>
              <div className="field">
                <label>Événement lié</label>
                <select value={form.evenementId} onChange={set('evenementId')}>
                  <option value="">— Aucun —</option>
                  {evenements.map((ev) => <option key={ev.id} value={ev.id}>{ev.titre}</option>)}
                </select>
              </div>
            </div>
            <div className="field"><label>Description</label><textarea value={form.description} onChange={set('description')} /></div>
          </form>
        </Modal>
      )}
    </div>
  )
}
