# OEL Store (Fernhouse)

Inventory management system and online store for the Software Construction OEL.

Authors: Abdullah Waleed Ahmed, Rehan Haider

- **Backend:** Java 17, the JDK's built-in `HttpServer`, data saved in JSON files (Gson)
- **Frontend:** React + Tailwind CSS (JavaScript), built with Vite
- **Tests:** JUnit 5 (standalone jar in `backend/lib`)
- **IDE:** VS Code with the JDK (settings in `.vscode/settings.json`)

## Data structures

| Structure | Class | Used for |
|-----------|-------|----------|
| Trie (prefix tree), children in a `TreeMap` | `store.ds.ProductTrie`, `TrieNode` | Search by name; every product starting with "a" sits under the "a" node; results come out A to Z |
| Hash table (`HashMap`) | `store.ds.ProductCatalog`, `store.service.UserService` | Product lookup by ID, user lookup by username, session tokens |
| Linked list (`LinkedList`) | `store.model.User`, `store.service.CartService` | Each user's cart |
| Weighted graph (adjacency map) | `store.ds.RecommendationGraph` | Products bought together; recommendations per user |

## Running it

Backend (from `backend/`):

```bash
./run.sh
```

The API runs at http://localhost:8080/api. On first run the files in `backend/seed/` are copied to `backend/data/`, which holds the live data.

Frontend (from `frontend/`):

```bash
npm install
npm run dev
```

Open http://localhost:5173.

## Admin account

The seed file `backend/seed/users.json` has one admin account, username `admin` and password `admin123`. Any user becomes an admin when `"admin": true` is set for them in `backend/data/users.json`. Change this password before using the store for real.

## Tests and documentation

From `backend/`:

```bash
./test.sh
```

```bash
./docs.sh
```

`docs.sh` writes the Javadoc HTML to `backend/docs/`. Open `backend/docs/index.html` in a browser.

## API

| Method | Path | Who | What |
|--------|------|-----|------|
| POST | `/api/auth/register`, `/api/auth/login`, `/api/auth/logout` | anyone | Accounts |
| GET | `/api/auth/me` | user | Current user |
| GET | `/api/products?search=ap` or `?category=X` | anyone | List or search products |
| GET | `/api/products/{id}`, `/api/categories` | anyone | One product, all categories |
| GET, POST, DELETE | `/api/cart` | user | View, add to, or empty the cart |
| PUT, DELETE | `/api/cart/{productId}` | user | Change quantity or remove an item |
| POST | `/api/checkout` | user | Buy the cart (reduces stock) |
| GET | `/api/orders`, `/api/recommendations` | user | Order history, suggestions |
| POST | `/api/admin/products` | admin | Add a product |
| PUT, DELETE | `/api/admin/products/{id}` | admin | Update or delete a product |
| PUT | `/api/admin/products/{id}/status` | admin | Set In Stock, Low Stock or Out of Stock |
| GET | `/api/admin/orders` | admin | All orders |
