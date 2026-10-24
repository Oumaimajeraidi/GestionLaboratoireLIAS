import { useState } from 'react'
import { Link } from 'react-router-dom'
import { submitDemande } from '../api/demandeApi'
import Button from '../components/ui/Button'
import Icon from '../components/ui/Icon'

const EMPTY = { nom: '', prenom: '', email: '', etablissementOrigine: '', motivation: '' }

export default function AdhesionPage() {
  const [form, setForm] = useState(EMPTY)
  const [submitting, setSubmitting] = useState(false)
  const [done, setDone] = useState(false)
  const [erreur, setErreur] = useState(null)

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setErreur(null)
    setSubmitting(true)
    try {
      await submitDemande(form)
      setDone(true)
    } catch (err) {
      setErreur(err.response?.data?.message || "Échec de l'envoi de la demande")
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="section" style={{ maxWidth: 640 }}>
      {done ? (
        <div className="card text-center">
          <div className="f-ico" style={{ margin: '0 auto 16px', width: 60, height: 60, background: 'var(--green-50)', color: 'var(--green-600)' }}>
            <Icon name="checkCircle" size={30} />
          </div>
          <h2>Demande envoyée</h2>
          <p className="muted">
            Votre demande d'adhésion a bien été transmise au directeur du laboratoire.
            Vous serez contacté après examen de votre dossier.
          </p>
          <Link to="/" className="btn btn-primary mt-4"><Icon name="arrowLeft" /> Retour à l'accueil</Link>
        </div>
      ) : (
        <>
          <div className="text-center mb-4">
            <h1 className="section-title">Demande d'adhésion</h1>
            <p className="section-sub">Rejoignez le laboratoire LIAS. Votre demande sera examinée par la direction.</p>
          </div>
          <form className="card" onSubmit={handleSubmit}>
            {erreur && <div className="alert alert-error mb-4"><Icon name="xCircle" size={18} /> {erreur}</div>}
            <div className="field-row">
              <div className="field"><label>Nom</label><input value={form.nom} onChange={set('nom')} required /></div>
              <div className="field"><label>Prénom</label><input value={form.prenom} onChange={set('prenom')} required /></div>
            </div>
            <div className="field"><label>Email</label><input type="email" value={form.email} onChange={set('email')} required /></div>
            <div className="field"><label>Établissement d'origine</label><input value={form.etablissementOrigine} onChange={set('etablissementOrigine')} /></div>
            <div className="field"><label>Motivation</label><textarea value={form.motivation} onChange={set('motivation')} placeholder="Présentez votre parcours et vos motivations..." /></div>
            <Button type="submit" variant="primary" className="btn-block" disabled={submitting}>
              {submitting ? 'Envoi...' : 'Envoyer la demande'} {!submitting && <Icon name="mail" />}
            </Button>
          </form>
        </>
      )}
    </div>
  )
}
