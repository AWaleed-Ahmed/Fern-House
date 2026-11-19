/*
 * File:    CartPage.jsx
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: The user's cart (a linked list on the backend), checkout and suggestions.
 */
import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { apiRequest } from '../Api.js'
import { useAuth } from '../AuthContext.jsx'
import { formatPrice } from '../Config.js'
import ProductCard from '../components/ProductCard.jsx'
import ProductImage from '../components/ProductImage.jsx'
import SectionLabel from '../components/SectionLabel.jsx'

/**
 * The cart page.
 *
 * @returns {JSX.Element} the page
 */
export default function CartPage() {
  const { user, isLoading, refreshCart } = useAuth()
  const [cart, setCart] = useState({ items: [], total: 0 })
  const [recommendations, setRecommendations] = useState([])
  const [error, setError] = useState('')
  const [order, setOrder] = useState(null)
  const [cartVersion, setCartVersion] = useState(0)

  /**
   * Loads the cart and the suggestions from the backend.
   */
  const loadCart = useCallback(async () => {
    try {
      setCart(await apiRequest('/cart'))
      setRecommendations(await apiRequest('/recommendations'))
    } catch (err) {
      setError(err.message)
    }
  }, [])

  useEffect(() => {
    if (user) {
      loadCart()
    }
  }, [user, loadCart])

  /**
   * Runs a cart change, then shows the new cart or the error.
   *
   * @param {string} path   API path after /api
   * @param {object} options request options for apiRequest
   */
  async function changeCart(path, options) {
    setError('')
    try {
      setCart(await apiRequest(path, options))
      await refreshCart()
    } catch (err) {
      setError(err.message)
    } finally {
      setCartVersion((version) => version + 1)
    }
  }

  /**
   * Buys everything in the cart.
   */
  async function handleCheckout() {
    setError('')
    try {
      setOrder(await apiRequest('/checkout', { method: 'POST' }))
      await refreshCart()
      await loadCart()
    } catch (err) {
      setError(err.message)
    }
  }

  if (isLoading) {
    return <p className="text-ink-muted">Loading…</p>
  }
  if (!user) {
    return (
      <div className="rounded-card bg-card p-10 text-center">
        <h1 className="mb-3 text-5xl text-forest">Your cart</h1>
        <p className="mb-6 text-ink-muted">Log in to add products and check out.</p>
        <Link to="/login" state={{ from: '/cart' }} className="btn">Log in</Link>
      </div>
    )
  }

  return (
    <div className="flex flex-col gap-14">
      <h1 className="text-[56px] leading-none text-forest sm:text-[80px]">Your cart</h1>

      {order && (
        <div className="rounded-card border border-forest p-6 text-forest">
          Order #{order.id} placed for {formatPrice(order.total)}.{' '}
          <Link to="/orders" className="underline underline-offset-4">See your orders →</Link>
        </div>
      )}
      {error && <p className="text-danger">{error}</p>}

      {cart.items.length === 0 ? (
        <div className="rounded-card bg-card p-10 text-center">
          <p className="mb-6 text-ink-muted">Your cart is empty.</p>
          <Link to="/" className="btn">Browse the shop</Link>
        </div>
      ) : (
        <section className="grid gap-8 lg:grid-cols-[1fr_340px]">
          <ul className="flex flex-col gap-4">
            {cart.items.map(({ product, quantity, lineTotal }) => (
              <li key={product.id} className="flex flex-wrap items-center gap-5 rounded-card bg-card p-5">
                <ProductImage product={product} className="h-24 w-24 shrink-0" />
                <div className="flex min-w-[160px] flex-1 flex-col gap-1">
                  <Link to={`/product/${product.id}`} className="text-[19px] hover:text-forest">{product.name}</Link>
                  <span className="text-ink-muted">{formatPrice(product.price)} each</span>
                </div>
                <input
                  key={`${quantity}-${cartVersion}`}
                  type="number"
                  min="0"
                  max={product.quantity}
                  defaultValue={quantity}
                  aria-label={`Quantity of ${product.name}`}
                  onBlur={(event) => {
                    if (event.target.value !== String(quantity)) {
                      changeCart(`/cart/${product.id}`, { method: 'PUT', body: { quantity: event.target.value } })
                    }
                  }}
                  className="field w-24"
                />
                <span className="w-24 text-right">{formatPrice(lineTotal)}</span>
                <button
                  type="button"
                  className="label text-danger hover:opacity-70"
                  onClick={() => changeCart(`/cart/${product.id}`, { method: 'DELETE' })}
                >
                  Remove
                </button>
              </li>
            ))}
          </ul>
          <aside className="flex h-fit flex-col gap-5 rounded-card bg-card p-7">
            <span className="label text-forest">Summary</span>
            <div className="flex justify-between text-[19px]">
              <span>Total</span>
              <span>{formatPrice(cart.total)}</span>
            </div>
            <button type="button" className="btn" onClick={handleCheckout}>Check out</button>
            <button
              type="button"
              className="label text-ink-muted hover:text-danger"
              onClick={() => changeCart('/cart', { method: 'DELETE' })}
            >
              Empty cart
            </button>
          </aside>
        </section>
      )}

      {recommendations.length > 0 && (
        <section>
          <SectionLabel title="Goes well with your picks" linkText="Shop all" linkTo="/" />
          <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
            {recommendations.map((product) => <ProductCard key={product.id} product={product} />)}
          </div>
        </section>
      )}
    </div>
  )
}
