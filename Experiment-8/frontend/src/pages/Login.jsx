import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(form.email, form.password)
      navigate('/')
    } catch (err) {
      setError(err.response?.data?.error || 'Invalid email or password')
    } finally {
      setLoading(false)
    }
  }

  const handleGoogleLogin = () => {
    window.location.href = 'http://localhost:8080/oauth2/authorization/google'
  }

  return (
    <div style={{ minHeight: '80vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <div style={{ width: '100%', maxWidth: 400 }}>
        <div className="card">
          <h2 style={{ fontSize: 22, fontWeight: 700, marginBottom: 6 }}>Sign in</h2>
          <p style={{ color: '#888', fontSize: 14, marginBottom: 24 }}>
            Welcome back to LivePoll
          </p>

          <form onSubmit={handleSubmit}>
            <div style={{ marginBottom: 14 }}>
              <label style={{ fontSize: 13, color: '#555', display: 'block', marginBottom: 4 }}>Email</label>
              <input
                type="email"
                placeholder="you@example.com"
                value={form.email}
                onChange={e => setForm({ ...form, email: e.target.value })}
                required
              />
            </div>
            <div style={{ marginBottom: 20 }}>
              <label style={{ fontSize: 13, color: '#555', display: 'block', marginBottom: 4 }}>Password</label>
              <input
                type="password"
                placeholder="••••••••"
                value={form.password}
                onChange={e => setForm({ ...form, password: e.target.value })}
                required
              />
            </div>

            {error && <p className="error" style={{ marginBottom: 12 }}>{error}</p>}

            <button type="submit" className="btn-primary" style={{ width: '100%' }} disabled={loading}>
              {loading ? 'Signing in…' : 'Sign in'}
            </button>
          </form>

          <div style={{ textAlign: 'center', margin: '16px 0', color: '#aaa', fontSize: 13 }}>or</div>

          <button
            onClick={handleGoogleLogin}
            style={{
              width: '100%',
              background: '#fff',
              border: '1px solid #dde1e7',
              borderRadius: 8,
              padding: '10px 20px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: 10,
              fontSize: 14,
              fontWeight: 500,
              color: '#333',
              cursor: 'pointer',
            }}
          >
            <svg width="18" height="18" viewBox="0 0 48 48">
              <path fill="#4285F4" d="M47.5 24.5c0-1.6-.1-3.1-.4-4.5H24v8.5h13.1c-.6 3-2.3 5.5-4.9 7.2v6h7.9c4.6-4.3 7.4-10.6 7.4-17.2z"/>
              <path fill="#34A853" d="M24 48c6.5 0 11.9-2.1 15.9-5.8l-7.9-6c-2.1 1.4-4.8 2.3-8 2.3-6.1 0-11.3-4.1-13.2-9.7H2.7v6.2C6.7 42.7 14.8 48 24 48z"/>
              <path fill="#FBBC05" d="M10.8 28.8c-.5-1.4-.7-2.8-.7-4.3s.3-2.9.7-4.3v-6.2H2.7C1 17.4 0 20.6 0 24s1 6.6 2.7 9.5l8.1-4.7z"/>
              <path fill="#EA4335" d="M24 9.5c3.4 0 6.5 1.2 8.9 3.5l6.7-6.7C35.9 2.4 30.4 0 24 0 14.8 0 6.7 5.3 2.7 13.5l8.1 6.2c1.9-5.6 7.1-10.2 13.2-10.2z"/>
            </svg>
            Continue with Google
          </button>

          <p style={{ textAlign: 'center', marginTop: 20, fontSize: 13, color: '#888' }}>
            No account?{' '}
            <Link to="/register" style={{ color: '#4f6ef7', fontWeight: 500 }}>Register</Link>
          </p>

          <div style={{ marginTop: 16, padding: 12, background: '#f8f9ff', borderRadius: 8, fontSize: 12, color: '#666' }}>
            <strong>Demo credentials:</strong><br />
            Admin: admin@livepoll.com / admin123<br />
            User: user@livepoll.com / user123
          </div>
        </div>
      </div>
    </div>
  )
}
