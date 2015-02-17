# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table customer (
  id                        bigint not null,
  date                      timestamp,
  cust_id                   varchar(255),
  cust_fname                varchar(255),
  cust_lname                varchar(255),
  date_of_birth             timestamp,
  email                     varchar(255),
  address1                  varchar(255),
  address2                  varchar(255),
  city                      varchar(255),
  phone_num                 varchar(255),
  password                  varchar(255),
  constraint pk_customer primary key (id))
;

create table login (
  id                        bigint not null,
  email_in                  varchar(255),
  password_in               varchar(255),
  constraint pk_login primary key (id))
;

create sequence customer_seq;

create sequence login_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists customer;

drop table if exists login;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists customer_seq;

drop sequence if exists login_seq;

