/*
 * File:    ProductPage.jsx
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: One product with its details and an add to cart form.
 */
import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { apiRequest } from '../Api.js'
import { useAuth } from '../AuthContext.jsx'
import { formatPrice } from '../Config.js'
import ProductImage from '../components/ProductImage.jsx'
import StatusPill from '../components/StatusPill.jsx'

/**
 * The product detail page.
 *
 * @returns {JSX.Element} the page
 */
export default function ProductPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { user, refreshCart } = useAuth()
  const [product, setProduct] = useState(null)
  const [quantity, setQuantity] = useState('1')
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    apiRequest(`/products/${id}`).then(setProduct).catch((err) => setError(err.message))
  }, [id])

  /**
   * Adds the chosen quantity to the cart, or sends the user to log in.
   *
   * @param {Event} event the form submit event
   */
  async function handleAdd(event) {
    event.preventDefault()
    setMessage('')
    setError('')
    if (!user) {
      navigate('/login', { state: { from: `/product/${id}` } })
      return
    }
    try {
      await apiRequest('/cart', { method: 'POST', body: { productId: product.id, quantity } })
      await refreshCart()
      setMessage(`Added ${quantity} to your cart.`)
    } catch (err) {
      setError(err.message)
    }
  }

  if (!product) {
    return <p className={error ? 'text-danger' : 'text-ink-muted'}>{error || 'Loading…'}</p>
  }

  const isOut = product.status === 'OUT_OF_STOCK' || product.quantity === 0
  return (
    <div className="grid gap-10 lg:grid-cols-2">
      <ProductImage product={product} className="aspect-square w-full rounded-card" />
      <div className="flex flex-col gap-6 lg:py-6">
        <Link to="/" className="label text-forest hover:opacity-70">← Back to shop</Link>
        <span className="label text-forest">{product.category}</span>
        <h1 className="text-[48px] leading-none text-forest sm:text-[64px]">{product.name}</h1>
        <div className="flex items-center gap-4">
          <span className="text-[24px]">{formatPrice(product.price)}</span>
          <StatusPill status={product.status} />
        </div>
        <p className="text-ink-muted">{product.quantity} in stock</p>

        <form onSubmit={handleAdd} className="flex flex-wrap items-center gap-3">
          <label className="label text-ink-muted" htmlFor="quantity">Quantity</label>
          <input
            id="quantity"
            type="number"
            min="1"
            max={product.quantity}
            value={quantity}
            onChange={(event) => setQuantity(event.target.value)}
            className="field w-28"
            disabled={isOut}
          />
          <button type="submit" className="btn" disabled={isOut}>
            {isOut ? 'Out of stock' : 'Add to cart'}
          </button>
        </form>
        {message && (
          <p className="text-forest">
            {message} <Link to="/cart" className="underline underline-offset-4">View cart →</Link>
          </p>
        )}
        {error && <p className="text-danger">{error}</p>}
      </div>
    </div>
  )
}
