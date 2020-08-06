create sequence hibernate_sequence start 1 increment 1;

create table post (
    id int8 not null,
    couple1 varchar(255),
    couple2 varchar(255),
    couple3 varchar(255),
    couple4 varchar(255),
    couple5 varchar(255),
    couple6 varchar(255),
    couple7 varchar(255),
    day_of_week varchar(255),
    parity varchar(255),
    subgroup varchar(255),
    team varchar(255),
    author_id int8, primary key (id)
);

create table user_roles (
    user_id int8 not null,
    roles varchar(255)
);

create table usr (
    id int8 not null,
    activation_code varchar(255),
    active boolean not null,
    email varchar(255),
    password varchar(255) not null,
    username varchar(255) not null,
    primary key (id)
);

alter table if exists post
    add constraint post_user_fk
        foreign key (author_id) references usr;

alter table if exists user_roles
    add constraint user_roles_user_fk
        foreign key (user_id) references usr;