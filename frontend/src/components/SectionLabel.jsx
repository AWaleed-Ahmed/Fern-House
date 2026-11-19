/*
 * File:    SectionLabel.jsx
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Magazine-style running head: small label, hairline rule, optional arrow link.
 */
import { Link } from 'react-router-dom'

/**
 * A section heading with a thin rule under it.
 *
 * @param {object} props
 * @param {string} props.title      the label text
 * @param {string} [props.linkText] text of the arrow link on the right
 * @param {string} [props.linkTo]   where the arrow link goes
 * @returns {JSX.Element} the section label
 */
export default function SectionLabel({ title, linkText, linkTo }) {
  return (
    <div className="mb-6 flex items-end justify-between border-b border-forest pb-2 text-forest">
      <span className="label">{title}</span>
      {linkText && linkTo && (
        <Link to={linkTo} className="label hover:opacity-70">
          {linkText} →
        </Link>
      )}
    </div>
  )
}
