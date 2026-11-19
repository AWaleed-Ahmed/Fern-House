/*
 * File:    Main.jsx
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Entry point: loads the theme and renders the app.
 */
import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './Theme.css'
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
