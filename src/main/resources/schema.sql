create table if not exists `jmx_file`
(
    `id`                  bigint(11) unsigned not null auto_increment comment '主键id 无符号 自增长',
    `source_jmx_file`     text                not null comment '原始jmx文件',
    `source_jmx_file_md5` char(32)            not null comment '目标mx文件的md5',
    `map`                 text                not null comment '序号与名称的映射关系',
    `operator_name`       varchar(64)         not null comment '操作人姓名',
    `operator_id`         varchar(64)         not null comment '操作人唯一身份标识',
    `gmt_create`          datetime            not null default current_timestamp comment '创建时间',
    `gmt_modified`        datetime            not null default current_timestamp on update current_timestamp comment '修改时间',
    primary key (`id`)
) auto_increment = 9999
  default charset = utf8mb4;