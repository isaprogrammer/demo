create table if not exists `sys_user`
(
    `id`                  bigint(11) unsigned not null auto_increment comment '主键id 无符号 自增长',
    `name`     text                not null comment 'name',
    primary key (`id`)
) auto_increment = 9999
  default charset = utf8mb4;