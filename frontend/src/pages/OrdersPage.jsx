/*
 * File:    OrdersPage.jsx
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Lists the logged-in user's past orders.
 */
import { useEffect, useState } from 'react'
import { Link, Navigate } from 'react-router-dom'
import { apiRequest } from '../Api.js'
import { useAuth } from '../AuthContext.jsx'
import { formatPrice } from '../Config.js'

/**
 * The order history page.
 *
 * @returns {JSX.Element} the page
 */
export default function OrdersPage() {
  const { user, isLoading } = useAuth()
  const [orders, setOrders] = useState([])
  const [products, setProducts] = useState({})
  const [error, setError] = useState('')

  useEffect(() => {
    if (!user) {
      return
    }
    apiRequest('/orders').then(setOrders).catch((err) => setError(err.message))
    apiRequest('/products')
      .then((list) => setProducts(Object.fromEntries(list.map((product) => [product.id, product]))))
      .catch(() => setProducts({}))
  }, [user])

  if (isLoading) {
    return <p className="text-ink-muted">Loading…</p>
  }
  if (!user) {
    return <Navigate to="/login" state={{ from: '/orders' }} replace />
  }

  return (
    <div className="flex flex-col gap-10">
      <h1 className="text-[56px] leading-none text-forest sm:text-[80px]">Your orders</h1>
      {error && <p className="text-danger">{error}</p>}
      {orders.length === 0 && !error && (
        <div className="rounded-card bg-card p-10 text-center">
          <p className="mb-6 text-ink-muted">You haven't ordered anything yet.</p>
          <Link to="/shop" className="btn">Browse the shop</Link>
        </div>
      )}
      <ul className="flex flex-col gap-4">
        {orders.map((order) => (
          <li key={order.id} className="rounded-card bg-card p-6">
            <div className="mb-3 flex flex-wrap justify-between gap-2 border-b border-forest pb-2 text-forest">
              <span className="label">Order #{order.id} · {order.date}</span>
              <span className="label">{formatPrice(order.total)}</span>
            </div>
            <ul className="flex flex-col gap-1">
              {order.items.map((item) => (
                <li key={item.productId} className="flex justify-between">
                  <span>{products[item.productId]?.name || `Product #${item.productId} (removed)`}</span>
                  <span className="text-ink-muted">× {item.quantity}</span>
                </li>
              ))}
            </ul>
          </li>
        ))}
      </ul>
    </div>
  )
}
