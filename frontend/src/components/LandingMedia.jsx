/*
 * File:    LandingMedia.jsx
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: A big picture slot on the landing page: a looping clip (GIF or video) if set, otherwise a photo.
 */
import { useState } from 'react'

/** Folder (inside public/) that landing page clips are served from. */
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
 * Shows a looping clip, falling back to a photo when there is no clip, the
 * clip file is missing, or the visitor prefers less motion. GIFs are shown as
 * images; MP4 and WebM files as muted looping videos.
 *
 * @param {object} props
 * @param {string} [props.clip]      clip file name in public/media/, for example "hero.gif"
 * @param {string} props.image       photo path used as fallback, for example "/images/hero.jpg"
 * @param {string} [props.alt]       description of the picture
 * @param {string} [props.className] extra classes for the frame, for example an aspect ratio
 * @returns {JSX.Element} the framed clip or photo
 */
export default function LandingMedia({ clip, image, alt = '', className = '' }) {
  const [hasClipFailed, setHasClipFailed] = useState(false)
  const useClip = clip && !hasClipFailed && !prefersReducedMotion()
  const media = 'zoom-media h-full w-full object-cover'

  let content
  if (useClip && clip.toLowerCase().endsWith('.gif')) {
    content = <img className={media} src={MEDIA_FOLDER + clip} alt={alt} onError={() => setHasClipFailed(true)} />
  } else if (useClip) {
    content = (
      <video
        className={media}
        src={MEDIA_FOLDER + clip}
        poster={image}
        autoPlay
        muted
        loop
        playsInline
        aria-label={alt}
        onError={() => setHasClipFailed(true)}
      />
    )
  } else {
    content = <img className={media} src={image} alt={alt} loading="lazy" />
  }
  return <div className={`w-full overflow-hidden rounded-[28px] bg-card ${className}`}>{content}</div>
}
