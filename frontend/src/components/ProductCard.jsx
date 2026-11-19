/*
 * File:    ProductCard.jsx
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Product tile used in the shop grid and recommendations.
 */
import { Link } from 'react-router-dom'
import { formatPrice } from '../Config.js'
import ProductImage from './ProductImage.jsx'
import StatusPill from './StatusPill.jsx'

/**
 * A product tile linking to the product page.
 *
 * @param {object} props
 * @param {object} props.product the product to show
 * @returns {JSX.Element} the card
 */
export default function ProductCard({ product }) {
  return (
    <Link
      to={`/product/${product.id}`}
      className="group flex flex-col gap-4 rounded-card bg-card p-5 transition-colors hover:bg-[#e8e2d5]"
    >
      <ProductImage product={product} className="aspect-square w-full" />
      <div className="flex flex-col gap-1.5">
        <span className="label text-forest">{product.category}</span>
        <span className="text-[19px] leading-snug text-ink">{product.name}</span>
        <div className="mt-1 flex items-center justify-between gap-2">
          <span className="text-[15px] text-ink">{formatPrice(product.price)}</span>
          <StatusPill status={product.status} />
        </div>
      </div>
    </Link>
  )
}
