import { useState } from 'react'
import { useNavigate, useLocation, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import Button from '../components/ui/Button'
import Icon from '../components/ui/Icon'

const POINTS = [
  'Gestion complète du cycle de vie des membres',
  'Traçabilité totale des décisions et activités',
  'Publications, événements et archives centralisés',
]

export default function LoginPage() {
  const [email, setEmail] = useState('')
  const [motDePasse, setMotDePasse] = useState('')
  const [erreur, setErreur] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const from = location.state?.from?.pathname || '/dashboard'

  const handleSubmit = async (e) => {
    e.preventDefault()
    setErreur(null)
    setSubmitting(true)
    try {
      await login(email, motDePasse)
      navigate(from, { replace: true })
    } catch (err) {
      setErreur(err.response?.data?.message || 'Échec de la connexion')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="auth-split">
      <div className="auth-brand">
        <div className="ab-inner">
          <div className="ab-logo">L</div>
          <h1>Laboratoire LIAS</h1>
          <p>La plateforme qui centralise la mémoire et les activités du laboratoire.</p>
          <div className="ab-points">
            {POINTS.map((p) => (
              <div className="ab-point" key={p}>
                <span className="tick"><Icon name="check" size={15} /></span>
                {p}
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="auth-form-side">
        <form className="auth-form" onSubmit={handleSubmit}>
          <h2>Connexion</h2>
          <p className="muted mb-4">Accédez à votre espace de travail.</p>

          {erreur && (
            <div className="alert alert-error mb-4">
              <Icon name="xCircle" size={18} /> {erreur}
            </div>
          )}

          <div className="field">
            <label>Email</label>
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)}
              placeholder="exemple@lias.ma" required />
          </div>
          <div className="field">
            <label>Mot de passe</label>
            <input type="password" value={motDePasse} onChange={(e) => setMotDePasse(e.target.value)}
              placeholder="••••••••" required />
          </div>

          <Button type="submit" variant="primary" size="lg" className="btn-block" disabled={submitting}>
            {submitting ? 'Connexion...' : 'Se connecter'}
            {!submitting && <Icon name="chevronRight" />}
          </Button>

          <div className="auth-hint">
            <strong>Comptes de démonstration</strong>
            <div className="mt-2">
              <code>admin@lias.ma</code> · <code>directeur@lias.ma</code> · <code>membre@lias.ma</code>
              <br />Mot de passe : <code>admin123</code> / <code>directeur123</code> / <code>membre123</code>
            </div>
          </div>

          <p className="muted mt-4 text-center" style={{ fontSize: 13.5 }}>
            Pas encore membre ? <Link to="/adhesion">Demander une adhésion</Link>
          </p>
        </form>
      </div>
    </div>
  )
}
