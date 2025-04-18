-- Lisa algsed rollid
INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');

-- Lisa admin kasutaja (parool: admin123)
INSERT INTO users (email, password, first_name, last_name, enabled, created_at, updated_at)
VALUES ('admin@example.com', '$2a$10$mFtGfmK6Rf.PZ3GyrHD7WOvgwFN8ZZQ8jgwTIz8zUAGsW7ZI0lU3O', 'Admin', 'User', true, NOW(), NOW());

-- Lisa admin roll admin kasutajale
INSERT INTO user_roles (user_id, role_id)
VALUES ((SELECT id FROM users WHERE email = 'admin@example.com'), (SELECT id FROM roles WHERE name = 'ROLE_ADMIN'));

-- Lisa peamised tootekategooriad
INSERT INTO categories (name, description, parent_id, created_at, updated_at) VALUES
                                                                                  ('Elektroonika', 'Elektroonikaseadmed ja tarvikud', NULL, NOW(), NOW()),
                                                                                  ('Riided', 'Meeste, naiste ja laste riided', NULL, NOW(), NOW()),
                                                                                  ('Kodu ja aed', 'Kodukaubad, mööbel ja aiatarvikud', NULL, NOW(), NOW()),
                                                                                  ('Sport ja vaba aeg', 'Sporditarvikud ja vaba aja tegevused', NULL, NOW(), NOW());

-- Lisa alamkategooriad
INSERT INTO categories (name, description, parent_id, created_at, updated_at) VALUES
                                                                                  ('Sülearvutid', 'Erinevad sülearvutid', (SELECT id FROM categories WHERE name = 'Elektroonika'), NOW(), NOW()),
                                                                                  ('Nutitelefonid', 'Erinevad nutitelefonid', (SELECT id FROM categories WHERE name = 'Elektroonika'), NOW(), NOW()),
                                                                                  ('Meeste riided', 'Riided meestele', (SELECT id FROM categories WHERE name = 'Riided'), NOW(), NOW()),
                                                                                  ('Naiste riided', 'Riided naistele', (SELECT id FROM categories WHERE name = 'Riided'), NOW(), NOW());

-- Lisa mõned näidistooted
INSERT INTO products (name, description, price, stock_quantity, image_url, category_id, created_at, updated_at) VALUES
                                                                                                                    ('Lenovo ThinkPad X1', 'Võimas äriklassi sülearvuti', 1299.99, 10, 'https://example.com/images/lenovo-x1.jpg', (SELECT id FROM categories WHERE name = 'Sülearvutid'), NOW(), NOW()),
                                                                                                                    ('Samsung Galaxy S21', 'Tipptasemel Android nutitelefon', 899.99, 15, 'https://example.com/images/samsung-s21.jpg', (SELECT id FROM categories WHERE name = 'Nutitelefonid'), NOW(), NOW()),
                                                                                                                    ('Meeste nahktagi', 'Kvaliteetne meeste nahktagi', 199.99, 5, 'https://example.com/images/leather-jacket.jpg', (SELECT id FROM categories WHERE name = 'Meeste riided'), NOW(), NOW()),
                                                                                                                    ('Naiste kleit', 'Elegantne naiste kleit', 89.99, 20, 'https://example.com/images/dress.jpg', (SELECT id FROM categories WHERE name = 'Naiste riided'), NOW(), NOW());