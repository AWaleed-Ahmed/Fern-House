/*
 * File:    Api.js
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Small wrapper around fetch for calling the Java backend.
 */

const TOKEN_KEY = 'storeToken'

/**
 * Reads the saved login token from the browser.
 *
 * @returns {string|null} the token, or null if not logged in
 */
export function getToken() {
  try {
    return localStorage.getItem(TOKEN_KEY)
  } catch {
    return null
  }
}

/**
 * Saves or clears the login token in the browser.
 *
 * @param {string|null} token the token to save, or null to log out
 */
export function setToken(token) {
  try {
    if (token) {
      localStorage.setItem(TOKEN_KEY, token)
    } else {
      localStorage.removeItem(TOKEN_KEY)
    }
  } catch {
    // Storage can be blocked (private mode); the app still works for this tab.
  }
}

/**
 * Calls the API and returns the JSON reply. Throws an Error with the
 * server's message when the request fails.
 *
 * @param {string} path   path after /api, for example "/products"
 * @param {object} [options]
 * @param {string} [options.method] HTTP method, default GET
 * @param {object} [options.body]   object sent as JSON
 * @returns {Promise<any>} the parsed reply
 */
export async function apiRequest(path, { method = 'GET', body } = {}) {
  const headers = {}
  const token = getToken()
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }
  if (body !== undefined) {
    headers['Content-Type'] = 'application/json'
  }

  let response
  try {
    response = await fetch(`/api${path}`, {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
    })
  } catch {
    throw new Error("Couldn't reach the server. Check that the backend is running.")
  }

  const data = await response.json().catch(() => ({}))
  if (!response.ok) {
    const error = new Error(data.error || 'Something went wrong.')
    error.status = response.status
    throw error
  }
  return data
}
