insert into k_user (u_id, u_activation_code, u_active, u_email, u_password, u_username)
values (1, null, true, 'toxim69143@brbqx.com', 'q', 'admin');

insert into user_roles (user_u_id, roles)
values (1, 'STUDENT'),
       (1, 'TEACHER'),
       (1, 'ADMIN');