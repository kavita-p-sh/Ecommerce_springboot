CREATE TABLE IF NOT EXISTS tb_roles (
    role_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS tb_users (
    user_id CHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role_id BIGINT NOT NULL,
    phone_number VARCHAR(20) NOT NULL UNIQUE,

    created_by VARCHAR(50),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_tb_users_roles
        FOREIGN KEY (role_id) REFERENCES tb_roles(role_id)
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS tb_products (
    product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,

    created_by VARCHAR(50),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tb_order_status (
    status_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS tb_orders (
    order_id CHAR(36) PRIMARY KEY,
    user_id CHAR(36) NOT NULL,
    status_id BIGINT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    total_quantity INT NOT NULL,

    created_by VARCHAR(50),
    created_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_tb_orders_users
        FOREIGN KEY (user_id) REFERENCES tb_users(user_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_tb_orders_status
        FOREIGN KEY (status_id) REFERENCES tb_order_status(status_id)
        ON DELETE RESTRICT

        );
  CREATE TABLE IF NOT EXISTS tb_order_items (
       order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
       order_id CHAR(36) NOT NULL ,
       product_id BIGINT NOT NULL,
       quantity INT NOT NULL,

       CONSTRAINT fk_tb_order_items_orders
         FOREIGN KEY (order_id) REFERENCES tb_orders(order_id),

        CONSTRAINT fk_tb_order_items_products
          FOREIGN KEY (product_id) REFERENCES tb_products(product_id)
  );

CREATE INDEX idx_tb_orders_user ON tb_orders(user_id);
CREATE INDEX idx_tb_orders_status ON tb_orders(status_id);
CREATE INDEX idx_tb_orders_created_by ON tb_orders(created_by);
CREATE INDEX idx_tb_products_price ON tb_products(price);
CREATE INDEX idx_tb_products_quantity ON tb_products(quantity);