INSERT INTO tb_roles (role_name)
SELECT 'USER'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_roles WHERE role_name = 'USER'
);

INSERT INTO tb_roles (role_name)
SELECT 'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_roles WHERE role_name = 'ADMIN'
);

INSERT INTO tb_roles (role_name)
SELECT 'MANAGER'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_roles WHERE role_name = 'MANAGER'
);


INSERT INTO tb_order_status (status_name)
SELECT 'PLACED'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_order_status WHERE status_name = 'PLACED'
);

INSERT INTO tb_order_status (status_name)
SELECT 'CANCELLED'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_order_status WHERE status_name = 'CANCELLED'
);

INSERT INTO tb_order_status (status_name)
SELECT 'DELIVERED'
WHERE NOT EXISTS (
    SELECT 1 FROM tb_order_status WHERE status_name = 'DELIVERED'
);