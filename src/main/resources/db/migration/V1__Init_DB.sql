create sequence hibernate_sequence start 1 increment 1;
CREATE TABLE k_schedule
(
    s_id          int8         NOT NULL,
    s_day_of_week varchar(255) not null,
    s_parity      varchar(255) not null,
    s_subgroup    varchar(255),
    s_team        varchar(255) not null,
    s_author_id   int8,
    primary key (s_id)
);
CREATE TABLE k_couple
(
    c_id          int8         NOT NULL,
    c_audience    varchar(255) not null,
    c_title       varchar(255) not null,
    c_type        varchar(255) not null,
    c_time        timestamp    not null,
    c_schedule_id int8,
    c_teacher_id  int8,
    PRIMARY KEY (c_id)
);
create table user_roles
(
    user_u_id int8 not null,
    roles     varchar(255)
);
CREATE TABLE k_user
(
    u_id              int8         NOT NULL,
    u_activation_code varchar(255),
    u_active          boolean      not null,
    u_email           varchar(255),
    u_password        varchar(255) not null,
    u_username        varchar(255) not null,
    PRIMARY KEY (u_id)
);
alter table if exists k_schedule
    add constraint schedule_user_fk
        foreign key (s_author_id) references k_user;
alter table if exists k_couple
    add constraint couple_user_fk
        foreign key (c_teacher_id) references k_user;
alter table if exists k_couple
    add constraint couple_schedule_fk
        foreign key (c_schedule_id) references k_user;

alter table if exists user_roles
    add constraint user_roles_user_fk
        foreign key (user_u_id) references k_user;