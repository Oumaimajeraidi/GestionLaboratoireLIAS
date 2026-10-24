export default function Avatar({ name = '', size = 'md', className = '' }) {
  const initials = name
    .split(' ')
    .filter(Boolean)
    .slice(0, 2)
    .map((s) => s[0])
    .join('')
  return <div className={`avatar avatar-${size} ${className}`}>{initials || '?'}</div>
}
