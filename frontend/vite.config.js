/*
 * File:    vite.config.js
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Vite setup: React, Tailwind, and forwarding /api to the Java backend.
 */
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

const BACKEND_URL = 'http://localhost:8080'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    port: 5173,
    proxy: {
      '/api': BACKEND_URL,
    },
  },
})
