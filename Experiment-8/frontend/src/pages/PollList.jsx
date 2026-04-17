import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api/api'
import { useAuth } from '../context/AuthContext'

export default function PollList() {
  const { user } = useAuth()
  const [polls, setPolls] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/polls')
      .then(r => setPolls(r.data))
      .catch(console.error)
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="page" style={{ textAlign: 'center', paddingTop: 80 }}>Loading polls…</div>

  return (
    <div className="page">
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 28 }}>
        <div>
          <h1 style={{ fontSize: 26, fontWeight: 700 }}>Active Polls</h1>
          <p style={{ color: '#888', fontSize: 14, marginTop: 4 }}>
            {user ? `Logged in as ${user.email}` : 'Log in to vote'}
          </p>
        </div>
        {!user && (
          <Link to="/login">
            <button className="btn-primary">Sign in to vote</button>
          </Link>
        )}
      </div>

      {polls.length === 0 && (
        <div className="card" style={{ textAlign: 'center', color: '#aaa', padding: 48 }}>
          No active polls yet.
        </div>
      )}

      {polls.map(poll => (
        <div key={poll.id} className="card">
          <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: 12 }}>
            <div style={{ flex: 1 }}>
              <h3 style={{ fontSize: 17, fontWeight: 600, marginBottom: 10 }}>{poll.question}</h3>

              {/* Vote bars (read-only preview) */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                {poll.options.map(opt => {
                  const total = poll.options.reduce((s, o) => s + o.voteCount, 0)
                  const pct = total > 0 ? Math.round((opt.voteCount / total) * 100) : 0
                  return (
                    <div key={opt.id}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13, marginBottom: 3 }}>
                        <span>{opt.text}</span>
                        <span style={{ color: '#888' }}>{opt.voteCount} votes · {pct}%</span>
                      </div>
                      <div style={{ height: 6, background: '#eef0f5', borderRadius: 99 }}>
                        <div style={{
                          height: '100%',
                          width: `${pct}%`,
                          background: '#4f6ef7',
                          borderRadius: 99,
                          transition: 'width 0.4s ease',
                        }} />
                      </div>
                    </div>
                  )
                })}
              </div>

              <p style={{ fontSize: 12, color: '#bbb', marginTop: 10 }}>
                {poll.options.reduce((s, o) => s + o.voteCount, 0)} total votes
              </p>
            </div>

            {user && (
              <Link to={`/polls/${poll.id}`}>
                <button className="btn-primary" style={{ whiteSpace: 'nowrap', padding: '8px 16px' }}>
                  Vote
                </button>
              </Link>
            )}
          </div>
        </div>
      ))}
    </div>
  )
}
