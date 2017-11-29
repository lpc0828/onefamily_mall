CREATE TABLE t_user (
  id bigint,
  nick_name character varying(32) not null,
  headimgurl text,
  sex smallint not null default 3,
  mobile character varying(20) not null,
  certification_yn smallint not null default 0,
  card_no character varying(64) default null,
  real_name character varying(64) default null,
  unionid character varying(64) default null,
  status smallint not null default 1,
  app_token character varying(64) default null,
  wx_token character varying(64) default null,
  created_date timestamp without time zone not null default now(),
  modi_date timestamp without time zone,
  comments text default null,
  constraint pk_t_user primary key (id)
);

comment on table t_user IS '用户表';
comment on column t_user.id IS 'PK';
comment on column t_user.nick_name IS '昵称';
comment on column t_user.headimgurl IS '头像';
comment on column t_user.sex is '性别: 1男; 2女; 3未知';
comment on column t_user.mobile IS '用户唯一手机号';
comment on column t_user.certification_yn IS '是否实名认证, 0否, 1是';
comment on column t_user.card_no IS '加密后的身份证密码';
comment on column t_user.real_name IS '真实姓名';
comment on column t_user.unionid IS '用户的unionid';
comment on column t_user.created_date IS '用户创建时间';
comment on column t_user.modi_date  IS '最后修改时间';
comment on column t_user.status IS '用户状态: 1正常; 2删除';
comment on column t_user.app_token IS '用户APP登录token';
comment on column t_user.wx_token IS '用户微信登录token';
comment on column t_user.comments IS '备注';

create sequence seq_t_user_id
increment by 1
minvalue 10000
maxvalue 9223372036854775807
start with 10000
NO CYCLE;

