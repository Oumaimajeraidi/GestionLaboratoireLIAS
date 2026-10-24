import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { PageLoader } from './ui/Spinner'

/**
 * Protege les routes : redirige vers /login si non connecte,
 * vers le tableau de bord si le role requis n'est pas present.
 */
export default function ProtectedRoute({ roles }) {
  const { user, loading } = useAuth()

  if (loading) {
    return <PageLoader />
  }

  if (!user) {
    return <Navigate to="/login" replace />
  }

  if (roles && roles.length > 0) {
    const autorise = roles.some((r) => user.roles?.includes(`ROLE_${r}`))
    if (!autorise) {
      return <Navigate to="/dashboard" replace />
    }
  }

  return <Outlet />
}
