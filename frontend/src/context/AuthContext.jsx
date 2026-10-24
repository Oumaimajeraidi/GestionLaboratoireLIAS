import { createContext, useContext, useEffect, useState } from 'react'
import * as authApi from '../api/authApi'
import { TOKEN_KEY } from '../api/axiosClient'

const AuthContext = createContext(null)

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => useContext(AuthContext)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  // Restaure la session si un token est present au demarrage.
  useEffect(() => {
    const token = localStorage.getItem(TOKEN_KEY)
    if (!token) {
      setLoading(false)
      return
    }
    authApi
      .fetchCurrentUser()
      .then(setUser)
      .catch(() => localStorage.removeItem(TOKEN_KEY))
      .finally(() => setLoading(false))
  }, [])

  const login = async (email, motDePasse) => {
    const data = await authApi.login(email, motDePasse)
    localStorage.setItem(TOKEN_KEY, data.token)
    setUser(data)
    return data
  }

  const logout = () => {
    localStorage.removeItem(TOKEN_KEY)
    setUser(null)
  }

  const hasRole = (role) => {
    if (!user || !user.roles) return false
    const full = role.startsWith('ROLE_') ? role : `ROLE_${role}`
    return user.roles.includes(full)
  }

  const value = { user, loading, login, logout, hasRole }
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
