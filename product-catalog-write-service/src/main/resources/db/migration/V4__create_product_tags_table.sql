CREATE TABLE product_tags (

    product_id BIGINT NOT NULL,

    tag VARCHAR(100) NOT NULL,

    PRIMARY KEY(product_id, tag),

    CONSTRAINT fk_product_tag_product
        FOREIGN KEY(product_id)
        REFERENCES products(id)
        ON DELETE CASCADE
);