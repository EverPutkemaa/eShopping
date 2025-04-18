-- Rollide tabel
CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE
);

-- Kasutajate tabel
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       first_name VARCHAR(100),
                       last_name VARCHAR(100),
                       enabled BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL
);

-- Kasutaja-rollide suhetabel
CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                            FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);

-- Kategooriate tabel
CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            description TEXT,
                            parent_id BIGINT,
                            created_at TIMESTAMP NOT NULL,
                            updated_at TIMESTAMP NOT NULL,
                            FOREIGN KEY (parent_id) REFERENCES categories (id) ON DELETE SET NULL
);

-- Toodete tabel
CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          price DECIMAL(10, 2) NOT NULL,
                          stock_quantity INTEGER NOT NULL DEFAULT 0,
                          image_url VARCHAR(255),
                          category_id BIGINT,
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP NOT NULL,
                          FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL
);

-- Ostukorvide tabel
CREATE TABLE carts (
                       id BIGSERIAL PRIMARY KEY,
                       user_id BIGINT NOT NULL UNIQUE,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL,
                       FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Ostukorvi esemete tabel
CREATE TABLE cart_items (
                            id BIGSERIAL PRIMARY KEY,
                            cart_id BIGINT NOT NULL,
                            product_id BIGINT NOT NULL,
                            quantity INTEGER NOT NULL,
                            FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE,
                            FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
);

-- Tellimuste tabel
CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        order_number VARCHAR(100) NOT NULL UNIQUE,
                        user_id BIGINT NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        total_price DECIMAL(10, 2) NOT NULL,
                        shipping_method VARCHAR(50),
                        payment_method VARCHAR(50),
                        payment_status VARCHAR(50),
                        street_address VARCHAR(255),
                        city VARCHAR(100),
                        state VARCHAR(100),
                        postal_code VARCHAR(20),
                        country VARCHAR(100),
                        phone_number VARCHAR(20),
                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL,
                        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Tellimuse esemete tabel
CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL,
                             product_id BIGINT NOT NULL,
                             quantity INTEGER NOT NULL,
                             price DECIMAL(10, 2) NOT NULL,
                             FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
                             FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE SET NULL
);