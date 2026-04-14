
INSERT INTO roles (role_name)
SELECT 'USER'
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE role_name = 'USER'
);

INSERT INTO roles (role_name)
SELECT 'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE role_name = 'ADMIN'
);

INSERT INTO roles (role_name)
SELECT 'MANAGER'
WHERE NOT EXISTS (
    SELECT 1 FROM roles WHERE role_name = 'MANAGER'
);

INSERT INTO order_status (status_name)
SELECT 'PLACED'
WHERE NOT EXISTS (
    SELECT 1 FROM order_status WHERE status_name = 'PLACED'
);


INSERT INTO order_status (status_name)
SELECT 'DELIVERED'
WHERE NOT EXISTS (
    SELECT 1 FROM order_status WHERE status_name = 'DELIVERED'
);

INSERT INTO order_status (status_name)
SELECT 'CANCELLED'
WHERE NOT EXISTS (
    SELECT 1 FROM order_status WHERE status_name = 'CANCELLED'
);