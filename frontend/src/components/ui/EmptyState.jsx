import Icon from './Icon'

export default function EmptyState({ icon = 'inbox', title, children }) {
  return (
    <div className="empty-state">
      <div className="ico">
        <Icon name={icon} size={26} />
      </div>
      <h4>{title}</h4>
      {children && <p>{children}</p>}
    </div>
  )
}
