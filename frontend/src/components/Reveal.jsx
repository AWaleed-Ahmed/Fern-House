/*
 * File:    Reveal.jsx
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Fades and slides its content in when it scrolls into view.
 */
import { useEffect, useRef, useState } from 'react'

/** How much of the element must be on screen before it animates (0 to 1). */
const VISIBLE_THRESHOLD = 0.15

/**
 * Wraps content that should animate in once, the first time it is seen.
 * Uses IntersectionObserver; if the browser lacks it, content shows at once.
 *
 * @param {object} props
 * @param {React.ReactNode} props.children the content to reveal
 * @param {number} [props.delay]     milliseconds to wait before animating, for staggering
 * @param {string} [props.className] extra classes for the wrapper
 * @returns {JSX.Element} the wrapper
 */
export default function Reveal({ children, delay = 0, className = '' }) {
  const elementRef = useRef(null)
  const [isVisible, setIsVisible] = useState(false)

  useEffect(() => {
    const element = elementRef.current
    if (!element || typeof IntersectionObserver === 'undefined') {
      setIsVisible(true)
      return undefined
    }
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          setIsVisible(true)
          observer.disconnect()
        }
      },
      { threshold: VISIBLE_THRESHOLD, rootMargin: '0px 0px -8% 0px' },
    )
    observer.observe(element)
    return () => observer.disconnect()
  }, [])

  return (
    <div
      ref={elementRef}
      className={`reveal ${isVisible ? 'is-visible' : ''} ${className}`}
      style={{ transitionDelay: `${delay}ms` }}
    >
      {children}
    </div>
  )
}
