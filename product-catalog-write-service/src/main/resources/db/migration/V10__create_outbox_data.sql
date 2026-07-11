INSERT INTO outbox (id, aggregate_type, aggregate_id, event_type, payload, created_date)
VALUES
(
    UUID_TO_BIN(UUID()),
    'product',
    UUID(),
    'PRODUCT_CREATED',
    CAST('{"productId":"P1001","name":"iPhone 16 Pro","brand":"Apple","category":"Mobile","price":129999}' AS BINARY),
    NOW()
),
(
    UUID_TO_BIN(UUID()),
    'product',
    UUID(),
    'PRODUCT_CREATED',
    CAST('{"productId":"P1002","name":"Galaxy S25 Ultra","brand":"Samsung","category":"Mobile","price":119999}' AS BINARY),
    NOW()
),
(
    UUID_TO_BIN(UUID()),
    'product',
    UUID(),
    'PRODUCT_CREATED',
    CAST('{"productId":"P1003","name":"Pixel 10 Pro","brand":"Google","category":"Mobile","price":99999}' AS BINARY),
    NOW()
),
(
    UUID_TO_BIN(UUID()),
    'product',
    UUID(),
    'PRODUCT_UPDATED',
    CAST('{"productId":"P1001","price":124999,"stock":25}' AS BINARY),
    NOW()
),
(
    UUID_TO_BIN(UUID()),
    'product',
    UUID(),
    'PRODUCT_UPDATED',
    CAST('{"productId":"P1002","price":114999,"stock":40}' AS BINARY),
    NOW()
),
(
    UUID_TO_BIN(UUID()),
    'product',
    UUID(),
    'PRODUCT_CREATED',
    CAST('{"productId":"P1004","name":"MacBook Pro M4","brand":"Apple","category":"Laptop","price":229999}' AS BINARY),
    NOW()
),
(
    UUID_TO_BIN(UUID()),
    'product',
    UUID(),
    'PRODUCT_CREATED',
    CAST('{"productId":"P1005","name":"Dell XPS 15","brand":"Dell","category":"Laptop","price":189999}' AS BINARY),
    NOW()
),
(
    UUID_TO_BIN(UUID()),
    'product',
    UUID(),
    'PRODUCT_DELETED',
    CAST('{"productId":"P1003","reason":"Discontinued"}' AS BINARY),
    NOW()
),
(
    UUID_TO_BIN(UUID()),
    'product',
    UUID(),
    'PRODUCT_UPDATED',
    CAST('{"productId":"P1004","price":219999,"stock":12}' AS BINARY),
    NOW()
),
VALUES (
    UUID_TO_BIN(UUID()),
    'product',
    UUID(),
    'PRODUCT_CREATED',
    CAST('{"productId":"P1001","name":"iPhone 16 Pro","brand":"Apple","price":129999
    }' AS BINARY),
    NOW()
)