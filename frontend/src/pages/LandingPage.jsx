/*
 * File:    LandingPage.jsx
 * Authors: Abdullah Waleed Ahmed, Rehan Haider
 * Purpose: The introduction page: full-width bands that present the Fernhouse collection.
 */
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { apiRequest } from '../Api.js'
import { useAuth } from '../AuthContext.jsx'
import { formatPrice, IMAGE_FOLDER } from '../Config.js'
import LandingMedia from '../components/LandingMedia.jsx'

/** Products shown as large feature cards in "The highlights". */
const HIGHLIGHTS = [
  {
    productId: 30,
    kicker: 'Candles & Scent',
    title: 'A room that smells like a forest walk.',
    text: 'Our reed diffuser releases cedar and moss slowly for months. No flame, no fuss.',
    image: 'reed-diffuser.jpg',
  },
  {
    productId: 10,
    kicker: 'Tableware',
    title: 'Thrown by hand, one at a time.',
    text: 'Every mug carries the marks of the wheel it was made on, so no two are quite alike.',
    image: 'hand-thrown-mug.jpg',
  },
  {
    productId: 22,
    kicker: 'Textiles',
    title: 'Warm without the weight.',
    text: 'Thick waffle-weave cotton that gets softer with every wash.',
    image: 'waffle-cotton-throw.jpg',
  },
]

/** One photo for each category tile. */
const CATEGORY_IMAGES = {
  Kitchen: 'cast-iron-skillet.jpg',
  Tableware: 'speckled-dinner-plate.jpg',
  'Coffee & Tea': 'pour-over-coffee-dripper.jpg',
  Textiles: 'linen-table-runner.jpg',
  'Candles & Scent': 'cedar-and-moss-candle.jpg',
  Storage: 'seagrass-basket.jpg',
  Decor: 'botanical-print.jpg',
  Bath: 'turkish-cotton-towel.jpg',
}

/** Rows of the "A closer look" section. */
const CLOSER_LOOK = [
  { title: 'Natural materials', text: 'Stoneware, linen, oak, rattan, brass and beeswax. Nothing we wouldn’t keep in our own homes.' },
  { title: 'Small workshops', text: 'Each piece comes from a small maker we know by name, in batches rather than by the thousand.' },
  { title: 'Made to last', text: 'Simple shapes and honest finishes that age well instead of going out of style.' },
  { title: 'Free delivery over Rs 3,000', text: 'Orders are packed in recycled paper and arrive in three to five working days.' },
  { title: 'Easy returns', text: 'Changed your mind? Send it back within 30 days for a full refund.' },
]

/**
 * A full-width band with a centred content column.
 *
 * @param {object} props
 * @param {boolean} [props.isTinted] true for the slightly darker band colour
 * @param {string}  [props.id]       anchor ID for links like #categories
 * @param {React.ReactNode} props.children the band content
 * @returns {JSX.Element} the band
 */
function Band({ isTinted = false, id, children }) {
  return (
    <section id={id} className={isTinted ? 'bg-band' : 'bg-paper'}>
      <div className="mx-auto max-w-[1200px] px-4 py-20 sm:px-8 lg:py-28">{children}</div>
    </section>
  )
}

/**
 * A large headline. Wrap the one stressed word in an element with the
 * accent-word class to tint it.
 *
 * @param {object} props
 * @param {React.ReactNode} props.children the headline text
 * @param {string} [props.size] Tailwind text size classes
 * @returns {JSX.Element} the headline
 */
function Headline({ children, size = 'text-[44px] sm:text-[56px] lg:text-[64px]' }) {
  return <h2 className={`${size} leading-[1.04] text-ink`}>{children}</h2>
}

/**
 * The landing page.
 *
 * @returns {JSX.Element} the page
 */
export default function LandingPage() {
  const { user } = useAuth()
  const [stats, setStats] = useState(null)
  const [openRow, setOpenRow] = useState(0)

  useEffect(() => {
    apiRequest('/products')
      .then((list) => {
        if (list.length > 0) {
          setStats({ count: list.length, lowestPrice: Math.min(...list.map((product) => product.price)) })
        }
      })
      .catch(() => setStats(null))
  }, [])

  return (
    <div>
      <Band>
        <div className="flex flex-col gap-7">
          <span className="label text-forest">Fernhouse · Home collection 2026</span>
          <h1 className="max-w-[1000px] text-[56px] leading-[1.0] text-ink sm:text-[84px] lg:text-[104px]">
            Made slowly. <span className="accent-word">Lived</span> in daily.
          </h1>
          <p className="max-w-[560px] text-[19px] leading-[1.45] text-ink-muted">
            Calm, well-made things for the kitchen, the table and the quiet corners of your home.
          </p>
          <div className="flex flex-wrap items-center gap-4">
            {stats && (
              <span className="text-[15px] text-ink">
                From {formatPrice(stats.lowestPrice)} · {stats.count} pieces
              </span>
            )}
            <Link to="/shop" className="btn-primary">Shop the collection</Link>
            <a href="#categories" className="btn-neutral">Browse rooms</a>
          </div>
        </div>
        <LandingMedia
          image={`${IMAGE_FOLDER}hero.jpg`}
          video=""
          alt="A morning table with coffee, a croissant and an open book"
          className="mt-14 aspect-[16/9]"
        />
      </Band>

      <Band isTinted>
        <Headline>The <span className="accent-word">highlights</span>.</Headline>
        <div className="mt-12 flex flex-col gap-6">
          {HIGHLIGHTS.map((item) => (
            <article key={item.productId} className="grid gap-8 rounded-[28px] bg-paper p-5 sm:p-7 lg:grid-cols-[2fr_1fr] lg:items-center">
              <LandingMedia image={IMAGE_FOLDER + item.image} alt={item.title} className="aspect-[4/3]" />
              <div className="flex flex-col gap-3 lg:pr-4">
                <span className="label text-forest">{item.kicker}</span>
                <h3 className="text-[28px] leading-[1.14] text-ink">{item.title}</h3>
                <p className="text-[17px] leading-[1.47] text-ink-muted">{item.text}</p>
                <Link to={`/product/${item.productId}`} className="mt-2 text-[17px] text-forest underline-offset-4 hover:underline">
                  Shop it →
                </Link>
              </div>
            </article>
          ))}
        </div>
      </Band>

      <Band>
        <div className="grid items-center gap-12 lg:grid-cols-2">
          <LandingMedia image={`${IMAGE_FOLDER}gooseneck-kettle.jpg`} video="" alt="A gooseneck kettle on a glass shelf" className="aspect-square" />
          <div className="flex flex-col gap-5">
            <span className="label text-forest">Coffee & Tea</span>
            <Headline>Brewed <span className="accent-word">slowly</span>.</Headline>
            <p className="max-w-[440px] text-[17px] leading-[1.47] text-ink-muted">
              A gooseneck kettle for a steady pour, a ceramic dripper, and a hand grinder for
              mornings that start at their own pace.
            </p>
            <Link to={`/shop?category=${encodeURIComponent('Coffee & Tea')}`} className="text-[17px] text-forest underline-offset-4 hover:underline">
              Shop coffee and tea →
            </Link>
          </div>
        </div>
      </Band>

      <Band isTinted>
        <div className="grid items-center gap-12 lg:grid-cols-2">
          <div className="order-2 flex flex-col gap-5 lg:order-1">
            <span className="label text-forest">Candles & Scent</span>
            <Headline>Evenings, <span className="accent-word">lit low</span>.</Headline>
            <p className="max-w-[440px] text-[17px] leading-[1.47] text-ink-muted">
              Hand-poured candles in stoneware vessels, beeswax tapers and slow-release diffusers.
              Light one and let the day go.
            </p>
            <Link to={`/shop?category=${encodeURIComponent('Candles & Scent')}`} className="text-[17px] text-forest underline-offset-4 hover:underline">
              Shop candles and scent →
            </Link>
          </div>
          <LandingMedia image={`${IMAGE_FOLDER}fig-leaf-candle.jpg`} video="" alt="A lit fig leaf candle" className="order-1 aspect-square lg:order-2" />
        </div>
      </Band>

      <Band>
        <Headline>A closer <span className="accent-word">look</span>.</Headline>
        <div className="mt-12 grid items-start gap-12 lg:grid-cols-[1fr_1.2fr]">
          <ul className="flex flex-col gap-3">
            {CLOSER_LOOK.map((row, index) => {
              const isOpen = openRow === index
              return (
                <li key={row.title}>
                  <button
                    type="button"
                    aria-expanded={isOpen}
                    onClick={() => setOpenRow(isOpen ? -1 : index)}
                    className={`flex w-full items-center gap-3 rounded-full border px-5 py-3 text-left text-[15px] transition-colors ${
                      isOpen ? 'border-forest bg-forest-soft text-forest' : 'border-[#d9d2c3] text-ink hover:border-forest'
                    }`}
                  >
                    <span className="text-[18px] leading-none" aria-hidden="true">{isOpen ? '−' : '+'}</span>
                    {row.title}
                  </button>
                  {isOpen && <p className="px-6 pt-3 text-[17px] leading-[1.47] text-ink-muted">{row.text}</p>}
                </li>
              )
            })}
          </ul>
          <LandingMedia image={`${IMAGE_FOLDER}linen-lampshade.jpg`} alt="A linen lamp on a glass shelf" className="aspect-[4/5]" />
        </div>
      </Band>

      <Band isTinted id="categories">
        <Headline>Every room, <span className="accent-word">considered</span>.</Headline>
        <div className="mt-12 grid grid-cols-2 gap-4 sm:gap-6 lg:grid-cols-4">
          {Object.entries(CATEGORY_IMAGES).map(([name, image]) => (
            <Link
              key={name}
              to={`/shop?category=${encodeURIComponent(name)}`}
              className="group flex flex-col gap-3 rounded-[28px] bg-paper p-4 transition-colors hover:bg-[#fffdf8]"
            >
              <img src={IMAGE_FOLDER + image} alt="" loading="lazy" className="aspect-square w-full rounded-[20px] object-cover" />
              <div className="flex items-center justify-between px-1 pb-1">
                <span className="text-[17px] text-ink">{name}</span>
                <span className="text-forest transition-transform group-hover:translate-x-1" aria-hidden="true">→</span>
              </div>
            </Link>
          ))}
        </div>
      </Band>

      <Band>
        <div className="flex flex-col gap-7">
          <Headline size="text-[56px] sm:text-[84px] lg:text-[96px]">
            Your home, <span className="accent-word">slower</span>.
          </Headline>
          <div className="flex flex-wrap gap-4">
            <Link to="/shop" className="btn-primary">Shop the collection</Link>
            {user ? (
              <Link to="/cart" className="btn-neutral">View your cart</Link>
            ) : (
              <Link to="/login" className="btn-neutral">Create an account</Link>
            )}
          </div>
        </div>
      </Band>
    </div>
  )
}
