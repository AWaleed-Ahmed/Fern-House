/*
 * File:    LandingMedia.jsx
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: A big picture slot on the landing page: a looping video if one is set, otherwise a photo.
 */
import { useState } from 'react'

/** Folder (inside public/) that landing page videos are served from. */
export const MEDIA_FOLDER = '/media/'

/**
 * Checks if the visitor asked their system for less motion.
 *
 * @returns {boolean} true when motion should be avoided
 */
function prefersReducedMotion() {
  try {
    return window.matchMedia('(prefers-reduced-motion: reduce)').matches
  } catch {
    return false
  }
}

/**
 * Shows a muted looping video, falling back to a photo when there is no
 * video, the video fails to load, or the visitor prefers less motion.
 *
 * @param {object} props
 * @param {string} [props.video]     video file name in public/media/, for example "hero.mp4"
 * @param {string} props.image       photo path used as poster and fallback, for example "/images/hero.jpg"
 * @param {string} [props.alt]       description of the picture
 * @param {string} [props.className] extra classes, for example an aspect ratio
 * @returns {JSX.Element} the video or image
 */
export default function LandingMedia({ video, image, alt = '', className = '' }) {
  const [hasVideoFailed, setHasVideoFailed] = useState(false)
  const frame = `w-full overflow-hidden rounded-[28px] bg-card object-cover ${className}`

  if (video && !hasVideoFailed && !prefersReducedMotion()) {
    return (
      <video
        className={frame}
        src={MEDIA_FOLDER + video}
        poster={image}
        autoPlay
        muted
        loop
        playsInline
        aria-label={alt}
        onError={() => setHasVideoFailed(true)}
      />
    )
  }
  return <img className={frame} src={image} alt={alt} loading="lazy" />
}
