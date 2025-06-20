CREATE SCHEMA IF NOT EXISTS users;

CREATE TABLE IF NOT EXISTS users.roles (
    role_id INT PRIMARY KEY,
    role_name VARCHAR(50),
    role_desc VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS users.user_details (
    user_id INT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(50),
    email_id VARCHAR(50),
    created_on DATE,
    last_login DATE,
    deleted_b INT,
    is_mfa_required BOOLEAN
);


CREATE TABLE IF NOT EXISTS users.user_roles
(
    user_role_key INT PRIMARY KEY,
    user_id_fk INT,
    role_id_fk INT
);

CREATE TABLE IF NOT EXISTS users.user_mfa_details (
    id INT PRIMARY KEY,
    user_id INT,
    mfa_secret VARCHAR(50),
    ip_address VARCHAR(50),
    is_trusted BOOLEAN,
    ts_token_expiration TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    finger_print_info VARCHAR(50),
    register_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
