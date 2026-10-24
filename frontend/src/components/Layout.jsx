import { Outlet } from 'react-router-dom'
import Navbar from './Navbar'

export default function Layout() {
  return (
    <div className="app-shell">
      <Navbar />
      <main className="app-main">
        <Outlet />
      </main>
      <footer className="app-footer">
        Laboratoire LIAS &middot; Faculte des Sciences Ben M'Sik &middot; Casablanca
      </footer>
    </div>
  )
}
