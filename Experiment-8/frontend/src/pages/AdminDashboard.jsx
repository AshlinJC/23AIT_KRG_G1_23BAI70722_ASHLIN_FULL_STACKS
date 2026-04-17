import { useEffect, useState } from 'react'
import api from '../api/api'

export default function AdminDashboard() {
  const [stats, setStats]   = useState(null)
  const [polls, setPolls]   = useState([])
  const [users, setUsers]   = useState([])
  const [tab, setTab]       = useState('polls')
  const [question, setQuestion]   = useState('')
  const [optionsText, setOptionsText] = useState('')
  const [createMsg, setCreateMsg] = useState('')
  const [createErr, setCreateErr] = useState('')

  const loadData = () => {
    api.get('/admin/stats').then(r => setStats(r.data))
    api.get('/admin/polls').then(r => setPolls(r.data))
    api.get('/admin/users').then(r => setUsers(r.data))
  }

  useEffect(() => { loadData() }, [])

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this poll?')) return
    await api.delete(`/polls/${id}`)
    loadData()
  }

  const handleToggle = async (id) => {
    await api.put(`/polls/${id}/toggle`)
    loadData()
  }

  const handleCreate = async (e) => {
    e.preventDefault()
    setCreateMsg(''); setCreateErr('')
    const options = optionsText.split('\n').map(s => s.trim()).filter(Boolean)
    if (options.length < 2) { setCreateErr('Enter at least 2 options (one per line)'); return }
    try {
      await api.post('/polls', { question, options })
      setCreateMsg('Poll created!')
      setQuestion(''); setOptionsText('')
      loadData()
    } catch (err) {
      setCreateErr(err.response?.data?.error || 'Failed to create poll')
    }
  }

  return (
    <div className="page">
      <h1 style={{ fontSize: 26, fontWeight: 700, marginBottom: 8 }}>Admin Dashboard</h1>

      {/* Stats */}
      {stats && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 12, marginBottom: 28 }}>
          {[
            { label: 'Total Polls',  value: stats.totalPolls  },
            { label: 'Active Polls', value: stats.activePolls },
            { label: 'Total Users',  value: stats.totalUsers  },
          ].map(s => (
            <div key={s.label} style={{
              background: '#fff', border: '1px solid #e8ebf0',
              borderRadius: 10, padding: '16px 20px',
            }}>
              <p style={{ fontSize: 12, color: '#888', marginBottom: 4 }}>{s.label}</p>
              <p style={{ fontSize: 28, fontWeight: 700, color: '#4f6ef7' }}>{s.value}</p>
            </div>
          ))}
        </div>
      )}

      {/* Tabs */}
      <div style={{ display: 'flex', gap: 8, marginBottom: 20 }}>
        {['polls', 'users', 'create'].map(t => (
          <button
            key={t}
            onClick={() => setTab(t)}
            style={{
              padding: '7px 18px',
              borderRadius: 8,
              border: '1px solid',
              borderColor: tab === t ? '#4f6ef7' : '#e8ebf0',
              background: tab === t ? '#4f6ef7' : '#fff',
              color: tab === t ? '#fff' : '#555',
              fontSize: 14,
              fontWeight: tab === t ? 600 : 400,
              cursor: 'pointer',
            }}
          >
            {t.charAt(0).toUpperCase() + t.slice(1)}
          </button>
        ))}
      </div>

      {/* Polls tab */}
      {tab === 'polls' && (
        <div>
          {polls.map(poll => (
            <div key={poll.id} className="card" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 12 }}>
              <div>
                <p style={{ fontWeight: 600, marginBottom: 4 }}>{poll.question}</p>
                <p style={{ fontSize: 13, color: '#888' }}>
                  {poll.options.reduce((s, o) => s + o.voteCount, 0)} votes · {poll.options.length} options
                </p>
                <span className={`badge ${poll.active ? 'badge-active' : 'badge-closed'}`} style={{ marginTop: 6 }}>
                  {poll.active ? 'Active' : 'Closed'}
                </span>
              </div>
              <div style={{ display: 'flex', gap: 8, flexShrink: 0 }}>
                <button className="btn-secondary" style={{ padding: '6px 14px', fontSize: 13 }} onClick={() => handleToggle(poll.id)}>
                  {poll.active ? 'Close' : 'Reopen'}
                </button>
                <button className="btn-danger" style={{ padding: '6px 14px', fontSize: 13 }} onClick={() => handleDelete(poll.id)}>
                  Delete
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Users tab */}
      {tab === 'users' && (
        <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: 14 }}>
            <thead>
              <tr style={{ background: '#f8f9ff' }}>
                <th style={{ padding: '12px 16px', textAlign: 'left', fontWeight: 600, color: '#555', borderBottom: '1px solid #e8ebf0' }}>Name</th>
                <th style={{ padding: '12px 16px', textAlign: 'left', fontWeight: 600, color: '#555', borderBottom: '1px solid #e8ebf0' }}>Email</th>
                <th style={{ padding: '12px 16px', textAlign: 'left', fontWeight: 600, color: '#555', borderBottom: '1px solid #e8ebf0' }}>Provider</th>
                <th style={{ padding: '12px 16px', textAlign: 'left', fontWeight: 600, color: '#555', borderBottom: '1px solid #e8ebf0' }}>Roles</th>
              </tr>
            </thead>
            <tbody>
              {users.map(u => (
                <tr key={u.id} style={{ borderBottom: '1px solid #f0f2f5' }}>
                  <td style={{ padding: '10px 16px' }}>{u.name}</td>
                  <td style={{ padding: '10px 16px', color: '#555' }}>{u.email}</td>
                  <td style={{ padding: '10px 16px' }}>
                    <span style={{ fontSize: 12, background: u.provider === 'GOOGLE' ? '#fff3cd' : '#e8f5e9', color: u.provider === 'GOOGLE' ? '#856404' : '#2e7d32', padding: '2px 8px', borderRadius: 99, fontWeight: 500 }}>
                      {u.provider}
                    </span>
                  </td>
                  <td style={{ padding: '10px 16px' }}>
                    {u.roles?.map(r => (
                      <span key={r} className={`badge ${r === 'ADMIN' ? 'badge-admin' : 'badge-user'}`} style={{ marginRight: 4 }}>{r}</span>
                    ))}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Create poll tab */}
      {tab === 'create' && (
        <div className="card" style={{ maxWidth: 520 }}>
          <h3 style={{ fontSize: 17, fontWeight: 600, marginBottom: 20 }}>Create new poll</h3>
          <form onSubmit={handleCreate}>
            <div style={{ marginBottom: 16 }}>
              <label style={{ fontSize: 13, color: '#555', display: 'block', marginBottom: 4 }}>Question</label>
              <input
                type="text"
                placeholder="What is your question?"
                value={question}
                onChange={e => setQuestion(e.target.value)}
                required
              />
            </div>
            <div style={{ marginBottom: 20 }}>
              <label style={{ fontSize: 13, color: '#555', display: 'block', marginBottom: 4 }}>
                Options <span style={{ color: '#aaa', fontWeight: 400 }}>(one per line, min 2)</span>
              </label>
              <textarea
                rows={5}
                placeholder={"Option A\nOption B\nOption C"}
                value={optionsText}
                onChange={e => setOptionsText(e.target.value)}
                style={{
                  width: '100%', fontFamily: 'inherit', fontSize: 14,
                  padding: '10px 14px', border: '1px solid #dde1e7',
                  borderRadius: 8, resize: 'vertical', outline: 'none',
                }}
                required
              />
            </div>
            {createMsg && <p className="success" style={{ marginBottom: 10 }}>{createMsg}</p>}
            {createErr && <p className="error"   style={{ marginBottom: 10 }}>{createErr}</p>}
            <button type="submit" className="btn-primary" style={{ width: '100%' }}>
              Create poll
            </button>
          </form>
        </div>
      )}
    </div>
  )
}
