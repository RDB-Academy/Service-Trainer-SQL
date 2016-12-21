# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table column_def (
  id                            bigint auto_increment not null,
  created_at                    timestamp,
  table_def_id                  bigint not null,
  name                          varchar(255) not null,
  data_type                     varchar(255) not null,
  is_primary                    boolean not null,
  is_not_null                   boolean not null,
  meta_value_set                integer not null,
  min_value_set                 integer,
  max_value_set                 integer,
  modified_at                   timestamp not null,
  constraint pk_column_def primary key (id)
);

create table extension_def (
  id                            bigint auto_increment not null,
  created_at                    timestamp,
  insert_statement              varchar(255) not null,
  table_def_id                  bigint not null,
  modified_at                   timestamp not null,
  constraint pk_extension_def primary key (id)
);

create table foreign_key (
  id                            bigint auto_increment not null,
  created_at                    timestamp,
  name                          varchar(255) not null,
  schema_def_id                 bigint not null,
  modified_at                   timestamp not null,
  constraint pk_foreign_key primary key (id)
);

create table foreign_key_relation (
  id                            bigint auto_increment not null,
  created_at                    timestamp,
  foreign_key_id                bigint not null,
  source_column_id              bigint not null,
  target_column_id              bigint not null,
  modified_at                   timestamp not null,
  constraint pk_foreign_key_relation primary key (id)
);

create table schema_def (
  id                            bigint auto_increment not null,
  created_at                    timestamp,
  name                          varchar(255) not null,
  modified_at                   timestamp not null,
  constraint uq_schema_def_name unique (name),
  constraint pk_schema_def primary key (id)
);

create table session (
  id                            varchar(255) not null,
  created_at                    timestamp,
  user_id                       bigint,
  user_name                     varchar(255),
  connection_info               integer,
  task_trial_id                 bigint,
  modified_at                   timestamp not null,
  constraint pk_session primary key (id)
);

create table table_def (
  id                            bigint auto_increment not null,
  created_at                    timestamp,
  name                          varchar(255) not null,
  schema_def_id                 bigint not null,
  modified_at                   timestamp not null,
  constraint pk_table_def primary key (id)
);

create table task (
  id                            bigint auto_increment not null,
  created_at                    timestamp,
  schema_def_id                 bigint not null,
  name                          varchar(255) not null,
  text                          varchar(255) not null,
  reference_statement           varchar(255) not null,
  difficulty                    integer not null,
  modified_at                   timestamp not null,
  constraint pk_task primary key (id)
);

create table task_trial (
  id                            bigint auto_increment not null,
  created_at                    timestamp,
  tries                         integer,
  begin_date                    timestamp not null,
  submit_date                   timestamp,
  is_available                  boolean,
  seed                          bigint,
  url                           varchar(255),
  path                          varchar(255),
  name                          varchar(255),
  user_statement                varchar(255),
  is_correct                    boolean,
  is_finished                   boolean,
  task_id                       bigint not null,
  modified_at                   timestamp not null,
  constraint pk_task_trial primary key (id)
);

alter table column_def add constraint fk_column_def_table_def_id foreign key (table_def_id) references table_def (id) on delete restrict on update restrict;
create index ix_column_def_table_def_id on column_def (table_def_id);

alter table extension_def add constraint fk_extension_def_table_def_id foreign key (table_def_id) references table_def (id) on delete restrict on update restrict;
create index ix_extension_def_table_def_id on extension_def (table_def_id);

alter table foreign_key add constraint fk_foreign_key_schema_def_id foreign key (schema_def_id) references schema_def (id) on delete restrict on update restrict;
create index ix_foreign_key_schema_def_id on foreign_key (schema_def_id);

alter table foreign_key_relation add constraint fk_foreign_key_relation_foreign_key_id foreign key (foreign_key_id) references foreign_key (id) on delete restrict on update restrict;
create index ix_foreign_key_relation_foreign_key_id on foreign_key_relation (foreign_key_id);

alter table foreign_key_relation add constraint fk_foreign_key_relation_source_column_id foreign key (source_column_id) references column_def (id) on delete restrict on update restrict;
create index ix_foreign_key_relation_source_column_id on foreign_key_relation (source_column_id);

alter table foreign_key_relation add constraint fk_foreign_key_relation_target_column_id foreign key (target_column_id) references column_def (id) on delete restrict on update restrict;
create index ix_foreign_key_relation_target_column_id on foreign_key_relation (target_column_id);

alter table session add constraint fk_session_task_trial_id foreign key (task_trial_id) references task_trial (id) on delete restrict on update restrict;
create index ix_session_task_trial_id on session (task_trial_id);

alter table table_def add constraint fk_table_def_schema_def_id foreign key (schema_def_id) references schema_def (id) on delete restrict on update restrict;
create index ix_table_def_schema_def_id on table_def (schema_def_id);

alter table task add constraint fk_task_schema_def_id foreign key (schema_def_id) references schema_def (id) on delete restrict on update restrict;
create index ix_task_schema_def_id on task (schema_def_id);

alter table task_trial add constraint fk_task_trial_task_id foreign key (task_id) references task (id) on delete restrict on update restrict;
create index ix_task_trial_task_id on task_trial (task_id);


# --- !Downs

alter table column_def drop constraint if exists fk_column_def_table_def_id;
drop index if exists ix_column_def_table_def_id;

alter table extension_def drop constraint if exists fk_extension_def_table_def_id;
drop index if exists ix_extension_def_table_def_id;

alter table foreign_key drop constraint if exists fk_foreign_key_schema_def_id;
drop index if exists ix_foreign_key_schema_def_id;

alter table foreign_key_relation drop constraint if exists fk_foreign_key_relation_foreign_key_id;
drop index if exists ix_foreign_key_relation_foreign_key_id;

alter table foreign_key_relation drop constraint if exists fk_foreign_key_relation_source_column_id;
drop index if exists ix_foreign_key_relation_source_column_id;

alter table foreign_key_relation drop constraint if exists fk_foreign_key_relation_target_column_id;
drop index if exists ix_foreign_key_relation_target_column_id;

alter table session drop constraint if exists fk_session_task_trial_id;
drop index if exists ix_session_task_trial_id;

alter table table_def drop constraint if exists fk_table_def_schema_def_id;
drop index if exists ix_table_def_schema_def_id;

alter table task drop constraint if exists fk_task_schema_def_id;
drop index if exists ix_task_schema_def_id;

alter table task_trial drop constraint if exists fk_task_trial_task_id;
drop index if exists ix_task_trial_task_id;

drop table if exists column_def;

drop table if exists extension_def;

drop table if exists foreign_key;

drop table if exists foreign_key_relation;

drop table if exists schema_def;

drop table if exists session;

drop table if exists table_def;

drop table if exists task;

drop table if exists task_trial;

