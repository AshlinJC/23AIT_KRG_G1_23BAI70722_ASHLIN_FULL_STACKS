import { Link } from 'react-router-dom'

export default function Unauthorized() {
  return (
    <div style={{ minHeight: '80vh', display: 'flex', alignItems: 'center', justifyContent: 'center', textAlign: 'center' }}>
      <div>
        <p style={{ fontSize: 64, marginBottom: 8 }}>🔒</p>
        <h2 style={{ fontSize: 22, fontWeight: 700, marginBottom: 8 }}>Access Denied</h2>
        <p style={{ color: '#888', marginBottom: 24 }}>You don't have permission to view this page.</p>
        <Link to="/"><button className="btn-primary">Back to home</button></Link>
      </div>
    </div>
  )
}
