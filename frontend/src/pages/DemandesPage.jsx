import { useEffect, useState, useCallback } from 'react'
import { getDemandes, accepterDemande, refuserDemande } from '../api/demandeApi'
import { useToast } from '../context/ToastContext'
import { STATUT_DEMANDE, STATUT_MEMBRE, enumOptions, formatDate } from '../constants'
import Button from '../components/ui/Button'
import Badge from '../components/ui/Badge'
import Avatar from '../components/ui/Avatar'
import Icon from '../components/ui/Icon'
import Modal from '../components/ui/Modal'
import EmptyState from '../components/ui/EmptyState'
import { PageLoader } from '../components/ui/Spinner'

export default function DemandesPage() {
  const toast = useToast()
  const [demandes, setDemandes] = useState([])
  const [loading, setLoading] = useState(true)
  const [filterStatut, setFilterStatut] = useState('')
  const [action, setAction] = useState(null) // { type, demande }
  const [statut, setStatut] = useState('PERMANENT')
  const [commentaire, setCommentaire] = useState('')
  const [processing, setProcessing] = useState(false)
  const [credentials, setCredentials] = useState(null)

  const load = useCallback((s) => {
    setLoading(true)
    getDemandes(s ? { statut: s } : {}).then(setDemandes).catch(() => toast.error('Erreur de chargement')).finally(() => setLoading(false))
  }, [toast])

  useEffect(() => { load('') }, [load])

  const openAccept = (d) => { setAction({ type: 'accepter', demande: d }); setStatut('PERMANENT') }
  const openRefuse = (d) => { setAction({ type: 'refuser', demande: d }); setCommentaire('') }

  const confirm = async () => {
    setProcessing(true)
    try {
      if (action.type === 'accepter') {
        const res = await accepterDemande(action.demande.id, { statut })
        setAction(null)
        setCredentials(res)
        toast.success('Demande acceptée, compte créé')
      } else {
        await refuserDemande(action.demande.id, { commentaire })
        setAction(null)
        toast.info('Demande refusée')
      }
      load(filterStatut)
    } catch (err) {
      toast.error(err.response?.data?.message || "Échec de l'opération")
    } finally { setProcessing(false) }
  }

  const onFilter = (e) => { setFilterStatut(e.target.value); load(e.target.value) }

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="ph-title">Demandes d'adhésion</div>
          <div className="ph-sub">{demandes.length} demande(s)</div>
        </div>
        <select className="select-filter" value={filterStatut} onChange={onFilter}>
          <option value="">Tous les statuts</option>
          {enumOptions(STATUT_DEMANDE).map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
        </select>
      </div>

      {loading ? (
        <PageLoader />
      ) : demandes.length === 0 ? (
        <div className="card"><EmptyState icon="inbox" title="Aucune demande" /></div>
      ) : (
        <div className="grid grid-2">
          {demandes.map((d) => {
            const st = STATUT_DEMANDE[d.statut] || { label: d.statut, color: 'grey' }
            return (
              <div className="demande-card" key={d.id}>
                <div className="dc-head">
                  <Avatar name={`${d.prenom} ${d.nom}`} size="md" />
                  <div style={{ flex: 1 }}>
                    <h3 style={{ fontSize: 16 }}>{d.prenom} {d.nom}</h3>
                    <div className="muted" style={{ fontSize: 13 }}>{d.email}</div>
                  </div>
                  <Badge color={st.color} dot>{st.label}</Badge>
                </div>
                <div className="pub-meta mb-4">
                  {d.etablissementOrigine && <span><Icon name="building" size={13} /> {d.etablissementOrigine}</span>}
                  <span><Icon name="clock" size={13} /> {formatDate(d.createdAt)}</span>
                </div>
                {d.motivation && <p className="muted" style={{ fontSize: 13.5 }}>{d.motivation}</p>}
                {d.commentaireDecision && (
                  <div className="alert alert-info" style={{ fontSize: 13 }}><Icon name="mail" size={15} /> {d.commentaireDecision}</div>
                )}
                {d.statut === 'EN_ATTENTE' && (
                  <div className="flex gap-2 mt-4">
                    <Button variant="primary" size="sm" icon="check" onClick={() => openAccept(d)}>Accepter</Button>
                    <Button variant="danger-soft" size="sm" icon="x" onClick={() => openRefuse(d)}>Refuser</Button>
                  </div>
                )}
              </div>
            )
          })}
        </div>
      )}

      {/* Modal décision */}
      {action && (
        <Modal
          title={action.type === 'accepter' ? 'Accepter la demande' : 'Refuser la demande'}
          onClose={() => setAction(null)}
          footer={
            <>
              <Button variant="ghost" onClick={() => setAction(null)}>Annuler</Button>
              <Button variant={action.type === 'accepter' ? 'primary' : 'danger'} onClick={confirm} disabled={processing}>
                {processing ? '...' : action.type === 'accepter' ? 'Créer le compte' : 'Confirmer le refus'}
              </Button>
            </>
          }
        >
          <p className="mb-4">
            {action.demande.prenom} {action.demande.nom} — <span className="muted">{action.demande.email}</span>
          </p>
          {action.type === 'accepter' ? (
            <div className="field">
              <label>Statut attribué au nouveau membre</label>
              <select value={statut} onChange={(e) => setStatut(e.target.value)}>
                {enumOptions(STATUT_MEMBRE).map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}
              </select>
            </div>
          ) : (
            <div className="field">
              <label>Motif du refus</label>
              <textarea value={commentaire} onChange={(e) => setCommentaire(e.target.value)} placeholder="Communiqué au demandeur..." />
            </div>
          )}
        </Modal>
      )}

      {/* Modal identifiants générés */}
      {credentials && (
        <Modal
          title="Compte créé"
          onClose={() => setCredentials(null)}
          footer={<Button onClick={() => setCredentials(null)}>Terminé</Button>}
        >
          <div className="alert alert-success mb-4"><Icon name="checkCircle" size={18} /> Le compte a été créé avec succès.</div>
          <p className="muted" style={{ fontSize: 13.5 }}>Communiquez ces identifiants au nouveau membre (aucun email n'est envoyé automatiquement) :</p>
          <div className="credentials-box mt-2">
            <div><span className="ck">email</span> : {credentials.email}</div>
            <div><span className="ck">mot de passe</span> : {credentials.motDePasseTemporaire}</div>
          </div>
        </Modal>
      )}
    </div>
  )
}
