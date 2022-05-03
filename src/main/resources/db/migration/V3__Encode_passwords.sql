create extension if not exists pgcrypto;
update k_user
set u_password = crypt(u_password, gen_salt('bf', 8));