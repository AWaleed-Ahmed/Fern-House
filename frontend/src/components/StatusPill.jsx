/*
 * File:    StatusPill.jsx
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Small outlined label for In Stock, Low Stock or Out of Stock.
 */

/** Text and colour for each stock status sent by the backend. */
export const STATUS_STYLES = {
  IN_STOCK: { label: 'In stock', classes: 'border-forest text-forest' },
  LOW_STOCK: { label: 'Low stock', classes: 'border-amber text-amber' },
  OUT_OF_STOCK: { label: 'Out of stock', classes: 'border-ink-muted text-ink-muted line-through' },
}

/**
 * An outlined pill showing a stock status.
 *
 * @param {object} props
 * @param {string} props.status IN_STOCK, LOW_STOCK or OUT_OF_STOCK
 * @returns {JSX.Element} the pill
 */
export default function StatusPill({ status }) {
  const style = STATUS_STYLES[status] || STATUS_STYLES.IN_STOCK
  return (
    <span className={`label inline-block rounded-full border px-2.5 py-0.5 ${style.classes}`}>
      {style.label}
    </span>
  )
}
