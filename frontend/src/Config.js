/*
 * File:    Config.js
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Store-wide settings in one place (name, tagline, currency).
 */

/** Name shown in the logo and page titles. */
export const STORE_NAME = 'Fernhouse'

/** Line shown in the top announcement bar. */
export const STORE_TAGLINE = 'Free delivery on orders over Rs 3,000'

/** Currency label put before every price. */
export const CURRENCY = 'Rs'

/** Folder (inside public/) that product images are served from. */
export const IMAGE_FOLDER = '/images/'

/**
 * Formats a price for display, for example 1250 to "Rs 1,250".
 *
 * @param {number} amount the price
 * @returns {string} the formatted price
 */
export function formatPrice(amount) {
  return `${CURRENCY} ${Math.round(amount).toLocaleString('en-US')}`
}
