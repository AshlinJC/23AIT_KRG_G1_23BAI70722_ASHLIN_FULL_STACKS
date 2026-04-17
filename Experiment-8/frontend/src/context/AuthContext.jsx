import { createContext, useContext, useState, useEffect } from 'react'
import api from '../api/api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser]   = useState(null)
  const [token, setToken] = useState(() => sessionStorage.getItem('token'))
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (token) {
      try {
        // Decode JWT payload (no library required)
        const payload = JSON.parse(atob(token.split('.')[1]))
        const isExpired = payload.exp * 1000 < Date.now()
        if (isExpired) {
          logout()
        } else {
          const roles = payload.roles ? payload.roles.split(',') : []
          setUser({ email: payload.sub, roles })
        }
      } catch {
        logout()
      }
    }
    setLoading(false)
  }, [token])

  const login = async (email, password) => {
    const { data } = await api.post('/auth/login', { email, password })
    sessionStorage.setItem('token', data.token)
    setToken(data.token)
    const roles = data.roles || []
    setUser({ email: data.email, name: data.name, roles })
    return data
  }

  const logout = () => {
    sessionStorage.removeItem('token')
    setToken(null)
    setUser(null)
  }

  const storeOAuthToken = (jwt) => {
    sessionStorage.setItem('token', jwt)
    setToken(jwt)
  }

  const isAdmin = () => user?.roles?.includes('ROLE_ADMIN') ?? false
  const isAuthenticated = () => !!user

  return (
    <AuthContext.Provider value={{
      user, token, loading,
      login, logout, storeOAuthToken,
      isAdmin, isAuthenticated,
    }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
