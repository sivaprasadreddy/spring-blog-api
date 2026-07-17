create sequence category_id_seq start with 100 increment by 50;

create table categories
(
    id         bigint       not null default nextval('category_id_seq'),
    name       varchar(100) not null,
    slug       varchar(150) not null,
    created_at timestamp    not null,
    updated_at timestamp,
    version    bigint       not null default 0,
    primary key (id),
    constraint categories_slug_unique unique (slug)
);

insert into categories(id, name, slug, created_at) values
(1, 'Java', 'java', now()),
(2, 'Spring', 'spring', now()),
(3, 'General', 'general', now());

alter sequence category_id_seq restart with 101;

alter table posts add column category_id bigint references categories (id);
update posts set category_id = 3 where category_id is null;
alter table posts alter column category_id set not null;
