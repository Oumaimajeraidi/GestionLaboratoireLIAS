import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { getMembre } from '../api/membreApi'
import { getPublications } from '../api/publicationApi'
import { STATUT_MEMBRE, formatDate } from '../constants'
import Card from '../components/ui/Card'
import Badge from '../components/ui/Badge'
import Avatar from '../components/ui/Avatar'
import Icon from '../components/ui/Icon'
import { PageLoader } from '../components/ui/Spinner'

export default function MembreDetailPage() {
  const { id } = useParams()
  const [membre, setMembre] = useState(null)
  const [publications, setPublications] = useState([])
  const [erreur, setErreur] = useState(null)

  useEffect(() => {
    getMembre(id).then(setMembre).catch(() => setErreur('Membre introuvable'))
    getPublications({ auteurId: id }).then(setPublications).catch(() => {})
  }, [id])

  if (erreur) return <div className="alert alert-error"><Icon name="xCircle" size={18} /> {erreur}</div>
  if (!membre) return <PageLoader />

  const st = STATUT_MEMBRE[membre.statut] || { label: membre.statut, color: 'grey' }

  return (
    <div>
      <Link to="/membres" className="btn btn-ghost btn-sm mb-4"><Icon name="arrowLeft" /> Retour</Link>

      <Card className="mb-4">
        <div className="profile-hero">
          <Avatar name={`${membre.prenom} ${membre.nom}`} size="lg" />
          <div>
            <h1 style={{ fontSize: 26 }}>{membre.prenom} {membre.nom}</h1>
            <div className="flex items-center gap-2 mt-2">
              <Badge color={st.color} dot>{st.label}</Badge>
              {membre.equipeNom && <Badge color="indigo">{membre.equipeNom}</Badge>}
            </div>
          </div>
        </div>
      </Card>

      <div className="grid grid-2">
        <Card>
          <h3 className="mb-4">Informations</h3>
          <dl className="profile-grid">
            <div><dt>Email</dt><dd>{membre.email}</dd></div>
            <div><dt>Équipe actuelle</dt><dd>{membre.equipeNom || '—'}</dd></div>
            <div><dt>Établissement d'origine</dt><dd>{membre.etablissementOrigine || '—'}</dd></div>
            <div><dt>Laboratoire d'origine</dt><dd>{membre.laboratoireOrigine || '—'}</dd></div>
            <div><dt>Date d'embauche</dt><dd>{formatDate(membre.dateEmbauche)}</dd></div>
            <div><dt>Date d'affiliation</dt><dd>{formatDate(membre.dateAffiliation)}</dd></div>
            <div><dt>Centres d'intérêt</dt><dd>{membre.centresInteret || '—'}</dd></div>
          </dl>
          {membre.biographie && (
            <>
              <div className="divider" />
              <h3 style={{ fontSize: 15 }}>Biographie</h3>
              <p className="muted" style={{ marginBottom: 0 }}>{membre.biographie}</p>
            </>
          )}
        </Card>

        <Card>
          <div className="card-head">
            <h3>Publications</h3>
            <Badge color="violet">{publications.length}</Badge>
          </div>
          {publications.length === 0 ? (
            <p className="muted">Aucune publication enregistrée.</p>
          ) : (
            publications.map((p) => (
              <div className="pub-item" key={p.id}>
                <div className="pub-year">{p.annee || '—'}</div>
                <div style={{ flex: 1 }}>
                  <h4>{p.titre}</h4>
                  {p.venue && <div className="pub-meta"><Icon name="book" size={13} /> {p.venue}</div>}
                </div>
              </div>
            ))
          )}
        </Card>
      </div>
    </div>
  )
}
