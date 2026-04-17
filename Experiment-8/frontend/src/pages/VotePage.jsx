import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import api from '../api/api'

export default function VotePage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [poll, setPoll] = useState(null)
  const [selected, setSelected] = useState(null)
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [voted, setVoted] = useState(false)

  useEffect(() => {
    api.get(`/polls/${id}`)
      .then(r => setPoll(r.data))
      .catch(() => navigate('/'))
      .finally(() => setLoading(false))
  }, [id])

  const handleVote = async () => {
    if (!selected) return
    setSubmitting(true)
    setError('')
    try {
      await api.post(`/polls/${id}/vote?optionId=${selected}`)
      setMessage('Your vote has been recorded!')
      setVoted(true)
      // Refresh poll data to show updated counts
      const r = await api.get(`/polls/${id}`)
      setPoll(r.data)
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to submit vote')
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) return <div className="page" style={{ textAlign: 'center', paddingTop: 80 }}>Loading…</div>
  if (!poll) return null

  const totalVotes = poll.options.reduce((s, o) => s + o.voteCount, 0)

  return (
    <div className="page" style={{ maxWidth: 600 }}>
      <button
        onClick={() => navigate('/')}
        style={{ background: 'none', border: 'none', color: '#4f6ef7', fontSize: 14, padding: 0, marginBottom: 20, cursor: 'pointer' }}
      >
        ← Back to polls
      </button>

      <div className="card">
        <h2 style={{ fontSize: 20, fontWeight: 700, marginBottom: 6 }}>{poll.question}</h2>
        <p style={{ fontSize: 13, color: '#aaa', marginBottom: 24 }}>{totalVotes} votes · {poll.active ? 'Active' : 'Closed'}</p>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 10, marginBottom: 24 }}>
          {poll.options.map(opt => {
            const pct = totalVotes > 0 ? Math.round((opt.voteCount / totalVotes) * 100) : 0
            const isSelected = selected === opt.id

            return (
              <div
                key={opt.id}
                onClick={() => !voted && setSelected(opt.id)}
                style={{
                  border: `2px solid ${isSelected ? '#4f6ef7' : '#e8ebf0'}`,
                  borderRadius: 10,
                  padding: '12px 16px',
                  cursor: voted ? 'default' : 'pointer',
                  background: isSelected ? '#f0f3ff' : '#fafafa',
                  transition: 'all 0.15s',
                  position: 'relative',
                  overflow: 'hidden',
                }}
              >
                {/* vote bar background */}
                {voted && (
                  <div style={{
                    position: 'absolute', top: 0, left: 0,
                    height: '100%', width: `${pct}%`,
                    background: isSelected ? '#dde4ff' : '#f0f2f7',
                    zIndex: 0,
                    transition: 'width 0.5s ease',
                  }} />
                )}
                <div style={{ position: 'relative', zIndex: 1, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                    <div style={{
                      width: 18, height: 18, borderRadius: '50%',
                      border: `2px solid ${isSelected ? '#4f6ef7' : '#ccc'}`,
                      background: isSelected ? '#4f6ef7' : 'transparent',
                      display: 'flex', alignItems: 'center', justifyContent: 'center',
                      flexShrink: 0,
                    }}>
                      {isSelected && <div style={{ width: 6, height: 6, borderRadius: '50%', background: '#fff' }} />}
                    </div>
                    <span style={{ fontSize: 15 }}>{opt.text}</span>
                  </div>
                  {voted && <span style={{ fontSize: 13, color: '#666', fontWeight: 600 }}>{pct}%</span>}
                </div>
              </div>
            )
          })}
        </div>

        {message && <p className="success" style={{ marginBottom: 12 }}>{message}</p>}
        {error   && <p className="error"   style={{ marginBottom: 12 }}>{error}</p>}

        {!voted ? (
          <button
            className="btn-primary"
            style={{ width: '100%' }}
            onClick={handleVote}
            disabled={!selected || submitting}
          >
            {submitting ? 'Submitting…' : 'Submit vote'}
          </button>
        ) : (
          <button className="btn-secondary" style={{ width: '100%' }} onClick={() => navigate('/')}>
            Back to all polls
          </button>
        )}
      </div>
    </div>
  )
}
