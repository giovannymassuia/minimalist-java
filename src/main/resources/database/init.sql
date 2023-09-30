create table if not exists todos
(
    id   uuid primary key,
    task varchar(255) not null,
    done boolean      not null default false
)