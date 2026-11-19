/*
 * File:    ProductImage.jsx
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Shows a product picture, or a lettered placeholder if there is none.
 */
import { useState } from 'react'
import { IMAGE_FOLDER } from '../Config.js'

/**
 * A product picture in a rounded frame.
 *
 * @param {object} props
 * @param {object} props.product   the product (uses name and image)
 * @param {string} [props.className] extra classes, for example a height
 * @returns {JSX.Element} the image or placeholder
 */
export default function ProductImage({ product, className = '' }) {
  const [hasFailed, setHasFailed] = useState(false)
  const frame = `overflow-hidden rounded-[20px] bg-forest-soft ${className}`

  if (!product.image || hasFailed) {
    return (
      <div className={`${frame} flex items-center justify-center`}>
        <span className="font-display text-7xl font-light text-forest/60">
          {product.name.charAt(0).toUpperCase()}
        </span>
      </div>
    )
  }
  const source = product.image.startsWith('http') ? product.image : IMAGE_FOLDER + product.image
  return (
    <div className={frame}>
      <img
        src={source}
        alt={product.name}
        className="h-full w-full object-cover"
        loading="lazy"
        onError={() => setHasFailed(true)}
      />
    </div>
  )
}
