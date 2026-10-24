import { useEffect, useMemo, useState } from 'react'
import { getEvenements } from '../api/evenementApi'
import { TYPE_EVENEMENT } from '../constants'
import Card from '../components/ui/Card'
import Badge from '../components/ui/Badge'
import Icon from '../components/ui/Icon'

const DOW = ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim']

const toKey = (d) => d.toISOString().slice(0, 10)

export default function CalendrierPage() {
  const [evenements, setEvenements] = useState([])
  const [cursor, setCursor] = useState(() => {
    const n = new Date()
    return { year: n.getFullYear(), month: n.getMonth() }
  })

  useEffect(() => {
    getEvenements().then(setEvenements).catch(() => {})
  }, [])

  // Index des événements par jour (du début à la fin inclus).
  const eventsByDay = useMemo(() => {
    const map = {}
    for (const ev of evenements) {
      if (!ev.dateDebut) continue
      const start = new Date(ev.dateDebut)
      const end = ev.dateFin ? new Date(ev.dateFin) : start
      for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
        const k = toKey(d)
        ;(map[k] = map[k] || []).push(ev)
      }
    }
    return map
  }, [evenements])

  const { year, month } = cursor
  const firstDay = new Date(year, month, 1)
  const daysInMonth = new Date(year, month + 1, 0).getDate()
  const leading = (firstDay.getDay() + 6) % 7 // lundi en premier
  const todayKey = toKey(new Date())
  const monthLabel = firstDay.toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' })

  const cells = []
  for (let i = 0; i < leading; i++) cells.push(null)
  for (let d = 1; d <= daysInMonth; d++) cells.push(d)

  const move = (delta) => {
    let m = month + delta
    let y = year
    if (m < 0) { m = 11; y-- }
    if (m > 11) { m = 0; y++ }
    setCursor({ year: y, month: m })
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="ph-title">Calendrier</div>
          <div className="ph-sub">Visualisation temporelle des événements</div>
        </div>
        <div className="flex items-center gap-2">
          {Object.entries(TYPE_EVENEMENT).map(([k, v]) => (
            <Badge key={k} color={v.color}>{v.label}</Badge>
          ))}
        </div>
      </div>

      <Card>
        <div className="calendar-head">
          <button className="icon-btn" onClick={() => move(-1)}><Icon name="arrowLeft" size={18} /></button>
          <h3>{monthLabel}</h3>
          <button className="icon-btn" onClick={() => move(1)} style={{ transform: 'rotate(180deg)' }}><Icon name="arrowLeft" size={18} /></button>
        </div>

        <div className="calendar-grid">
          {DOW.map((d) => <div key={d} className="cal-dow">{d}</div>)}
          {cells.map((day, i) => {
            if (day === null) return <div key={`e${i}`} className="cal-cell empty" />
            const key = toKey(new Date(year, month, day))
            const dayEvents = eventsByDay[key] || []
            return (
              <div key={key} className={`cal-cell ${key === todayKey ? 'today' : ''}`}>
                <div className="cal-num">{day}</div>
                {dayEvents.slice(0, 3).map((ev) => (
                  <div key={ev.id} className={`cal-ev ${ev.type}`} title={ev.titre}>{ev.titre}</div>
                ))}
                {dayEvents.length > 3 && <div className="muted" style={{ fontSize: 10.5 }}>+{dayEvents.length - 3}</div>}
              </div>
            )
          })}
        </div>
      </Card>
    </div>
  )
}
