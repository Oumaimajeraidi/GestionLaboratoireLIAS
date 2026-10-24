export default function Card({ children, className = '', pad0 = false, ...rest }) {
  return (
    <div className={`card ${pad0 ? 'card-pad-0' : ''} ${className}`} {...rest}>
      {children}
    </div>
  )
}
