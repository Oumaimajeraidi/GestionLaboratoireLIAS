import { useEffect, useState, useCallback } from 'react'
import {
  getMateriels, createMateriel, updateMateriel, deleteMateriel, attribuerMateriel, retournerMateriel,
  getBesoins, createBesoin, validerBesoin, refuserBesoin
} from '../api/materielApi'
import { getMembres } from '../api/membreApi'
import { useToast } from '../context/ToastContext'
import { useAuth } from '../context/AuthContext'
import { ETAT_MATERIEL, STATUT_BESOIN, enumOptions, formatDate } from '../constants'
import Button from '../components/ui/Button'
import Badge from '../components/ui/Badge'
import Icon from '../components/ui/Icon'
import Modal from '../components/ui/Modal'
import EmptyState from '../components/ui/EmptyState'
import { PageLoader } from '../components/ui/Spinner'

const EMPTY_MAT = { nom: '', description: '', reference: '', quantite: 1, etat: 'DISPONIBLE', dateArrivee: '' }
const EMPTY_BES = { intitule: '', description: '', quantiteDemandee: 1, demandeurId: '' }

export default function MaterielPage() {
  const toast = useToast()
  const { hasRole } = useAuth()
  const canEdit = hasRole('ADMIN') || hasRole('DIRECTEUR')

  const [tab, setTab] = useState('materiel')
  const [materiels, setMateriels] = useState([])
  const [besoins, setBesoins] = useState([])
  const [membres, setMembres] = useState([])
  const [loading, setLoading] = useState(true)

  const [matModal, setMatModal] = useState(false)
  const [editingMat, setEditingMat] = useState(null)
  const [matForm, setMatForm] = useState(EMPTY_MAT)

  const [besModal, setBesModal] = useState(false)
  const [besForm, setBesForm] = useState(EMPTY_BES)

  const [attribModal, setAttribModal] = useState(null)
  const [attribMembreId, setAttribMembreId] = useState('')

  const [decisionModal, setDecisionModal] = useState(null) // { type, besoin }
  const [decisionComment, setDecisionComment] = useState('')
  const [decisionValideur, setDecisionValideur] = useState('')

  const [saving, setSaving] = useState(false)

  const load = useCallback(() => {
    setLoading(true)
    Promise.all([getMateriels(), getBesoins(), getMembres()])
      .then(([m, b, mb]) => { setMateriels(m); setBesoins(b); setMembres(mb) })
      .catch(() => toast.error('Erreur de chargement'))
      .finally(() => setLoading(false))
  }, [toast])

  useEffect(() => { load() }, [load])

  const setMat = (k) => (e) => setMatForm({ ...matForm, [k]: e.target.value })
  const setBes = (k) => (e) => setBesForm({ ...besForm, [k]: e.target.value })

  const openEditMat = (m) => {
    setEditingMat(m)
    setMatForm({ nom: m.nom, description: m.description || '', reference: m.reference || '', quantite: m.quantite, etat: m.etat, dateArrivee: m.dateArrivee || '' })
    setMatModal(true)
  }

  const saveMat = async (e) => {
    e.preventDefault()
    if (!matForm.nom.trim()) { toast.error('Nom requis'); return }
    setSaving(true)
    try {
      if (editingMat) await updateMateriel(editingMat.id, matForm)
      else await createMateriel(matForm)
      toast.success(editingMat ? 'Matériel mis à jour' : 'Matériel ajouté')
      setMatModal(false); setEditingMat(null); setMatForm(EMPTY_MAT); load()
    } catch { toast.error('Erreur') } finally { setSaving(false) }
  }

  const saveBes = async (e) => {
    e.preventDefault()
    if (!besForm.intitule.trim() || !besForm.demandeurId) { toast.error('Intitulé et demandeur requis'); return }
    setSaving(true)
    try {
      await createBesoin(besForm)
      toast.success('Besoin soumis')
      setBesModal(false); setBesForm(EMPTY_BES); load()
    } catch { toast.error('Erreur') } finally { setSaving(false) }
  }

  const attribuer = async () => {
    if (!attribMembreId) { toast.error('Sélectionner un membre'); return }
    try { await attribuerMateriel(attribModal.id, attribMembreId); toast.success('Matériel attribué'); setAttribModal(null); load() }
    catch { toast.error('Erreur') }
  }

  const retourner = async (id) => {
    try { await retournerMateriel(id); toast.success('Retour enregistré'); load() }
    catch { toast.error('Erreur') }
  }

  const removeMat = async (m) => {
    if (!window.confirm(`Supprimer "${m.nom}" ?`)) return
    try { await deleteMateriel(m.id); toast.success('Supprimé'); load() }
    catch { toast.error('Erreur') }
  }

  const confirmerDecision = async () => {
    if (!decisionValideur) { toast.error('Sélectionner un valideur'); return }
    setSaving(true)
    try {
      if (decisionModal.type === 'valider') await validerBesoin(decisionModal.besoin.id, decisionValideur, decisionComment)
      else await refuserBesoin(decisionModal.besoin.id, decisionValideur, decisionComment)
      toast.success(decisionModal.type === 'valider' ? 'Besoin validé' : 'Besoin refusé')
      setDecisionModal(null); setDecisionComment(''); setDecisionValideur(''); load()
    } catch { toast.error('Erreur') } finally { setSaving(false) }
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="ph-title">Gestion du Matériel</div>
          <div className="ph-sub">{materiels.length} équipement(s) · {besoins.length} besoin(s)</div>
        </div>
        <div className="flex gap-2">
          {canEdit && tab === 'materiel' && <Button icon="plus" onClick={() => { setEditingMat(null); setMatForm(EMPTY_MAT); setMatModal(true) }}>Ajouter</Button>}
          {tab === 'besoins' && <Button icon="plus" variant="primary" onClick={() => setBesModal(true)}>Soumettre un besoin</Button>}
        </div>
      </div>

      <div className="tab-bar mb-4">
        <button className={`tab-btn${tab === 'materiel' ? ' active' : ''}`} onClick={() => setTab('materiel')}>
          <Icon name="cpu" size={15} /> Équipements ({materiels.length})
        </button>
        <button className={`tab-btn${tab === 'besoins' ? ' active' : ''}`} onClick={() => setTab('besoins')}>
          <Icon name="inbox" size={15} /> Besoins en matériel ({besoins.length})
        </button>
      </div>

      {loading ? <PageLoader /> : tab === 'materiel' ? (
        materiels.length === 0 ? (
          <div className="card"><EmptyState icon="cpu" title="Aucun équipement" /></div>
        ) : (
          <div className="grid" style={{ gap: 10 }}>
            {materiels.map(m => {
              const etat = ETAT_MATERIEL[m.etat] || { label: m.etat, color: 'grey' }
              return (
                <div key={m.id} className="doc-row">
                  <div className="doc-ico" style={{ background: 'var(--indigo-50)', color: 'var(--primary)' }}><Icon name="cpu" size={20} /></div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div className="doc-name">{m.nom}</div>
                    <div className="doc-meta">
                      {m.reference && `Réf: ${m.reference} · `}Qté: {m.quantite}
                      {m.attribueANom && ` · Attribué à : ${m.attribueANom}`}
                      {m.dateArrivee && ` · Arrivée: ${formatDate(m.dateArrivee)}`}
                    </div>
                  </div>
                  <Badge color={etat.color}>{etat.label}</Badge>
                  {canEdit && (
                    <>
                      {m.etat === 'DISPONIBLE' && <button className="icon-btn" title="Attribuer" onClick={() => { setAttribModal(m); setAttribMembreId('') }}><Icon name="user" size={17} /></button>}
                      {m.etat === 'ATTRIBUE' && <button className="icon-btn" title="Retour" onClick={() => retourner(m.id)}><Icon name="arrowLeft" size={17} /></button>}
                      <button className="icon-btn" title="Modifier" onClick={() => openEditMat(m)}><Icon name="edit" size={17} /></button>
                      <button className="icon-btn" title="Supprimer" onClick={() => removeMat(m)}><Icon name="trash" size={17} /></button>
                    </>
                  )}
                </div>
              )
            })}
          </div>
        )
      ) : (
        besoins.length === 0 ? (
          <div className="card"><EmptyState icon="inbox" title="Aucun besoin soumis" /></div>
        ) : (
          <div className="grid grid-2">
            {besoins.map(b => {
              const st = STATUT_BESOIN[b.statut] || { label: b.statut, color: 'grey' }
              return (
                <div key={b.id} className="card">
                  <div className="flex items-center gap-2 mb-2">
                    <h3 style={{ flex: 1, fontSize: 15, margin: 0 }}>{b.intitule}</h3>
                    <Badge color={st.color} dot>{st.label}</Badge>
                  </div>
                  {b.description && <p className="muted" style={{ fontSize: 13.5 }}>{b.description}</p>}
                  <div className="pub-meta mb-3">
                    <span><Icon name="user" size={13} /> {b.demandeurNom}</span>
                    <span>Qté : {b.quantiteDemandee}</span>
                    <span><Icon name="clock" size={13} /> {formatDate(b.createdAt)}</span>
                  </div>
                  {b.commentaireDecision && <div className="alert alert-info" style={{ fontSize: 13 }}>{b.commentaireDecision}</div>}
                  {canEdit && b.statut === 'EN_ATTENTE' && (
                    <div className="flex gap-2 mt-3">
                      <Button variant="primary" size="sm" icon="check" onClick={() => { setDecisionModal({ type: 'valider', besoin: b }); setDecisionComment(''); setDecisionValideur('') }}>Valider</Button>
                      <Button variant="danger-soft" size="sm" icon="x" onClick={() => { setDecisionModal({ type: 'refuser', besoin: b }); setDecisionComment(''); setDecisionValideur('') }}>Refuser</Button>
                    </div>
                  )}
                </div>
              )
            })}
          </div>
        )
      )}

      {/* Modal matériel */}
      {matModal && (
        <Modal title={editingMat ? 'Modifier l\'équipement' : 'Ajouter un équipement'} onClose={() => setMatModal(false)}
          footer={<><Button variant="ghost" onClick={() => setMatModal(false)}>Annuler</Button><Button onClick={saveMat} disabled={saving}>{saving ? '...' : 'Enregistrer'}</Button></>}>
          <form onSubmit={saveMat}>
            <div className="field"><label>Nom *</label><input value={matForm.nom} onChange={setMat('nom')} required /></div>
            <div className="field-row">
              <div className="field"><label>Référence</label><input value={matForm.reference} onChange={setMat('reference')} /></div>
              <div className="field"><label>Quantité</label><input type="number" min="1" value={matForm.quantite} onChange={setMat('quantite')} /></div>
            </div>
            <div className="field-row">
              <div className="field"><label>État</label><select value={matForm.etat} onChange={setMat('etat')}>{enumOptions(ETAT_MATERIEL).map(o => <option key={o.value} value={o.value}>{o.label}</option>)}</select></div>
              <div className="field"><label>Date d'arrivée</label><input type="date" value={matForm.dateArrivee} onChange={setMat('dateArrivee')} /></div>
            </div>
            <div className="field"><label>Description</label><textarea value={matForm.description} onChange={setMat('description')} /></div>
          </form>
        </Modal>
      )}

      {/* Modal attribution */}
      {attribModal && (
        <Modal title={`Attribuer "${attribModal.nom}"`} onClose={() => setAttribModal(null)}
          footer={<><Button variant="ghost" onClick={() => setAttribModal(null)}>Annuler</Button><Button onClick={attribuer}>Attribuer</Button></>}>
          <div className="field"><label>Membre</label>
            <select value={attribMembreId} onChange={e => setAttribMembreId(e.target.value)}>
              <option value="">— Sélectionner —</option>
              {membres.map(m => <option key={m.id} value={m.id}>{m.prenom} {m.nom}</option>)}
            </select>
          </div>
        </Modal>
      )}

      {/* Modal besoin */}
      {besModal && (
        <Modal title="Soumettre un besoin" onClose={() => setBesModal(false)}
          footer={<><Button variant="ghost" onClick={() => setBesModal(false)}>Annuler</Button><Button onClick={saveBes} disabled={saving}>{saving ? '...' : 'Soumettre'}</Button></>}>
          <form onSubmit={saveBes}>
            <div className="field"><label>Intitulé *</label><input value={besForm.intitule} onChange={setBes('intitule')} required /></div>
            <div className="field-row">
              <div className="field"><label>Demandeur *</label>
                <select value={besForm.demandeurId} onChange={setBes('demandeurId')}>
                  <option value="">— Sélectionner —</option>
                  {membres.map(m => <option key={m.id} value={m.id}>{m.prenom} {m.nom}</option>)}
                </select>
              </div>
              <div className="field"><label>Quantité</label><input type="number" min="1" value={besForm.quantiteDemandee} onChange={setBes('quantiteDemandee')} /></div>
            </div>
            <div className="field"><label>Description</label><textarea value={besForm.description} onChange={setBes('description')} /></div>
          </form>
        </Modal>
      )}

      {/* Modal décision */}
      {decisionModal && (
        <Modal title={decisionModal.type === 'valider' ? 'Valider le besoin' : 'Refuser le besoin'} onClose={() => setDecisionModal(null)}
          footer={<><Button variant="ghost" onClick={() => setDecisionModal(null)}>Annuler</Button>
            <Button variant={decisionModal.type === 'valider' ? 'primary' : 'danger'} onClick={confirmerDecision} disabled={saving}>{saving ? '...' : decisionModal.type === 'valider' ? 'Valider' : 'Refuser'}</Button></>}>
          <p className="mb-3 muted">{decisionModal.besoin.intitule}</p>
          <div className="field"><label>Valideur *</label>
            <select value={decisionValideur} onChange={e => setDecisionValideur(e.target.value)}>
              <option value="">— Sélectionner —</option>
              {membres.map(m => <option key={m.id} value={m.id}>{m.prenom} {m.nom}</option>)}
            </select>
          </div>
          <div className="field"><label>Commentaire</label><textarea value={decisionComment} onChange={e => setDecisionComment(e.target.value)} /></div>
        </Modal>
      )}
    </div>
  )
}
