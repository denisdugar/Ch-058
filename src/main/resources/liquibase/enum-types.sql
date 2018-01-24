--liquibase formatted sql

--changeset shrralis:1513597385307-1
CREATE TYPE IMAGE_TYPE AS ENUM ('USER', 'ISSUE');

--changeset shrralis:1513597385307-2
CREATE TYPE USER_TYPE AS ENUM ('BANNED', 'USER', 'ADMIN', 'MASTER');