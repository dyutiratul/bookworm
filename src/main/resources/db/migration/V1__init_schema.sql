-- V1__init_schema.sql
-- BookWorm initial schema

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Users
CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name    VARCHAR(100),
    last_name     VARCHAR(100),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Categories
CREATE TABLE categories (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug         VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL
);

INSERT INTO categories (slug, display_name) VALUES
    ('all',                    'All'),
    ('romance',                'Romance'),
    ('mystery',                'Mystery'),
    ('science-fiction',        'Science Fiction'),
    ('fantasy',                'Fantasy'),
    ('historical',             'Historical'),
    ('biography',              'Biography'),
    ('self-help',              'Self-help'),
    ('memoir',                 'Memoir'),
    ('travel',                 'Travel'),
    ('cooking',                'Cooking'),
    ('childrens',              'Children''s'),
    ('young-adult',            'Young Adult'),
    ('comics-graphic-novels',  'Comics & Graphic Novels'),
    ('poetry',                 'Poetry'),
    ('drama',                  'Drama'),
    ('science',                'Science'),
    ('philosophy',             'Philosophy'),
    ('religion',               'Religion'),
    ('language-learning',      'Language Learning'),
    ('non-fiction',            'Non-fiction');

-- Genres
CREATE TABLE genres (
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE
);

INSERT INTO genres (name) VALUES
    ('Fiction'),('Non-fiction'),('Self Help'),('Thriller'),('Horror'),
    ('Love'),('Drama'),('Children'),('Fantasy'),('Science Fiction');

-- Books
CREATE TABLE books (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug               VARCHAR(200) NOT NULL UNIQUE,
    title              VARCHAR(300) NOT NULL,
    author             VARCHAR(200) NOT NULL,
    cover_image_url    VARCHAR(500),
    description        TEXT,
    back_cover_summary TEXT,
    publisher          VARCHAR(100),
    isbn               VARCHAR(50),
    language           VARCHAR(100),
    format             VARCHAR(20) NOT NULL CHECK (format IN ('PAPERBACK','HARDCOVER','EBOOK')),
    price              NUMERIC(10,2) NOT NULL,
    rating_average     NUMERIC(3,2),
    rating_count       INTEGER DEFAULT 0,
    total_sold         INTEGER DEFAULT 0,
    author_bio         TEXT,
    category_id        UUID REFERENCES categories(id),
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Book-Genre join table
CREATE TABLE book_genres (
    book_id  UUID NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    genre_id UUID NOT NULL REFERENCES genres(id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, genre_id)
);

-- Carts (one per user)
CREATE TABLE carts (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Cart items
CREATE TABLE cart_items (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cart_id    UUID NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    book_id    UUID NOT NULL REFERENCES books(id),
    quantity   INTEGER NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (cart_id, book_id)
);

-- Wishlist items
CREATE TABLE wishlist_items (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    book_id    UUID NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, book_id)
);

-- Addresses
CREATE TABLE addresses (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id            UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    first_name         VARCHAR(100) NOT NULL,
    last_name          VARCHAR(100) NOT NULL,
    address_line       VARCHAR(300) NOT NULL,
    city               VARCHAR(100) NOT NULL,
    state              VARCHAR(100) NOT NULL,
    pin                VARCHAR(20)  NOT NULL,
    country            VARCHAR(100) NOT NULL,
    email              VARCHAR(150) NOT NULL,
    phone_country_code VARCHAR(10),
    phone_number       VARCHAR(20)  NOT NULL,
    is_saved           BOOLEAN DEFAULT FALSE,
    created_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Orders
CREATE TABLE orders (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id             UUID NOT NULL REFERENCES users(id),
    status              VARCHAR(30) NOT NULL DEFAULT 'PLACED'
                            CHECK (status IN ('PLACED','PROCESSING','SHIPPED','DELIVERED','CANCELLED')),
    total_amount        NUMERIC(10,2) NOT NULL,
    currency            VARCHAR(10) NOT NULL DEFAULT 'INR',
    delivery_address_id UUID REFERENCES addresses(id),
    payment_method      VARCHAR(30),
    payment_id          VARCHAR(100),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Order items
CREATE TABLE order_items (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id   UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    book_id    UUID NOT NULL REFERENCES books(id),
    quantity   INTEGER NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(10,2) NOT NULL
);

-- Payments
CREATE TABLE payments (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id        UUID NOT NULL REFERENCES users(id),
    order_id       UUID REFERENCES orders(id),
    status         VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                       CHECK (status IN ('PENDING','PROCESSING','SUCCESS','FAILED')),
    payment_method VARCHAR(20) NOT NULL
                       CHECK (payment_method IN ('CREDIT_CARD','DEBIT_CARD','UPI','WALLET')),
    payable_amount NUMERIC(10,2) NOT NULL,
    currency       VARCHAR(10) NOT NULL DEFAULT 'INR',
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Reviews
CREATE TABLE reviews (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    book_id    UUID NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    rating     INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    body       TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_books_category   ON books(category_id);
CREATE INDEX idx_books_format     ON books(format);
CREATE INDEX idx_orders_user      ON orders(user_id);
CREATE INDEX idx_cart_items_cart  ON cart_items(cart_id);
CREATE INDEX idx_reviews_book     ON reviews(book_id);
