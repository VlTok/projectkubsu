create sequence hibernate_sequence start 1 increment 1;

create table k_couple (
    c_id BIGINT not null,
    c_audience varchar(255),
    c_teacher varchar(255),
    c_time timestamp not null,
    c_title varchar(255) not null,
    c_type varchar(255),
    c_schedule_id BIGINT,
    primary key (c_id)
);

create table k_schedule (
    s_id BIGINT not null,
    s_day_of_week varchar(255) not null,
    s_filename_errors varchar(255),
    s_filename_excel varchar(255),
    s_parity varchar(255) not null,
    s_team varchar(255) not null,
    s_author_id BIGINT,
    primary key (s_id)
);

create table k_user (
    u_id BIGINT not null,
    u_activation_code varchar(255),
    u_active boolean,
    u_email varchar(255) not null,
    u_password varchar(255) not null,
    u_username varchar(30) not null,
    primary key (u_id)
);

create table user_roles (
    user_u_id BIGINT not null,
    roles varchar(255)
);

alter table if exists k_schedule
    add constraint schedule_user_fk
        foreign key (s_author_id) references k_user;

alter table if exists k_couple
    add constraint couple_schedule_fk
        foreign key (c_schedule_id) references k_schedule;

alter table if exists user_roles
    add constraint user_roles_user_fk
        foreign key (user_u_id) references k_user;