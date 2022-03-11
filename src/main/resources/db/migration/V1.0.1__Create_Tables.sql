DROP TABLE IF EXISTS
    phones,
    addresses,
    photos,
    users,
    orders,
    restaurants,
    reviews,
    reviews_photos,
    items,
    orders_items
CASCADE;

CREATE TABLE phones (
    id SERIAL PRIMARY KEY,
    country_code VARCHAR NOT NULL,
    number VARCHAR NOT NULL
);

CREATE TABLE addresses (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    street VARCHAR,
    building_number VARCHAR NOT NULL,
    city VARCHAR NOT NULL,
    state VARCHAR NOT NULL,
    postcode VARCHAR NOT NULL
);

CREATE TABLE photos (
    id SERIAL PRIMARY KEY,
    encoded VARCHAR NOT NULL
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR UNIQUE NOT NULL,
    email VARCHAR UNIQUE NOT NULL,
    password VARCHAR NOT NULL,
    role VARCHAR NOT NULL,
    blocked BOOLEAN DEFAULT FALSE NOT NULL,
    phone_id INTEGER DEFAULT NULL,
    address_id INTEGER DEFAULT NULL,
    CONSTRAINT FK_UserPhone
        FOREIGN KEY (phone_id)
            REFERENCES phones(id),
    CONSTRAINT FK_UserAddress
        FOREIGN KEY (address_id)
            REFERENCES addresses(id)
);

CREATE TABLE orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    price INTEGER NOT NULL,
    ordered_at TIMESTAMP DEFAULT NOW() NOT NULL,
    delivered_at TIMESTAMP DEFAULT NULL,
    CONSTRAINT FK_UserOrder
        FOREIGN KEY (user_id)
            REFERENCES users(id)
);

CREATE TABLE restaurants (
    id SERIAL PRIMARY KEY,
    manager_id INTEGER NOT NULL,
    name VARCHAR NOT NULL,
    blocked BOOLEAN DEFAULT FALSE NOT NULL,
    description VARCHAR,
    url VARCHAR,
    phone_id INTEGER NOT NULL,
    address_id INTEGER NOT NULL,
    CONSTRAINT FK_RestaurantManager
        FOREIGN KEY (manager_id)
            REFERENCES users(id),
    CONSTRAINT FK_RestaurantPhone
        FOREIGN KEY (phone_id)
            REFERENCES phones(id),
    CONSTRAINT FK_RestaurantAddress
        FOREIGN KEY (address_id)
            REFERENCES addresses(id)
);

CREATE TABLE reviews (
    id SERIAL PRIMARY KEY,
    restaurant_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    score INTEGER NOT NULL,
    text VARCHAR,
    created_at TIMESTAMP DEFAULT NOW() NOT NULL,
    CONSTRAINT FK_ReviewUser
        FOREIGN KEY (user_id)
            REFERENCES users(id),
    CONSTRAINT FK_ReviewRestaurant
        FOREIGN KEY (restaurant_id)
            REFERENCES restaurants(id)
);

CREATE TABLE reviews_photos (
    id SERIAL PRIMARY KEY,
    review_id INTEGER NOT NULL,
    photo_id INTEGER NOT NULL,
    CONSTRAINT FK_ReviewRelation
        FOREIGN KEY (review_id)
            REFERENCES reviews(id),
    CONSTRAINT FK_PhotoRelation
        FOREIGN KEY (photo_id)
            REFERENCES photos(id)
);

CREATE TABLE items (
    id SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    price INTEGER NOT NULL,
    restaurant_id INTEGER NOT NULL,
    photo_id INTEGER DEFAULT NULL,
    CONSTRAINT FK_ItemRestaurant
        FOREIGN KEY (restaurant_id)
            REFERENCES restaurants(id),
    CONSTRAINT FK_ItemPhoto
        FOREIGN KEY (photo_id)
            REFERENCES photos(id)
);

CREATE TABLE orders_items (
    id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    item_id INTEGER NOT NULL,
    CONSTRAINT FK_OrderRelation
        FOREIGN KEY (order_id)
            REFERENCES orders(id),
    CONSTRAINT FK_ItemRelation
        FOREIGN KEY (item_id)
            REFERENCES items(id)
);
