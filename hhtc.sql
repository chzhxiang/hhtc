DROP TABLE IF EXISTS t_advice_info;
CREATE TABLE t_advice_info(
id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
openid      VARCHAR(64) NOT NULL COMMENT '粉丝的openid',
content     MEDIUMTEXT  NOT NULL COMMENT '内容',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='用户反馈表';


DROP TABLE IF EXISTS t_mpp_reply_info;
CREATE TABLE t_mpp_reply_info(
id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
uid         INT           NOT NULL COMMENT '平台用户ID，对应t_user#id',
category    TINYINT(1)    NOT NULL COMMENT '回复的类别：0--通用的回复，1--关注后回复，2--关键字回复',
type        TINYINT(1)    NOT NULL COMMENT '回复的类型：0--文本，1--图文，2--图片，3--活动，4--转发到多客服',
keyword     VARCHAR(16)   COMMENT '关键字',
content     VARCHAR(2048) COMMENT '回复的内容',
plugin_id   INT           COMMENT '活动插件ID，对应t_plugin#id',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
UNIQUE INDEX unique_index_uid_keyword(uid, keyword)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='统一回复设置表';


DROP TABLE IF EXISTS t_mpp_menu_info;
CREATE TABLE t_mpp_menu_info(
id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
pid         INT          COMMENT '上一级菜单的ID，一级菜单情况下为0',
uid         INT          NOT NULL COMMENT '平台用户ID，对应t_user#id',
type        TINYINT(1)   NOT NULL COMMENT '菜单类型：1--CLICK，2--VIEW，3--JSON',
level       TINYINT(1)   COMMENT '菜单级别：1--一级菜单，2--二级菜单',
name        VARCHAR(16)  COMMENT '菜单名称',
view_url    VARCHAR(256) COMMENT 'type=2时用到',
reply_id    INT          COMMENT 'type=1时用到，对应t_reply_info#id',
menu_json   MEDIUMTEXT   COMMENT '微信或QQ公众号菜单JSON',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='自定义菜单表';


DROP TABLE IF EXISTS t_mpp_fans_info;
CREATE TABLE t_mpp_fans_info(
id                       INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
uid                      INT          NOT NULL COMMENT '平台用户ID，对应t_mpp_user_info#id',
infor_state              CHAR(1)  COMMENT '用户信息状态:0--未授权，1--未验证电话，2--未验证住址，3--未验证车位或车牌，4基本信息补充完成',
openid                   VARCHAR(64)  NOT NULL COMMENT '粉丝的openid',
name                     VARCHAR(16)  COMMENT '粉丝的真实姓名',
id_card                  VARCHAR(18)  COMMENT '粉丝的身份证号',
phone_no                 CHAR(11)     COMMENT '粉丝的手机号',
subscribe                CHAR(1)      NOT NULL COMMENT '关注状态：0--未关注，其它为已关注',
nickname                 VARCHAR(32)  COMMENT '粉丝的昵称',
sex                      TINYINT(1)   COMMENT '粉丝的性别：0--未知，1--男，2--女',
city                     VARCHAR(32)  COMMENT '粉丝所在城市',
country                  VARCHAR(32)  COMMENT '粉丝所在国家',
province                 VARCHAR(32)  COMMENT '粉丝所在省份',
language                 VARCHAR(32)  COMMENT '粉丝的语言，简体中文为zh_CN',
headimgurl               VARCHAR(512) COMMENT '粉丝的头像，值为腾讯服务器的图片URL',
subscribe_time           VARCHAR(19)  COMMENT '粉丝最后一次关注的时间，格式为yyyy-MM-dd HH:mm:ss',
unionid                  VARCHAR(64)  COMMENT '只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段',
remark                   VARCHAR(64)  COMMENT '公众号运营者对粉丝的备注',
groupid                  VARCHAR(16)  COMMENT '粉丝用户所在的分组ID',
car_owner_status         TINYINT(1)   COMMENT '车主状态：0--未注册，1--审核中，2--已注册车主',
car_owner_audit_status   TINYINT(1)   COMMENT '车主审核状态：1--审核中，2--审核通过，3--审核拒绝',
car_owner_reg_time       DATETIME     COMMENT '车主注册时间',
car_owner_audit_time     DATETIME     COMMENT '车主审核时间',
car_owner_audit_uid      INT          COMMENT '车主审核人的uid，对应t_mpp_user_info#id',
car_owner_audit_remark   VARCHAR(99)  COMMENT '车主审核备注',
car_park_status          TINYINT(1)   COMMENT '车位主状态：0--未注册，1--审核中，2--已注册车位主',
car_park_audit_status    TINYINT(1)   COMMENT '车位主审核状态：1--审核中，2--审核通过，3--审核拒绝',
car_park_reg_time        DATETIME     COMMENT '车位主注册时间',
car_park_audit_time      DATETIME     COMMENT '车位主审核时间',
car_park_audit_uid       INT          COMMENT '车位主审核人的uid，对应t_mpp_user_info#id',
car_park_audit_remark    VARCHAR(99)  COMMENT '车位主审核备注',
car_owner_community_id   INT          COMMENT '车主所在的小区ID，对应t_community_info#id',
car_park_community_id    INT          COMMENT '车位主所在的小区ID，对应t_community_info#id',
car_owner_community_name VARCHAR(32)  COMMENT '车主所在的小区名称，冗余自t_community_info#name',
car_park_community_name  VARCHAR(32)  COMMENT '车位主所在的小区名称，冗余自t_community_info#name',
house_number             VARCHAR(32)  COMMENT '门牌号',
car_number               VARCHAR(999) COMMENT '车牌号（多个则以`分割）',
-- house_equity_img VARCHAR(999) COMMENT '房产证/租房合同图片（多张则以`分隔）',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
UNIQUE INDEX unique_index_phoneNo(phone_no),
UNIQUE INDEX unique_index_uid_openid(uid, openid)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='粉丝表';


DROP TABLE IF EXISTS t_mpp_user_info;
CREATE TABLE t_mpp_user_info(
id           INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
pid          INT          NOT NULL COMMENT '平台用户所属上一级ID',
username     VARCHAR(16)  NOT NULL COMMENT '用户名',
password     VARCHAR(32)  NOT NULL COMMENT '登录密码',
type         TINYINT(1)   NOT NULL COMMENT '用户类型：0--超管，1--运营，2--物管',
uuid         VARCHAR(32)  COMMENT '用户唯一标识，用来生成微信或QQ公众平台Token',
mptype       TINYINT(1)   COMMENT '公众平台类型：0--未知，1--微信，2--QQ',
mpid         VARCHAR(32)  COMMENT '微信或QQ公众平台原始ID',
mpno         VARCHAR(32)  COMMENT '微信或QQ公众平台号',
mpname       VARCHAR(32)  COMMENT '微信或QQ公众平台名称',
appid        VARCHAR(32)  COMMENT '微信或QQ公众平台appid',
appsecret    VARCHAR(64)  COMMENT '微信或QQ公众平台appsecret',
mchid        VARCHAR(64)  COMMENT '微信或QQ公众平台商户号',
mchkey       VARCHAR(64)  COMMENT '微信或QQ公众平台商户Key',
bind_status  TINYINT(1)   COMMENT '微信或QQ公众平台绑定状态：0--未绑定，1--已绑定',
bind_time    TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '微信或QQ公众平台绑定解绑时间',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
UNIQUE INDEX unique_index_username(username)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='mpplus平台用户表';


DROP TABLE IF EXISTS t_community_info;
CREATE TABLE t_community_info(
id                     INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
uid                    INT           NOT NULL COMMENT '用户ID，对应t_mpp_user_info#id（即物管ID）',
name                   VARCHAR(32)   NOT NULL COMMENT '小区名称',
city_name              VARCHAR(32)   NOT NULL COMMENT '城市名称',
province_name          VARCHAR(32)   NOT NULL COMMENT '省份名称',
point_lng              VARCHAR(32)   NOT NULL COMMENT '腾讯地图经度（http://lbs.qq.com/tool/getpoint/）',
point_lat              VARCHAR(32)   NOT NULL COMMENT '腾讯地图纬度（http://lbs.qq.com/tool/getpoint/）',
address                VARCHAR(99)   NOT NULL COMMENT '小区位置',
link_man               VARCHAR(16)   NOT NULL COMMENT '联系人',
link_tel               VARCHAR(11)   NOT NULL COMMENT '联系人手机号',
money_base             DECIMAL(16,4) NOT NULL COMMENT '押金，单位：元',
money_rent_day         DECIMAL(16,4) NOT NULL COMMENT '租金（日间），单位：元',
money_rent_night       DECIMAL(16,4) NOT NULL COMMENT '租金（夜间），单位：元',
money_rent_full        DECIMAL(16,4) NOT NULL COMMENT '租金（全天），单位：元',
rent_ratio_platform    INT           NOT NULL COMMENT '分润比例，比如20%则存储为20',
rent_ratio_carparker   INT           NOT NULL COMMENT '分润比例，比如80%则存储为80',
car_park_number_prefix VARCHAR(999)  NOT NULL COMMENT '车位号前缀，组成为：楼层+区域',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='小区信息表（小区若有多个车场，则创建多笔记录）';


DROP TABLE IF EXISTS t_community_device;
CREATE TABLE t_community_device(
id              INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
community_id    INT          NOT NULL COMMENT '小区ID，对应t_community_info#id',
community_name  VARCHAR(99)  NOT NULL COMMENT '小区名称，对应t_community_info#name',
serialno_camera VARCHAR(99)  NOT NULL COMMENT '摄像头系列号',
serialno_relays VARCHAR(99)  NOT NULL COMMENT '继电器系列号',
relays_doorid   VARCHAR(99)  COMMENT '继电器闸门ID',
type            TINYINT(1)   NOT NULL COMMENT '出入类型：1--进场，2--出场',
remark          VARCHAR(999) NOT NULL COMMENT '备注',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='小区设备表';


DROP TABLE IF EXISTS t_community_device_flow;
CREATE TABLE t_community_device_flow(
id              INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
device_id       INT          NOT NULL COMMENT '设备ID，对应t_community_device#id',
community_id    INT          NOT NULL COMMENT '小区ID，对应t_community_info#id',
community_name  VARCHAR(99)  NOT NULL COMMENT '小区名称，对应t_community_info#name',
scan_time       DATETIME     COMMENT '车牌识别时间',
scan_car_number VARCHAR(64)  COMMENT '识别的车牌号',
scan_allow_open TINYINT(1)   COMMENT '识别结果：0--不开闸，1--开闸',
open_result     TINYINT(1)   COMMENT '开闸结果：0--开闸失败，1--开闸成功',
open_time       DATETIME     COMMENT '开闸时间',
open_remark     VARCHAR(99)  COMMENT '开闸备注',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='小区设备扫描流水表';


DROP TABLE IF EXISTS t_goods_info;
CREATE TABLE t_goods_info(
id                   INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
community_id         INT          NOT NULL COMMENT '小区ID，对应t_community_info#id',
community_name       VARCHAR(32)  NOT NULL COMMENT '小区名称，冗余自t_community_info#name',
appid                VARCHAR(64)  NOT NULL COMMENT '粉丝關注的公眾號的appid',
openid               VARCHAR(64)  NOT NULL COMMENT '粉丝的openid',
car_park_number      VARCHAR(32)  NOT NULL COMMENT '车位号',
car_park_img         VARCHAR(999) COMMENT '车位平面图',
car_equity_img       VARCHAR(999) COMMENT '车位产权证明图片（多张则以`分隔）',
car_useful_from_date INT          COMMENT '车位可用的起始有效期，格式为20170712',
car_useful_end_date  INT          COMMENT '车位可用的截止有效期，格式为20170712',
is_used              TINYINT(1)   NOT NULL COMMENT '是否使用：0--待发布，1--发布中，2--已被预约',
is_repetition        TINYINT(1)   NOT NULL COMMENT '是否重复：0--未重复，1--重复',
car_audit_status     TINYINT(1)   NOT NULL COMMENT '车位审核状态：1--审核中，2--审核通过，3--审核拒绝',
car_audit_time       DATETIME     COMMENT '车位审核时间',
car_audit_uid        INT          COMMENT '车位审核人的uid,，对应t_mpp_user_info#id',
car_audit_remark     VARCHAR(99)  COMMENT '车位审核备注',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
UNIQUE INDEX unique_index_openid_carParkNumber(openid, car_park_number)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='商品信息表（存储车位信息）';


DROP TABLE IF EXISTS t_goods_need_info;
CREATE TABLE t_goods_need_info(
id                     INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
appid                  VARCHAR(32)   NOT NULL COMMENT '当前公众号的appid',
openid                 VARCHAR(64)   NOT NULL COMMENT '粉丝的openid',
community_id           INT           NOT NULL COMMENT '小区ID，对应t_community_info#id',
car_number             VARCHAR(16)   NOT NULL COMMENT '车牌号',
need_type              TINYINT(1)    NOT NULL COMMENT '需求的车位类型：1--日间，2--夜间，3--全天',
need_from_time         INT           NOT NULL COMMENT '需求的停放起始时间，格式为900则表示09:00（24小时制）',
need_end_time          INT           NOT NULL COMMENT '需求的停放截止时间，格式为1630则表示16:30（24小时制）',
need_from_date         INT           NOT NULL COMMENT '需求的停放起始日期，格式为20170715',
need_end_date          INT           NOT NULL COMMENT '需求的停放截止日期，格式为20170715',
money_rent             DECIMAL(16,4) NOT NULL COMMENT '租金（不含押金），单位：元',
status                 TINYINT(1)    NOT NULL COMMENT '需求状态：1--有效（可由系统自动匹配车位），2--已匹配',
goods_publish_order_id INT           COMMENT '匹配到的发布订单ID，对应t_goods_publish_order#id',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='商品需求信息表（存储车主已发布的需求）';


DROP TABLE IF EXISTS t_goods_need_history;
CREATE TABLE t_goods_need_history(
id                     INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
need_id                INT           NOT NULL COMMENT 't_goods_need_info#id',
appid                  VARCHAR(32)   NOT NULL COMMENT '当前公众号的appid',
openid                 VARCHAR(64)   NOT NULL COMMENT '粉丝的openid',
community_id           INT           NOT NULL COMMENT '小区ID，对应t_community_info#id',
car_number             VARCHAR(16)   NOT NULL COMMENT '车牌号',
need_type              TINYINT(1)    NOT NULL COMMENT '需求的车位类型：1--日间，2--夜间，3--全天',
need_from_time         INT           NOT NULL COMMENT '需求的停放起始时间，格式为900则表示09:00（24小时制）',
need_end_time          INT           NOT NULL COMMENT '需求的停放截止时间，格式为1630则表示16:30（24小时制）',
need_from_date         INT           NOT NULL COMMENT '需求的停放起始日期，格式为20170715',
need_end_date          INT           NOT NULL COMMENT '需求的停放截止日期，格式为20170715',
money_rent             DECIMAL(16,4) NOT NULL COMMENT '租金（不含押金），单位：元',
status                 TINYINT(1)    NOT NULL COMMENT '需求状态：1--有效（可由系统自动匹配车位），2--已匹配',
goods_publish_order_id INT           COMMENT '匹配到的发布订单ID，对应t_goods_publish_order#id',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='商品需求历史表（存储没有匹配到的过期需求）';


DROP TABLE IF EXISTS t_goods_publish_order;
CREATE TABLE t_goods_publish_order(
id                 INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
goods_publish_ids  VARCHAR(999)  NOT NULL COMMENT '商品发布的ids，对应t_goods_publish_info#id，多个以`分隔',
openid             VARCHAR(64)   NOT NULL COMMENT '粉丝的openid',
community_id       INT           NOT NULL COMMENT '小区ID，对应t_community_info#id',
community_name     VARCHAR(32)   NOT NULL COMMENT '小区名称，冗余自t_community_info#name',
goods_id           INT           NOT NULL COMMENT '商品ID，即车位ID，对应t_goods_info#id',
car_park_number    VARCHAR(32)   NOT NULL COMMENT '车位号，冗余自t_goods_info#car_park_number',
car_park_img       VARCHAR(999)  NOT NULL COMMENT '车位平面图，冗余自t_goods_info#car_park_img',
price              DECIMAL(16,4) NOT NULL COMMENT '租停价格，单位：元',
publish_type       TINYINT(1)    NOT NULL COMMENT '车位发布类型：1--日间，2--夜间，3--全天',
publish_from_dates VARCHAR(999)  NOT NULL COMMENT '车位发布的起始日期集合，格式为20170715-20170716-20170720',
publish_from_time  INT           NOT NULL COMMENT '车位发布的起始时间，格式为900则表示09:00（24小时制）',
publish_end_time   INT           NOT NULL COMMENT '车位发布的截止时间，格式为1630则表示16:30（24小时制）',
from_type          TINYINT(1)    NOT NULL COMMENT '来源类型：1--车位主发布，2--车主预约后转租，3--原信息被车主需求匹配后切割而生成',
from_id            INT           NOT NULL COMMENT '车位来源ID：非转租则填0，转租则填原发布订单ID（即自关联的本表主键）',
status             TINYINT(1)    NOT NULL COMMENT '状态：0--未锁定，1--已锁定，2--已使用，3--已过期',
lock_from_date     DATETIME      COMMENT '锁定的起始时间',
lock_end_date      DATETIME      COMMENT '锁定的结束时间',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
INDEX index_goodsId(goods_id)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='商品发布订单表';


DROP TABLE IF EXISTS t_goods_publish_info;
CREATE TABLE t_goods_publish_info(
id                     INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
goods_publish_order_id INT           NOT NULL COMMENT '商品发布的订单ID，对应t_goods_publish_order#id',
openid                 VARCHAR(64)   NOT NULL COMMENT '粉丝的openid',
community_id           INT           NOT NULL COMMENT '小区ID，对应t_community_info#id',
community_name         VARCHAR(32)   NOT NULL COMMENT '小区名称，冗余自t_community_info#name',
goods_id               INT           NOT NULL COMMENT '商品ID，即车位ID，对应t_goods_info#id',
car_park_number        VARCHAR(32)   NOT NULL COMMENT '车位号，冗余自t_goods_info#car_park_number',
car_park_img           VARCHAR(999)  NOT NULL COMMENT '车位平面图，冗余自t_goods_info#car_park_img',
price                  DECIMAL(16,4) NOT NULL COMMENT '租停价格，单位：元',
publish_type           TINYINT(1)    NOT NULL COMMENT '车位发布类型：1--日间，2--夜间，3--全天',
publish_from_date      INT           NOT NULL COMMENT '车位发布的起始日期，格式为20170715',
publish_end_date       INT           NOT NULL COMMENT '车位发布的截止日期，格式为20170716',
publish_from_time      INT           NOT NULL COMMENT '车位发布的起始时间，格式为900则表示09:00（24小时制）',
publish_end_time       INT           NOT NULL COMMENT '车位发布的截止时间，格式为1630则表示16:30（24小时制）',
from_type              TINYINT(1)    NOT NULL COMMENT '来源类型：1--车位主发布，2--车主预约后转租，3--原信息被车主需求匹配后切割而生成',
from_ids               VARCHAR(999)  NOT NULL COMMENT '车位来源ID：非转租则填0，转租则填原发布ID（即自关联的本表主键），需求匹配后切割同样填原发布ID，多个则以`分隔',
status                 TINYINT(1)    NOT NULL COMMENT '状态：0--未锁定，1--已锁定，2--已使用',
lock_from_date         DATETIME      COMMENT '锁定的起始时间',
lock_end_date          DATETIME      COMMENT '锁定的结束时间',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
INDEX index_goodsId(goods_id)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='商品发布信息表（存储已发布的，即可供车主付费停放的车位信息）';


DROP TABLE IF EXISTS t_goods_publish_history;
CREATE TABLE t_goods_publish_history(
id                     INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
goods_publish_id       INT           NOT NULL COMMENT '商品发布的ID，对应t_goods_publish_info#id',
goods_publish_order_id INT           NOT NULL COMMENT '商品发布的订单ID，对应t_goods_publish_order#id',
openid                 VARCHAR(64)   NOT NULL COMMENT '粉丝的openid',
community_id           INT           NOT NULL COMMENT '小区ID，对应t_community_info#id',
community_name         VARCHAR(32)   NOT NULL COMMENT '小区名称，冗余自t_community_info#name',
goods_id               INT           NOT NULL COMMENT '商品ID，即车位ID，对应t_goods_info#id',
car_park_number        VARCHAR(32)   NOT NULL COMMENT '车位号，冗余自t_goods_info#car_park_number',
car_park_img           VARCHAR(999)  NOT NULL COMMENT '车位平面图，冗余自t_goods_info#car_park_img',
price                  DECIMAL(16,4) NOT NULL COMMENT '租停价格',
publish_type           TINYINT(1)    NOT NULL COMMENT '车位发布类型：1--日间，2--夜间，3--全天',
publish_from_date      INT           NOT NULL COMMENT '车位发布的起始日期，格式为20170715',
publish_end_date       INT           NOT NULL COMMENT '车位发布的截止日期，格式为20170716',
publish_from_time      INT           NOT NULL COMMENT '车位发布的起始时间，格式为900则表示09:00（24小时制）',
publish_end_time       INT           NOT NULL COMMENT '车位发布的截止时间，格式为1630则表示16:30（24小时制）',
from_type              TINYINT(1)    NOT NULL COMMENT '来源类型：1--车位主发布，2--车主预约后转租，3--原发布信息被部分预约',
from_ids               VARCHAR(999)  NOT NULL COMMENT '车位来源ID：非转租则填0，转租则填原发布ID（即自关联的本表主键），部分预约同样填原发布ID，多个则以`分隔',
status                 TINYINT(1)    NOT NULL COMMENT '状态：0--未锁定，1--已锁定，2--已使用，3--已取消（车位主在未锁定的前一个小时允许主动取消），4--已删除',
lock_from_date         DATETIME      COMMENT '锁定的起始时间',
lock_end_date          DATETIME      COMMENT '锁定的结束时间',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='商品发布信息历史表（存储已过期的所有记录，其与status无关）';


DROP TABLE IF EXISTS t_order_inout;
CREATE TABLE t_order_inout(
id                     INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
community_id           INT           NOT NULL COMMENT '小区ID，对应t_community_info#id',
max_index              INT           NOT NULL COMMENT '卡号的最大序列号（用于计算最后一个信息的结算状态，来更新订单的结算状态）',
card_no                VARCHAR(32)   NOT NULL COMMENT '停车场卡号',
order_no               VARCHAR(32)   NOT NULL COMMENT '停车下单号，对应t_order_info#out_trade_no',
car_number             VARCHAR(16)   COMMENT '车牌号，即发生交易时车主使用的车牌',
from_date              INT           NOT NULL COMMENT '车位预约停车起始日期',
end_date               INT           NOT NULL COMMENT '车位预约停车截止日期',
from_time              INT           NOT NULL COMMENT '车位预约停车起始时间，格式为930则表示09:30（24小时制）',
end_time               INT           NOT NULL COMMENT '车位预约停车截止时间，格式为1600则表示16:00（24小时制）',
allow_latest_out_date  DATETIME      NOT NULL COMMENT '车位允许的最晚驶离时间',
openid                 VARCHAR(64)   NOT NULL COMMENT '车主的openid',
goods_openid           VARCHAR(64)   NOT NULL COMMENT '车位主的openid',
last_deduct_money      DECIMAL(16,4) COMMENT '上一次扣款金额，单位：元',
last_deduct_time       DATETIME      COMMENT '上一次扣款时间',
next_deduct_start_time DATETIME      COMMENT '下一次扣款的起始时间（用于超时一个小时后每半个小时的判断）',
next_deduct_end_time   DATETIME      COMMENT '下一次扣款的截止时间（用于超时一个小时后每半个小时的判断）',
in_time                DATETIME      COMMENT '车辆入场时间',
out_time               DATETIME      COMMENT '车辆出场时间',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='订单出入记录表';


DROP TABLE IF EXISTS t_order_rent;
CREATE TABLE t_order_rent(
id              INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
community_id    INT           NOT NULL COMMENT '小区ID，对应t_community_info#id',
order_id        INT           NOT NULL COMMENT '订单ID，对应t_order_info#id',
order_type      TINYINT(1)    NOT NULL COMMENT '订单类型：1--车主预约下单，2--车主需求下单，99--车位被订单调配后下单',
order_from_date INT           NOT NULL COMMENT '订单的业务起始日期，格式为yyyyMMdd',
goods_openid    VARCHAR(64)   NOT NULL COMMENT '車位主的openid',
order_money     DECIMAL(16,4) NOT NULL COMMENT '訂單總金額，单位：元',
per_money       DECIMAL(16,4) NOT NULL COMMENT '訂單平均金額，单位：元',
other_money     DECIMAL(16,4) NOT NULL COMMENT '剩餘待分潤的訂單金額，单位：元',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='订单分潤表';


DROP TABLE IF EXISTS t_order_info;
CREATE TABLE t_order_info(
id                      INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
community_id            INT           NOT NULL COMMENT '小区ID，对应t_community_info#id',
community_name          VARCHAR(32)   NOT NULL COMMENT '小区名称，冗余自t_community_info#name',
goods_id                INT           COMMENT '商品ID，即车位ID，对应t_goods_info#id',
goods_publish_order_ids VARCHAR(999)  COMMENT '商品发布的订单ID，对应t_goods_publish_order#id，多个ID则以`分隔',
goods_need_id           INT           COMMENT '车主发布的需求ID，对应t_goods_need_info#id',
car_park_number         VARCHAR(32)   COMMENT '车位号，冗余自t_goods_info#car_park_number',
car_park_img            VARCHAR(999)  COMMENT '车位平面图，冗余自t_goods_info#car_park_img',
car_number              VARCHAR(16)   COMMENT '车牌号，即发生交易时车主使用的车牌',
open_type               TINYINT(1)    COMMENT '车位预约停车类型：1--日间，2--夜间，3--全天',
open_from_time          INT           COMMENT '车位预约停车起始时间，格式为930则表示09:30（24小时制）',
open_end_time           INT           COMMENT '车位预约停车截止时间，格式为1600则表示16:00（24小时制）',
open_from_dates         VARCHAR(999)  COMMENT '车位预约停车起始日期集合，以半角横杠分隔，示例：20170719-20170727-20170727',
appid                   VARCHAR(32)   COMMENT 'wxpay-appid',
body                    VARCHAR(512)  COMMENT 'wxpay-商品描述',
attach                  VARCHAR(512)  COMMENT 'wxpay-附加数据',
out_trade_no            VARCHAR(32)   NOT NULL COMMENT 'wxpay-商户订单号',
total_fee               INT           NOT NULL COMMENT 'wxpay-标价金额，即订单总金额，单位为分',
deposit_money           DECIMAL(16,4) NOT NULL COMMENT '押金，单位：元',
can_refund_money        DECIMAL(16,4) NOT NULL COMMENT '剩余可退款金额，单位：元',
spbill_create_ip        VARCHAR(23)   COMMENT 'wxpay-终端IP',
time_start              CHAR(14)      COMMENT 'wxpay-交易起始时间，即订单生成时间，格式为yyyyMMddHHmmss',
time_expire             CHAR(14)      COMMENT 'wxpay-交易结束时间，即订单失效时间，格式为yyyyMMddHHmmss（最短失效必须大于5分钟）',
notify_url              VARCHAR(512)  COMMENT 'wxpay-通知地址',
trade_type              VARCHAR(8)    COMMENT 'wxpay-交易类型：JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付',
product_id              INT           COMMENT 'wxpay-商品ID',
openid                  VARCHAR(64)   NOT NULL COMMENT 'wxpay-用户标识，trade_type=JSAPI时（即公众号支付），此参数必传，此参数为微信用户在商户对应appid下的唯一标识',
is_subscribe            CHAR(1)       COMMENT 'wxpay-notify-用户是否关注公众账号，Y--关注，N--未关注，仅在公众账号类型支付有效',
bank_type               VARCHAR(16)   COMMENT 'wxpay-notify-付款银行，其为采用字符串类型的银行标识',
cash_fee                INT           COMMENT 'wxpay-notify-现金支付金额',
transaction_id          VARCHAR(64)   COMMENT 'wxpay-notify-微信支付订单号',
time_end                CHAR(14)      COMMENT 'wxpay-notify-支付完成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010',
trade_state_desc        VARCHAR(512)  COMMENT 'wxpay-query-交易状态描述（对当前查询订单状态的描述和下一步操作的指引）',
notify_time             DATETIME      COMMENT '后台通知的时间',
is_notify               TINYINT(1)    COMMENT '是否已后台通知：0--未通知，1--已通知',
order_type              TINYINT(1)    NOT NULL COMMENT '订单类型：1--车主预约下单，2--车主需求下单，10--个人中心充值，11--车位主发布车位充值，12--车主预约下单充值，13--车主发布需求充值',
order_status            TINYINT(1)    NOT NULL COMMENT '订单状态：0--待支付，1--支付中，2--支付成功，3--支付失败，4--已关闭，5--转入退款，6--已撤销（刷卡支付），9--已转租，99--订单生命周期已结束',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
INDEX index_openid(openid),
INDEX index_outTradeNo(out_trade_no)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='订单信息表（存储车位已被预约的信息）';


DROP TABLE IF EXISTS t_order_history;
CREATE TABLE t_order_history(
id                      INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
order_id                INT           NOT NULL COMMENT '订单ID，对应t_order_info#id',
community_id            INT           NOT NULL COMMENT '小区ID，对应t_community_info#id',
community_name          VARCHAR(32)   NOT NULL COMMENT '小区名称，冗余自t_community_info#name',
goods_id                INT           COMMENT '商品ID，即车位ID，对应t_goods_info#id',
goods_publish_order_ids VARCHAR(999)  COMMENT '商品发布的订单ID，对应t_goods_publish_order#id，多个ID则以`分隔',
goods_need_id           INT           COMMENT '车主发布的需求ID，对应t_goods_need_info#id',
car_park_number         VARCHAR(32)   COMMENT '车位号，冗余自t_goods_info#car_park_number',
car_park_img            VARCHAR(999)  COMMENT '车位平面图，冗余自t_goods_info#car_park_img',
car_number              VARCHAR(16)   COMMENT '车牌号，即发生交易时车主使用的车牌',
open_type               TINYINT(1)    COMMENT '车位预约停车类型：1--日间，2--夜间，3--全天',
open_from_time          INT           COMMENT '车位预约停车起始时间，格式为930则表示09:30（24小时制）',
open_end_time           INT           COMMENT '车位预约停车截止时间，格式为1600则表示16:00（24小时制）',
open_from_dates         VARCHAR(999)  COMMENT '车位预约停车起始日期集合，以半角横杠分隔，示例：20170719-20170727-20170727',
appid                   VARCHAR(32)   COMMENT 'wxpay-appid',
body                    VARCHAR(512)  COMMENT 'wxpay-商品描述',
attach                  VARCHAR(512)  COMMENT 'wxpay-附加数据',
out_trade_no            VARCHAR(32)   NOT NULL COMMENT 'wxpay-商户订单号',
total_fee               INT           NOT NULL COMMENT 'wxpay-标价金额，即订单总金额，单位为分',
deposit_money           DECIMAL(16,4) NOT NULL COMMENT '押金，单位：元',
can_refund_money        DECIMAL(16,4) NOT NULL COMMENT '剩余可退款金额，单位：元',
spbill_create_ip        VARCHAR(23)   COMMENT 'wxpay-终端IP',
time_start              CHAR(14)      COMMENT 'wxpay-交易起始时间，即订单生成时间，格式为yyyyMMddHHmmss',
time_expire             CHAR(14)      COMMENT 'wxpay-交易结束时间，即订单失效时间，格式为yyyyMMddHHmmss（最短失效必须大于5分钟）',
notify_url              VARCHAR(512)  COMMENT 'wxpay-通知地址',
trade_type              VARCHAR(8)    COMMENT 'wxpay-交易类型：JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付',
product_id              INT           COMMENT 'wxpay-商品ID',
openid                  VARCHAR(64)   NOT NULL COMMENT 'wxpay-用户标识，trade_type=JSAPI时（即公众号支付），此参数必传，此参数为微信用户在商户对应appid下的唯一标识',
is_subscribe            CHAR(1)       COMMENT 'wxpay-notify-用户是否关注公众账号，Y--关注，N--未关注，仅在公众账号类型支付有效',
bank_type               VARCHAR(16)   COMMENT 'wxpay-notify-付款银行，其为采用字符串类型的银行标识',
cash_fee                INT           COMMENT 'wxpay-notify-现金支付金额',
transaction_id          VARCHAR(64)   COMMENT 'wxpay-notify-微信支付订单号',
time_end                CHAR(14)      COMMENT 'wxpay-notify-支付完成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010',
trade_state_desc        VARCHAR(512)  COMMENT 'wxpay-query-交易状态描述（对当前查询订单状态的描述和下一步操作的指引）',
notify_time             DATETIME      COMMENT '后台通知的时间',
is_notify               TINYINT(1)    COMMENT '是否已后台通知：0--未通知，1--已通知',
order_type              TINYINT(1)    NOT NULL COMMENT '订单类型：1--车主预约下单，2--车主需求下单，10--个人中心充值，11--车位主发布车位充值，12--车主预约下单充值，13--车主发布需求充值',
order_status            TINYINT(1)    NOT NULL COMMENT '订单状态：0--待支付，1--支付中，2--支付成功，3--支付失败，4--已关闭，5--转入退款，6--已撤销（刷卡支付），9--已转租，99--订单生命周期已结束',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
INDEX index_openid(openid)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='订单历史表（存储已完成的或未支付成功的过期订单）';


DROP TABLE IF EXISTS t_redpack_info;
CREATE TABLE t_redpack_info(
id                INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
refund_apply_id   INT         NOT NULL COMMENT '退款申请ID，对应t_refund_apply#id',
refund_apply_type TINYINT(1)  NOT NULL COMMENT '退款申请类型：1--退款（押金），2--提现（余额），对应t_refund_apply#apply_type',
appid             VARCHAR(32) NOT NULL COMMENT 'wx-商户对应的微信公众号的appid',
mch_billno        VARCHAR(32) NOT NULL COMMENT 'wx-商户订单号',
re_openid         VARCHAR(64) NOT NULL COMMENT 'wx-发起退款红包申请的粉丝openid',
total_amount      INT         NOT NULL COMMENT 'wx-付款金额，单位为分',
status            TINYINT(1)  NOT NULL COMMENT 'wx-红包状态：0--未提交申请，1--发放中，2--已发放待领取，3--发放失败，4--已领取，5--退款中，6--已退款',
detail_id         VARCHAR(64) COMMENT 'wx-红包单号',
reason            VARCHAR(99) COMMENT 'wx-发送失败原因',
send_time         VARCHAR(99) COMMENT 'wx-红包发送时间，格式：2015-04-21 20:00:00',
refund_time       VARCHAR(99) COMMENT 'wx-红包的退款时间（如果其未领取的退款），格式：2015-04-21 23:03:00',
refund_amount     INT         COMMENT 'wx-红包退款金额',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='红包表';


DROP TABLE IF EXISTS t_refund_info;
CREATE TABLE t_refund_info(
id                  INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
refund_apply_id     INT         NOT NULL COMMENT '退款申请ID，对应t_refund_apply#id',
refund_apply_type   TINYINT(1)  NOT NULL COMMENT '退款申请类型：1--退款（押金），2--提现（余额），对应t_refund_apply#apply_type',
openid              VARCHAR(64) NOT NULL COMMENT '支付订单的粉丝openid',
order_id            INT         NOT NULL COMMENT '订单ID，对应t_order_info#id',
appid               VARCHAR(32) NOT NULL COMMENT 'wx-商户对应的微信公众号的appid',
out_refund_no       VARCHAR(32) NOT NULL COMMENT 'wx-商户退款订单号',
total_fee           INT         NOT NULL COMMENT 'wx-订单金额，单位为分',
refund_fee          INT         NOT NULL COMMENT 'wx-退款金额，单位为分',
refund_desc         VARCHAR(64) NOT NULL COMMENT 'wx-退款原因（若商户传入，会在下发给用户的退款消息中体现退款原因）',
refund_status       TINYINT(1)  NOT NULL COMMENT 'wx-退款状态：0--未提交申请，1--退款处理中，2--退款成功，3--退款关闭，4--退款异常',
refund_accout       VARCHAR(64) COMMENT 'wx-退款资金来源：REFUND_SOURCE_UNSETTLED_FUNDS--未结算资金退款，REFUND_SOURCE_RECHARGE_FUNDS--可用余额退款',
refund_id           VARCHAR(64) COMMENT 'wx-微信退款单号',
refund_channel      VARCHAR(16) COMMENT 'wx-退款渠道',
refund_recv_accout  VARCHAR(64) COMMENT 'wx-退款入账账户：1--退回银行卡，2--退回支付用户零钱，3--退还商户',
refund_success_time VARCHAR(32) COMMENT 'wx-退款成功时间，当退款状态为退款成功时有返回',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='退款表';


DROP TABLE IF EXISTS t_refund_history;
CREATE TABLE t_refund_history(
id                  INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
refund_info_id      INT         NOT NULL COMMENT '退款ID，对应t_refund_info#id',
refund_apply_id     INT         NOT NULL COMMENT '退款申请ID，对应t_refund_apply#id',
refund_apply_type   TINYINT(1)  NOT NULL COMMENT '退款申请类型：1--退款（押金），2--提现（余额），对应t_refund_apply#apply_type',
openid              VARCHAR(64) NOT NULL COMMENT '支付订单的粉丝openid',
order_id            INT         NOT NULL COMMENT '订单ID，对应t_order_info#id',
appid               VARCHAR(32) NOT NULL COMMENT 'wx-商户对应的微信公众号的appid',
out_refund_no       VARCHAR(32) NOT NULL COMMENT 'wx-商户退款订单号',
total_fee           INT         NOT NULL COMMENT 'wx-订单金额，单位为分',
refund_fee          INT         NOT NULL COMMENT 'wx-退款金额，单位为分',
refund_desc         VARCHAR(64) NOT NULL COMMENT 'wx-退款原因（若商户传入，会在下发给用户的退款消息中体现退款原因）',
refund_status       TINYINT(1)  NOT NULL COMMENT 'wx-退款状态：0--未提交申请，1--退款处理中，2--退款成功，3--退款关闭，4--退款异常',
refund_accout       VARCHAR(64) COMMENT 'wx-退款资金来源：REFUND_SOURCE_UNSETTLED_FUNDS--未结算资金退款，REFUND_SOURCE_RECHARGE_FUNDS--可用余额退款',
refund_id           VARCHAR(64) COMMENT 'wx-微信退款单号',
refund_channel      VARCHAR(16) COMMENT 'wx-退款渠道',
refund_recv_accout  VARCHAR(64) COMMENT 'wx-退款入账账户：1--退回银行卡，2--退回支付用户零钱，3--退还商户',
refund_success_time VARCHAR(32) COMMENT 'wx-退款成功时间，当退款状态为退款成功时有返回',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='退款表（资金来源更换）';


DROP TABLE IF EXISTS t_refund_apply;
CREATE TABLE t_refund_apply(
id           INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
appid        VARCHAR(32)  NOT NULL COMMENT '当前公众号的appid',
client_ip    VARCHAR(99)  NOT NULL,
openid       VARCHAR(64)  NOT NULL COMMENT '发起申请的粉丝openid',
order_ids    VARCHAR(999) NOT NULL COMMENT '订单ID，对应t_order_info#id，多个以`分隔',
refund_fee   INT          NOT NULL COMMENT '退款金额，单位为分',
apply_type   TINYINT(1)   NOT NULL COMMENT '申请类型：1--退款（押金），2--提现（余额）',
pay_status   TINYINT(1)   NOT NULL COMMENT '支付状态：0--未支付，1--支付中，2--支付成功（后台申请详情中看的到），3--支付部分失败（后台申请详情中看的到），4--支付全部失败（后台申请详情中看的到）',
audit_status TINYINT(1)   NOT NULL COMMENT '审核状态：1--待审核，2--审核通过，3--审核拒绝',
audit_time   DATETIME     COMMENT '审核时间',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='退款申请表';


DROP TABLE IF EXISTS t_user_funds;
CREATE TABLE t_user_funds(
id            INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
uid           INT           COMMENT '用户ID，对应t_mpp_user_info#id',
openid        VARCHAR(64)   COMMENT '粉丝的openid',
money_base    DECIMAL(16,4) NOT NULL COMMENT '押金，单位：元',
money_freeze  DECIMAL(16,4) NOT NULL COMMENT '冻结金额，单位：元',
money_balance DECIMAL(16,4) NOT NULL COMMENT '余额，单位：元',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='资金表（记录平台、物管、粉丝总收入）';


DROP TABLE IF EXISTS t_user_funds_flow;
CREATE TABLE t_user_funds_flow(
id              INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
funds_id        INT           NOT NULL COMMENT '资金ID，对应t_user_funds#id',
out_trade_no    VARCHAR(64)   COMMENT '订单号，对应t_order_info#out_trade_no',
out_refund_no   VARCHAR(64)   COMMENT '退款单号，对应t_order_refund_info#out_refund_no',
uid             INT           COMMENT '用户ID，对应t_mpp_user_info#id',
openid          VARCHAR(64)   COMMENT '粉丝的openid',
money           DECIMAL(16,4) NOT NULL COMMENT '收入或支出金额绝对值，单位：元',
in_out          VARCHAR(3)    NOT NULL COMMENT '收支类型：in--收入，out--支出',
in_out_desc     VARCHAR(64)   NOT NULL COMMENT '收入或支出的描述',
in_out_type     TINYINT(1)    NOT NULL COMMENT '收支轨迹：1--充值押金，2--充值余额，3--扣减押金，4--扣减余额，5--收入租金，6--返还余额，7--返还押金，8--收入超时补贴，9--无法停车补贴',
biz_date        INT           NOT NULL COMMENT '业务发生日期，存储格式为20160718',
biz_date_time   DATETIME      NOT NULL COMMENT '业务发生时间',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='资金流水表';


DROP TABLE IF EXISTS t_sms_info;
CREATE TABLE t_sms_info(
id          INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
phone_no    CHAR(11)   NOT NULL COMMENT '手机号',
verify_code VARCHAR(8) NOT NULL COMMENT '验证码',
type        TINYINT(1) NOT NULL COMMENT '短信类型：0--通用，1--电话号码验证，2--车位主注册，3--车主提现，4--车位主提现',
is_used     TINYINT(1) NOT NULL COMMENT '是否使用：0--未使用，1--已使用',
used_result TINYINT(1) COMMENT '使用结果：0--未验证通过，1--已验证通过',
used_time   DATETIME   COMMENT '使用时间',
time_send   DATETIME   NOT NULL COMMENT '发送时间',
time_expire DATETIME   NOT NULL COMMENT '过期时间',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='短信表';


INSERT INTO t_mpp_reply_info(uid, category, type) VALUES(2, 0, 4);
INSERT INTO t_mpp_reply_info(uid, category, type, content) VALUES(2, 1, 0, '海量车位，吼吼停车，让天下没有难停的车！！');
INSERT INTO t_mpp_reply_info(uid, category, type, keyword, content) VALUES(2, 2, 0, '联系客服', '客服电话：400-400-400');
INSERT INTO t_mpp_user_info(id, pid, username, password, type, mptype, bind_status) VALUES(1, 0, 'admin', 'admin', 0, 0, 0);
INSERT INTO t_mpp_user_info(id, pid, username, password, type, uuid, mptype, bind_status) VALUES(2, 1, 'hhyy', '5cd57d747b5b7632efa82e67ea3c4e43', 1, REPLACE(UUID(),'-',''), 1, 0);





