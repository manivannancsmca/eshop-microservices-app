CREATE TABLE products (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    version BIGINT DEFAULT 0,

    sku VARCHAR(50) NOT NULL UNIQUE,

    name VARCHAR(200) NOT NULL,

    description TEXT,

    brand_id BIGINT NOT NULL,

    category_id BIGINT NOT NULL,

    price DECIMAL(12,2) NOT NULL,

    stock_count INT NOT NULL DEFAULT 0,

    status VARCHAR(30) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_by VARCHAR(100),

    updated_by VARCHAR(100),

    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_product_brand
        FOREIGN KEY (brand_id)
        REFERENCES brands(id),

    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
);