 INSERT INTO users.roles(
    	role_id, role_name, role_desc)
    	VALUES (1, 'admin', 'admin');




    INSERT INTO users.user_details(user_id, username, email_id, created_on, last_login, password, deleted_b,is_mfa_required) VALUES (1, 'admin', 'admin@example.com', NULL, NULL, 'test',0, false);
    INSERT INTO users.user_details (user_id, username, email_id, created_on, last_login, password, deleted_b,is_mfa_required) VALUES (2, 'admin', 'test@example.com', NULL,NULL, 'test',0,false);
    INSERT INTO users.user_details (user_id, username, email_id, created_on, last_login, password, deleted_b,is_mfa_required) VALUES (3, 'admin', 'amps@example.com', NULL, NULL, 'test',0,true);
    INSERT INTO users.user_details (user_id, username, email_id, created_on, last_login, password, deleted_b,is_mfa_required) VALUES (4, 'admin', 'qa@example.com', NULL, NULL, 'test',0,false);

    INSERT INTO users.user_roles(
            	user_role_key, user_id_fk, role_id_fk)
            	VALUES (1, 1, 1);

INSERT INTO users.user_mfa_details(id, user_id, mfa_secret, ip_address, is_trusted, ts_token_expiration, finger_print_info, register_date, created_date)
VALUES (1, 1, 'test', 'test', false, NULL, 'test', NULL, NULL);

INSERT INTO users.user_mfa_details(id, user_id, mfa_secret, ip_address, is_trusted, ts_token_expiration, finger_print_info, register_date, created_date)
VALUES (2, 2, 'test', 'test', true, NULL, 'test', NULL, NULL);





