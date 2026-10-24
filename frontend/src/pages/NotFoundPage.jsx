import { Link } from 'react-router-dom'
import Icon from '../components/ui/Icon'

export default function NotFoundPage() {
  return (
    <div style={{ minHeight: '100vh', display: 'grid', placeItems: 'center', padding: 20 }}>
      <div className="card text-center" style={{ maxWidth: 420 }}>
        <div style={{ fontFamily: 'var(--font-display)', fontSize: 64, fontWeight: 800, color: 'var(--primary)' }}>404</div>
        <h2>Page introuvable</h2>
        <p className="muted">La page que vous cherchez n'existe pas ou a été déplacée.</p>
        <Link to="/" className="btn btn-primary mt-4"><Icon name="arrowLeft" /> Retour à l'accueil</Link>
      </div>
    </div>
  )
}
