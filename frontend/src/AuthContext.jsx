/*
 * File:    AuthContext.jsx
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Shares the logged-in user and cart count with every page.
 */
import { createContext, useCallback, useContext, useEffect, useState } from 'react'
import { apiRequest, getToken, setToken } from './Api.js'

const AuthContext = createContext(null)

/**
 * Wraps the app and keeps track of who is logged in.
 *
 * @param {object} props
 * @param {React.ReactNode} props.children the app
 * @returns {JSX.Element} the provider
 */
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [isLoading, setIsLoading] = useState(Boolean(getToken()))
  const [cartCount, setCartCount] = useState(0)

  /**
   * Reloads the number of items in the cart (shown in the header).
   */
  const refreshCart = useCallback(async () => {
    if (!getToken()) {
      setCartCount(0)
      return
    }
    try {
      const cart = await apiRequest('/cart')
      setCartCount(cart.items.reduce((sum, item) => sum + item.quantity, 0))
    } catch {
      setCartCount(0)
    }
  }, [])

  useEffect(() => {
    if (!getToken()) {
      return
    }
    apiRequest('/auth/me')
      .then((me) => {
        setUser(me)
        refreshCart()
      })
      .catch(() => setToken(null))
      .finally(() => setIsLoading(false))
  }, [refreshCart])

  /**
   * Logs in or signs up and saves the token.
   *
   * @param {'login'|'register'} mode     which action to take
   * @param {string}             username the username
   * @param {string}             password the password
   * @returns {Promise<object>} the logged-in user
   */
  async function authenticate(mode, username, password) {
    const result = await apiRequest(`/auth/${mode}`, {
      method: 'POST',
      body: { username, password },
    })
    setToken(result.token)
    setUser(result.user)
    await refreshCart()
    return result.user
  }

  /**
   * Logs out on the server and in the browser.
   */
  async function logout() {
    try {
      await apiRequest('/auth/logout', { method: 'POST' })
    } catch {
      // Logging out locally is enough if the server can't be reached.
    }
    setToken(null)
    setUser(null)
    setCartCount(0)
  }

  const value = { user, isLoading, cartCount, refreshCart, authenticate, logout }
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

/**
 * Gives a component the login state and actions.
 *
 * @returns {object} user, isLoading, cartCount, refreshCart, authenticate and logout
 */
export function useAuth() {
  return useContext(AuthContext)
}
