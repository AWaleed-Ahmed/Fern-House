/*
 * File:    LoginPage.jsx
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Log in or create an account.
 */
import { useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../AuthContext.jsx'

/**
 * The login and sign up page.
 *
 * @returns {JSX.Element} the page
 */
export default function LoginPage() {
  const { authenticate } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [mode, setMode] = useState('login')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [isBusy, setIsBusy] = useState(false)

  /**
   * Checks the form, then logs in or signs up.
   *
   * @param {Event} event the form submit event
   */
  async function handleSubmit(event) {
    event.preventDefault()
    if (!username.trim() || !password) {
      setError('Enter a username and password.')
      return
    }
    setError('')
    setIsBusy(true)
    try {
      const user = await authenticate(mode, username.trim(), password)
      navigate(location.state?.from || (user.admin ? '/admin' : '/'), { replace: true })
    } catch (err) {
      setError(err.message)
    } finally {
      setIsBusy(false)
    }
  }

  const isLogin = mode === 'login'
  return (
    <div className="grid items-center gap-10 lg:grid-cols-2">
      <h1 className="text-[56px] leading-[0.99] text-forest sm:text-[88px]">
        {isLogin ? 'Welcome back' : 'Join the house'}
      </h1>
      <form onSubmit={handleSubmit} className="flex max-w-[440px] flex-col gap-4 rounded-card bg-card p-8">
        <span className="label text-forest">{isLogin ? 'Log in' : 'Create account'}</span>
        <input
          className="field"
          placeholder="Username"
          autoComplete="username"
          value={username}
          onChange={(event) => setUsername(event.target.value)}
          aria-label="Username"
        />
        <input
          className="field"
          type="password"
          placeholder="Password"
          autoComplete={isLogin ? 'current-password' : 'new-password'}
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          aria-label="Password"
        />
        {!isLogin && (
          <p className="text-[13px] text-ink-muted">
            At least 3 characters for the username (letters, numbers or _) and 6 for the password.
          </p>
        )}
        {error && <p className="text-danger">{error}</p>}
        <button type="submit" className="btn" disabled={isBusy}>
          {isLogin ? 'Log in' : 'Create account'}
        </button>
        <button
          type="button"
          className="text-[14px] text-forest underline-offset-4 hover:underline"
          onClick={() => {
            setMode(isLogin ? 'register' : 'login')
            setError('')
          }}
        >
          {isLogin ? 'New here? Create an account' : 'Have an account? Log in'}
        </button>
      </form>
    </div>
  )
}
