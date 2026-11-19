/*
 * File:    HomePage.jsx
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: Shop front: hero, live name search (trie on the backend), categories and product grid.
 */
import { useEffect, useState } from 'react'
import { apiRequest } from '../Api.js'
import { useAuth } from '../AuthContext.jsx'
import { STORE_NAME } from '../Config.js'
import ProductCard from '../components/ProductCard.jsx'
import SectionLabel from '../components/SectionLabel.jsx'

const SEARCH_DELAY_MS = 200
const HERO_IMAGE = '/images/hero.jpg'

/**
 * The shop page.
 *
 * @returns {JSX.Element} the page
 */
export default function HomePage() {
  const { user } = useAuth()
  const [products, setProducts] = useState([])
  const [categories, setCategories] = useState([])
  const [recommendations, setRecommendations] = useState([])
  const [search, setSearch] = useState('')
  const [category, setCategory] = useState('')
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState('')
  const [hasHeroImage, setHasHeroImage] = useState(true)

  useEffect(() => {
    apiRequest('/categories').then(setCategories).catch(() => setCategories([]))
  }, [])

  useEffect(() => {
    if (!user) {
      setRecommendations([])
      return
    }
    apiRequest('/recommendations').then(setRecommendations).catch(() => setRecommendations([]))
  }, [user])

  useEffect(() => {
    const timer = setTimeout(() => {
      const query = search.trim()
        ? `?search=${encodeURIComponent(search.trim())}`
        : category ? `?category=${encodeURIComponent(category)}` : ''
      apiRequest(`/products${query}`)
        .then((list) => {
          setProducts(list)
          setError('')
        })
        .catch((err) => setError(err.message))
        .finally(() => setIsLoading(false))
    }, SEARCH_DELAY_MS)
    return () => clearTimeout(timer)
  }, [search, category])

  /**
   * Picks a category, or clears it when the same one is picked again.
   *
   * @param {string} name the category name
   */
  function chooseCategory(name) {
    setSearch('')
    setCategory((current) => (current === name ? '' : name))
  }

  return (
    <div className="flex flex-col gap-16">
      <section className="grid items-center gap-10 lg:grid-cols-2">
        <div className="flex flex-col gap-6">
          <span className="label text-forest">{STORE_NAME} · Est. 2026</span>
          <h1 className="text-[56px] leading-[0.99] text-forest sm:text-[80px] xl:text-[104px]">
            Good things for slow mornings
          </h1>
          <p className="max-w-[480px] text-[19px] leading-[1.35] tracking-[0.017em] text-ink">
            A small, careful selection for the kitchen and the table. Search by name,
            browse by category, and we'll suggest what goes well with what you've bought.
          </p>
        </div>
        {hasHeroImage ? (
          <img
            src={HERO_IMAGE}
            alt=""
            onError={() => setHasHeroImage(false)}
            className="aspect-[4/3] w-full rounded-card object-cover"
          />
        ) : (
          <div className="aspect-[4/3] w-full rounded-card bg-forest-soft" />
        )}
      </section>

      {recommendations.length > 0 && (
        <section>
          <SectionLabel title={`Picked for ${user.username}`} />
          <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
            {recommendations.map((product) => <ProductCard key={product.id} product={product} />)}
          </div>
        </section>
      )}

      <section>
        <SectionLabel title="All products" />
        <div className="mb-8 flex flex-col gap-4">
          <input
            type="search"
            value={search}
            onChange={(event) => {
              setSearch(event.target.value)
              setCategory('')
            }}
            placeholder="Search by name, for example “ap”"
            className="field max-w-[520px]"
            aria-label="Search products by name"
          />
          <div className="flex flex-wrap gap-2">
            {categories.map((name) => (
              <button
                key={name}
                type="button"
                onClick={() => chooseCategory(name)}
                className={`rounded-full border px-4 py-1.5 text-[14px] transition-colors ${
                  category === name
                    ? 'border-forest bg-forest-soft text-forest'
                    : 'border-[#cfc8b9] text-ink hover:border-forest hover:text-forest'
                }`}
              >
                {name}
              </button>
            ))}
          </div>
        </div>

        {error && <p className="text-danger">{error}</p>}
        {!error && !isLoading && products.length === 0 && (
          <div className="rounded-card bg-card p-10 text-center">
            <h2 className="mb-2 text-4xl text-forest">
              {search ? 'No matches' : 'The shelves are empty'}
            </h2>
            <p className="text-ink-muted">
              {search
                ? `No product name starts with “${search}”. Try fewer letters.`
                : 'There are no products in the inventory yet.'}
            </p>
          </div>
        )}
        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
          {products.map((product) => <ProductCard key={product.id} product={product} />)}
        </div>
      </section>
    </div>
  )
}
