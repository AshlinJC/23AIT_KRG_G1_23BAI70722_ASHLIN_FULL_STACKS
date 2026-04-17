import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { user, logout, isAdmin } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav style={{
      background: '#fff',
      borderBottom: '1px solid #e8ebf0',
      padding: '0 24px',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      height: 56,
      position: 'sticky',
      top: 0,
      zIndex: 10,
    }}>
      <Link to="/" style={{ fontWeight: 700, fontSize: 18, color: '#4f6ef7' }}>
        LivePoll
      </Link>

      <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
        <Link to="/" style={{ fontSize: 14, color: '#555' }}>Polls</Link>

        {isAdmin() && (
          <Link to="/admin" style={{ fontSize: 14, color: '#555' }}>Admin</Link>
        )}

        {user ? (
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <span style={{ fontSize: 13, color: '#888' }}>
              {user.email}
              {isAdmin() && (
                <span className="badge badge-admin" style={{ marginLeft: 6 }}>Admin</span>
              )}
            </span>
            <button onClick={handleLogout} className="btn-secondary" style={{ padding: '6px 14px' }}>
              Logout
            </button>
          </div>
        ) : (
          <div style={{ display: 'flex', gap: 8 }}>
            <Link to="/login">
              <button className="btn-secondary" style={{ padding: '6px 14px' }}>Login</button>
            </Link>
            <Link to="/register">
              <button className="btn-primary" style={{ padding: '6px 14px' }}>Register</button>
            </Link>
          </div>
        )}
      </div>
    </nav>
  )
}
