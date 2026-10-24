import { useEffect, useState, useCallback } from 'react'
import { getRecus, getEnvoyes, sendMessage, markAsRead } from '../api/messageApi'
import { getMembres } from '../api/membreApi'
import { getEquipes } from '../api/equipeApi'
import { useToast } from '../context/ToastContext'
import { formatDate } from '../constants'
import Button from '../components/ui/Button'
import Avatar from '../components/ui/Avatar'
import Icon from '../components/ui/Icon'
import Modal from '../components/ui/Modal'
import EmptyState from '../components/ui/EmptyState'
import { PageLoader } from '../components/ui/Spinner'

const EMPTY = { destinataireId: '', equipeId: '', sujet: '', contenu: '' }

export default function MessagesPage() {
  const toast = useToast()
  const [tab, setTab] = useState('recus')
  const [recus, setRecus] = useState([])
  const [envoyes, setEnvoyes] = useState([])
  const [membres, setMembres] = useState([])
  const [equipes, setEquipes] = useState([])
  const [loading, setLoading] = useState(true)
  const [modal, setModal] = useState(false)
  const [form, setForm] = useState(EMPTY)
  const [detail, setDetail] = useState(null)
  const [saving, setSaving] = useState(false)

  const load = useCallback(() => {
    setLoading(true)
    Promise.all([getRecus(), getEnvoyes(), getMembres(), getEquipes()])
      .then(([r, e, m, eq]) => { setRecus(r); setEnvoyes(e); setMembres(m); setEquipes(eq) })
      .catch(() => toast.error('Erreur'))
      .finally(() => setLoading(false))
  }, [toast])

  useEffect(() => { load() }, [load])

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  const openDetail = async (msg) => {
    setDetail(msg)
    if (!msg.lu && tab === 'recus') {
      try { await markAsRead(msg.id); load() } catch {}
    }
  }

  const send = async (e) => {
    e.preventDefault()
    if (!form.sujet.trim() || !form.contenu.trim()) { toast.error('Sujet et contenu requis'); return }
    if (!form.destinataireId && !form.equipeId) { toast.error('Choisir un destinataire ou une équipe'); return }
    setSaving(true)
    try {
      await sendMessage({ ...form, destinataireId: form.destinataireId || null, equipeId: form.equipeId || null })
      toast.success('Message envoyé')
      setModal(false); setForm(EMPTY); load()
    } catch { toast.error('Erreur') } finally { setSaving(false) }
  }

  const list = tab === 'recus' ? recus : envoyes
  const nonLus = recus.filter(m => !m.lu).length

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="ph-title">Communication interne</div>
          <div className="ph-sub">{nonLus > 0 ? `${nonLus} message(s) non lu(s)` : 'Tous les messages lus'}</div>
        </div>
        <Button icon="send" onClick={() => { setForm(EMPTY); setModal(true) }}>Nouveau message</Button>
      </div>

      <div className="tab-bar mb-4">
        <button className={`tab-btn${tab === 'recus' ? ' active' : ''}`} onClick={() => setTab('recus')}>
          <Icon name="inbox" size={15} /> Reçus ({recus.length})
          {nonLus > 0 && <span className="notif-dot" style={{ marginLeft: 6 }}>{nonLus}</span>}
        </button>
        <button className={`tab-btn${tab === 'envoyes' ? ' active' : ''}`} onClick={() => setTab('envoyes')}>
          <Icon name="send" size={15} /> Envoyés ({envoyes.length})
        </button>
      </div>

      {loading ? <PageLoader /> : list.length === 0 ? (
        <div className="card"><EmptyState icon="messageCircle" title="Aucun message" /></div>
      ) : (
        <div className="grid" style={{ gap: 6 }}>
          {list.map(msg => (
            <div key={msg.id} className="doc-row" onClick={() => openDetail(msg)} style={{ cursor: 'pointer', background: !msg.lu && tab === 'recus' ? 'var(--indigo-50)' : undefined }}>
              <Avatar name={msg.expediteurNom || msg.expediteurEmail} size="sm" />
              <div style={{ flex: 1, minWidth: 0 }}>
                <div className="doc-name" style={{ fontWeight: !msg.lu && tab === 'recus' ? 700 : 500 }}>
                  {msg.sujet}
                </div>
                <div className="doc-meta">
                  {tab === 'recus' ? `De: ${msg.expediteurEmail}` : `À: ${msg.destinataireNom || msg.equipeNom || '—'}`}
                  {' · '}{formatDate(msg.createdAt)}
                </div>
              </div>
              {!msg.lu && tab === 'recus' && <span style={{ width: 8, height: 8, borderRadius: '50%', background: 'var(--primary)', flexShrink: 0 }} />}
              <Icon name="chevronRight" size={16} style={{ color: 'var(--slate-400)' }} />
            </div>
          ))}
        </div>
      )}

      {/* Detail modal */}
      {detail && (
        <Modal title={detail.sujet} onClose={() => setDetail(null)}
          footer={<Button onClick={() => setDetail(null)}>Fermer</Button>}>
          <div className="flex items-center gap-3 mb-4">
            <Avatar name={detail.expediteurNom || detail.expediteurEmail} size="md" />
            <div>
              <div style={{ fontWeight: 600 }}>{detail.expediteurEmail}</div>
              <div className="muted" style={{ fontSize: 13 }}>
                {detail.equipeNom ? `Équipe : ${detail.equipeNom}` : `À : ${detail.destinataireNom || 'Moi'}`}
                {' · '}{formatDate(detail.createdAt)}
              </div>
            </div>
          </div>
          <div style={{ padding: '16px', background: 'var(--slate-50)', borderRadius: 10, fontSize: 14.5, lineHeight: 1.7, whiteSpace: 'pre-wrap' }}>
            {detail.contenu}
          </div>
        </Modal>
      )}

      {/* New message modal */}
      {modal && (
        <Modal title="Nouveau message" onClose={() => setModal(false)}
          footer={<><Button variant="ghost" onClick={() => setModal(false)}>Annuler</Button><Button onClick={send} disabled={saving} icon="send">{saving ? 'Envoi...' : 'Envoyer'}</Button></>}>
          <form onSubmit={send}>
            <div className="field-row">
              <div className="field"><label>Destinataire (membre)</label>
                <select value={form.destinataireId} onChange={e => setForm({ ...form, destinataireId: e.target.value, equipeId: '' })}>
                  <option value="">— Aucun —</option>
                  {membres.map(m => <option key={m.id} value={m.utilisateurId || m.id}>{m.prenom} {m.nom}</option>)}
                </select>
              </div>
              <div className="field"><label>OU Équipe</label>
                <select value={form.equipeId} onChange={e => setForm({ ...form, equipeId: e.target.value, destinataireId: '' })}>
                  <option value="">— Aucune —</option>
                  {equipes.map(eq => <option key={eq.id} value={eq.id}>{eq.nom}</option>)}
                </select>
              </div>
            </div>
            <div className="field"><label>Sujet *</label><input value={form.sujet} onChange={set('sujet')} required /></div>
            <div className="field"><label>Message *</label><textarea value={form.contenu} onChange={set('contenu')} rows={6} required /></div>
          </form>
        </Modal>
      )}
    </div>
  )
}
