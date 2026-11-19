/*
 * File:    Layout.jsx
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Page frame: announcement bar, header with navigation, and footer.
 */
import { Link, NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../AuthContext.jsx'
import { STORE_NAME, STORE_TAGLINE } from '../Config.js'

/**
 * Gives a nav link its style, underlined when it is the current page.
 *
 * @param {object}  state
 * @param {boolean} state.isActive true on the current page
 * @returns {string} the class names
 */
function navClass({ isActive }) {
  return `text-[15px] text-forest hover:opacity-70 ${isActive ? 'underline underline-offset-4' : ''}`
}

/**
 * The frame around every page.
 *
 * @returns {JSX.Element} the layout
 */
export default function Layout() {
  const { user, cartCount, logout } = useAuth()
  const navigate = useNavigate()
  const isLanding = useLocation().pathname === '/'

  /**
   * Logs out and goes back to the home page.
   */
  async function handleLogout() {
    await logout()
    navigate('/')
  }

  return (
    <div className="flex min-h-screen flex-col">
      <div className="label bg-forest px-4 py-2 text-center text-white">{STORE_TAGLINE}</div>

      <header className="border-b border-forest">
        <div className="mx-auto flex max-w-[1400px] flex-wrap items-center justify-between gap-4 px-4 py-5 sm:px-8">
          <Link to="/" className="flex items-center gap-2 text-forest">
            <svg width="26" height="26" viewBox="0 0 26 26" aria-hidden="true">
              <path d="M13 2 C6 8 6 18 13 24 C20 18 20 8 13 2 Z" fill="none" stroke="currentColor" strokeWidth="1.5" />
              <path d="M13 6 V24 M13 12 L9 9 M13 16 L17 13 M13 20 L9 17" stroke="currentColor" strokeWidth="1.2" />
            </svg>
            <span className="text-[20px] uppercase tracking-[0.12em]">{STORE_NAME}</span>
          </Link>

          <nav className="flex flex-wrap items-center gap-x-7 gap-y-2">
            <NavLink to="/shop" className={navClass}>Shop</NavLink>
            {user && <NavLink to="/orders" className={navClass}>Orders</NavLink>}
            {user?.admin && <NavLink to="/admin" className={navClass}>Admin</NavLink>}
            <NavLink to="/cart" className={navClass}>Cart ({cartCount})</NavLink>
            {user ? (
              <button type="button" onClick={handleLogout} className="text-[15px] text-forest hover:opacity-70">
                Log out {user.username}
              </button>
            ) : (
              <NavLink to="/login" className={navClass}>Log in</NavLink>
            )}
          </nav>
        </div>
      </header>

      {isLanding ? (
        <main className="flex-1">
          <Outlet />
        </main>
      ) : (
        <main className="mx-auto w-full max-w-[1400px] flex-1 px-4 py-10 sm:px-8">
          <Outlet />
        </main>
      )}

      <footer className="bg-night text-[#cfd8d2]">
        <div className="mx-auto flex max-w-[1400px] flex-wrap items-center justify-between gap-4 px-4 py-10 sm:px-8">
          <span className="font-display text-3xl font-light text-white">{STORE_NAME}</span>
          <div className="flex flex-wrap gap-7 text-[15px]">
            <Link to="/shop" className="hover:text-white">Shop</Link>
            <Link to="/cart" className="hover:text-white">Cart</Link>
            <Link to="/orders" className="hover:text-white">Orders</Link>
          </div>
          <span className="label w-full text-[#8fa197]">
            Built by Abdullah Waleed Ahmed and Rehan Haider · Software Construction OEL
          </span>
        </div>
      </footer>
    </div>
  )
}
