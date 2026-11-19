/*
 * File:    AdminPage.jsx
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Inventory management for admins: add, view, update, delete and set status.
 */
import { useCallback, useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { apiRequest } from '../Api.js'
import { useAuth } from '../AuthContext.jsx'
import { formatPrice } from '../Config.js'
import SectionLabel from '../components/SectionLabel.jsx'
import StatusPill, { STATUS_STYLES } from '../components/StatusPill.jsx'

const EMPTY_FORM = { name: '', category: '', price: '', quantity: '', image: '' }

/**
 * Turns a product into form values for editing.
 *
 * @param {object} product the product
 * @returns {object} the form values as text
 */
function toForm(product) {
  return {
    name: product.name,
    category: product.category,
    price: String(product.price),
    quantity: String(product.quantity),
    image: product.image || '',
  }
}

/**
 * The fields used by both the add form and the edit form.
 *
 * @param {object}   props
 * @param {object}   props.values   current form values
 * @param {Function} props.onChange called with (fieldName, newValue)
 * @returns {JSX.Element} the inputs
 */
function ProductFields({ values, onChange }) {
  const fields = [
    ['name', 'Name'],
    ['category', 'Category'],
    ['price', 'Price'],
    ['quantity', 'Quantity'],
    ['image', 'Image file (optional)'],
  ]
  return fields.map(([key, label]) => (
    <input
      key={key}
      className="field"
      placeholder={label}
      aria-label={label}
      value={values[key]}
      onChange={(event) => onChange(key, event.target.value)}
    />
  ))
}

/**
 * The admin page.
 *
 * @returns {JSX.Element} the page
 */
export default function AdminPage() {
  const { user, isLoading } = useAuth()
  const [products, setProducts] = useState([])
  const [orders, setOrders] = useState([])
  const [newProduct, setNewProduct] = useState(EMPTY_FORM)
  const [editingId, setEditingId] = useState(null)
  const [editValues, setEditValues] = useState(EMPTY_FORM)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  /**
   * Reloads products and orders from the backend.
   */
  const loadData = useCallback(async () => {
    try {
      setProducts(await apiRequest('/products'))
      setOrders(await apiRequest('/admin/orders'))
    } catch (err) {
      setError(err.message)
    }
  }, [])

  useEffect(() => {
    if (user?.admin) {
      loadData()
    }
  }, [user, loadData])

  /**
   * Runs an admin action, shows a message, and reloads the data.
   *
   * @param {Function} action      async function that calls the API
   * @param {string}   successText message shown when it works
   * @returns {Promise<boolean>} true if it worked
   */
  async function runAction(action, successText) {
    setError('')
    setMessage('')
    try {
      await action()
      setMessage(successText)
      await loadData()
      return true
    } catch (err) {
      setError(err.message)
      return false
    }
  }

  /**
   * Sends the add form.
   *
   * @param {Event} event the form submit event
   */
  async function handleAdd(event) {
    event.preventDefault()
    const worked = await runAction(
      () => apiRequest('/admin/products', { method: 'POST', body: newProduct }),
      `Added ${newProduct.name.trim()}.`,
    )
    if (worked) {
      setNewProduct(EMPTY_FORM)
    }
  }

  /**
   * Saves the row being edited.
   *
   * @param {Event} event the form submit event
   */
  async function handleSave(event) {
    event.preventDefault()
    const worked = await runAction(
      () => apiRequest(`/admin/products/${editingId}`, { method: 'PUT', body: editValues }),
      'Changes saved.',
    )
    if (worked) {
      setEditingId(null)
    }
  }

  /**
   * Deletes a product after asking to confirm.
   *
   * @param {object} product the product to delete
   */
  function handleDelete(product) {
    if (window.confirm(`Delete ${product.name}? This can't be undone.`)) {
      runAction(() => apiRequest(`/admin/products/${product.id}`, { method: 'DELETE' }), `Deleted ${product.name}.`)
    }
  }

  /**
   * Sets a product's stock status.
   *
   * @param {object} product the product
   * @param {string} status  IN_STOCK, LOW_STOCK or OUT_OF_STOCK
   */
  function handleStatus(product, status) {
    runAction(
      () => apiRequest(`/admin/products/${product.id}/status`, { method: 'PUT', body: { status } }),
      `${product.name} set to ${STATUS_STYLES[status].label.toLowerCase()}.`,
    )
  }

  if (isLoading) {
    return <p className="text-ink-muted">Loading…</p>
  }
  if (!user?.admin) {
    return <Navigate to="/login" state={{ from: '/admin' }} replace />
  }

  const lowCount = products.filter((product) => product.status !== 'IN_STOCK').length
  return (
    <div className="flex flex-col gap-14">
      <div className="flex flex-wrap items-end justify-between gap-6">
        <h1 className="text-[56px] leading-none text-forest sm:text-[80px]">Inventory</h1>
        <div className="flex gap-8 text-forest">
          <div><div className="label">Products</div><div className="text-3xl">{products.length}</div></div>
          <div><div className="label">Low or out</div><div className="text-3xl">{lowCount}</div></div>
          <div><div className="label">Orders</div><div className="text-3xl">{orders.length}</div></div>
        </div>
      </div>

      {(message || error) && (
        <p role="status" className={`rounded-card border p-4 ${error ? 'border-danger text-danger' : 'border-forest text-forest'}`}>
          {error || message}
        </p>
      )}

      <section>
        <SectionLabel title="Add a product" />
        <form onSubmit={handleAdd} className="grid gap-3 rounded-card bg-card p-6 sm:grid-cols-2 lg:grid-cols-6">
          <ProductFields
            values={newProduct}
            onChange={(key, value) => setNewProduct((current) => ({ ...current, [key]: value }))}
          />
          <button type="submit" className="btn">Add product</button>
        </form>
      </section>

      <section>
        <SectionLabel title="All products (A to Z)" />
        {products.length === 0 ? (
          <div className="rounded-card bg-card p-10 text-center text-ink-muted">
            The inventory is empty. Add your first product above.
          </div>
        ) : (
          <div className="overflow-x-auto rounded-card bg-card p-4">
            <table className="w-full min-w-[860px] text-left text-[15px]">
              <thead>
                <tr className="label text-forest">
                  <th className="p-3">ID</th>
                  <th className="p-3">Name</th>
                  <th className="p-3">Category</th>
                  <th className="p-3">Price</th>
                  <th className="p-3">Qty</th>
                  <th className="p-3">Status</th>
                  <th className="p-3 text-right">Actions</th>
                </tr>
              </thead>
              <tbody>
                {products.map((product) => editingId === product.id ? (
                  <tr key={product.id} className="border-t border-[#d9d2c3]">
                    <td colSpan={7} className="p-3">
                      <form onSubmit={handleSave} className="grid gap-3 sm:grid-cols-2 lg:grid-cols-7">
                        <ProductFields
                          values={editValues}
                          onChange={(key, value) => setEditValues((current) => ({ ...current, [key]: value }))}
                        />
                        <button type="submit" className="btn">Save</button>
                        <button type="button" className="btn" onClick={() => setEditingId(null)}>Cancel</button>
                      </form>
                    </td>
                  </tr>
                ) : (
                  <tr key={product.id} className="border-t border-[#d9d2c3]">
                    <td className="p-3 text-ink-muted">{product.id}</td>
                    <td className="p-3">{product.name}</td>
                    <td className="p-3">{product.category}</td>
                    <td className="p-3">{formatPrice(product.price)}</td>
                    <td className="p-3">{product.quantity}</td>
                    <td className="p-3">
                      <div className="flex items-center gap-2">
                        <StatusPill status={product.status} />
                        <select
                          value={product.status}
                          onChange={(event) => handleStatus(product, event.target.value)}
                          aria-label={`Status of ${product.name}`}
                          className="rounded-full border border-[#cfc8b9] bg-paper px-2 py-1 text-[13px]"
                        >
                          {Object.entries(STATUS_STYLES).map(([key, style]) => (
                            <option key={key} value={key}>{style.label}</option>
                          ))}
                        </select>
                      </div>
                    </td>
                    <td className="p-3 text-right">
                      <button
                        type="button"
                        className="label mr-4 text-forest hover:opacity-70"
                        onClick={() => {
                          setEditingId(product.id)
                          setEditValues(toForm(product))
                        }}
                      >
                        Edit
                      </button>
                      <button type="button" className="label text-danger hover:opacity-70" onClick={() => handleDelete(product)}>
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>

      <section>
        <SectionLabel title="Recent orders" />
        {orders.length === 0 ? (
          <p className="text-ink-muted">No orders yet.</p>
        ) : (
          <ul className="flex flex-col gap-2">
            {orders.map((order) => (
              <li key={order.id} className="flex flex-wrap justify-between gap-2 rounded-card bg-card px-6 py-4">
                <span>#{order.id} · {order.username}</span>
                <span className="text-ink-muted">{order.items.length} items · {order.date}</span>
                <span>{formatPrice(order.total)}</span>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  )
}
