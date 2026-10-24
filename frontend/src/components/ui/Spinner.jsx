export default function Spinner({ lg = false }) {
  return <div className={`spinner ${lg ? 'spinner-lg' : ''}`} />
}

export function PageLoader() {
  return (
    <div className="page-loader">
      <div className="spinner spinner-lg" />
    </div>
  )
}
