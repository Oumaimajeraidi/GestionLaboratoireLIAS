import Icon from './Icon'

export default function Button({
  variant = 'primary',
  size,
  icon,
  children,
  className = '',
  ...props
}) {
  const cls = ['btn', `btn-${variant}`, size && `btn-${size}`, className]
    .filter(Boolean)
    .join(' ')
  return (
    <button className={cls} {...props}>
      {icon && <Icon name={icon} />}
      {children}
    </button>
  )
}
