export default function Badge({ color = 'grey', children, dot = false }) {
  return (
    <span className={`badge badge-${color}`}>
      {dot && <span className="dot" />}
      {children}
    </span>
  )
}
