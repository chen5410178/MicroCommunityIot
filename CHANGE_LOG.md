## 变更记录

### 2024-03-11 楼栋加入精度维度 
```sql
alter table f_floor add COLUMN lat varchar(32) comment '精度';
alter table f_floor add COLUMN lng varchar(32) comment '维度';
```
### 2024-03-11 小区加入精度维度
```sql
alter table s_community add COLUMN lat varchar(32) comment '精度';
alter table s_community add COLUMN lng varchar(32) comment '维度';
```


### 2024-07-22 门禁加入监控功能

```sql
alter table access_control add COLUMN monitor_id varchar(30) comment '监控ID'; 
alter table access_control add COLUMN monitor_name varchar(64) comment '监控名称'; 
```

### 2024-11-05 加入收藏视频

```sql
CREATE TABLE `monitor_collect` (
  `collect_id` varchar(64) NOT NULL COMMENT '收藏编号',
  `machine_id` varchar(64) NOT NULL COMMENT '设备编号',
  `machine_code` varchar(64) NOT NULL COMMENT '设备编号',
  `machine_name` varchar(200) NOT NULL COMMENT '设备编号',
  `community_id` varchar(64) NOT NULL COMMENT '小区ID',
  `staff_id` varchar(64) NOT NULL COMMENT '员工ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，S 保存，0, 在用 1失效',
  PRIMARY KEY (`collect_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```
### 2025-01-23 充电桩支持国家新标准537号
```sql
alter table charge_rule_fee add COLUMN `service_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '服务单价';
alter table charge_machine_order add COLUMN `service_price` decimal(10,2)  DEFAULT '0.00' COMMENT '服务费';
```


### 2025-10-09 支持 充电桩退款逻辑
```sql
CREATE TABLE `online_pay` (
  `pay_id` varchar(30) NOT NULL COMMENT '支付ID',
  `order_id` varchar(30) NOT NULL COMMENT '订单ID',
  `transaction_id` varchar(64) DEFAULT NULL COMMENT '交易ID',
  `busi_id` varchar(64) DEFAULT NULL COMMENT '业务ID',
  `app_id` varchar(64) DEFAULT NULL COMMENT 'app_id',
  `total_fee` decimal(10,2) NOT NULL COMMENT '付款金额',
  `remaining_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '剩余金额',
  `pay_name` varchar(256) DEFAULT NULL COMMENT '名称',
  `open_id` varchar(30) DEFAULT NULL COMMENT '开放ID',
  `state` varchar(12) NOT NULL DEFAULT 'W' COMMENT '状态 W待支付 C 支付完成 F 通知失败 WT 待退费 CT退费完成',
  `message` varchar(1024) DEFAULT NULL COMMENT '状态原因',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  `payment_pool_id` varchar(30) DEFAULT NULL COMMENT '支付配置ID',
  PRIMARY KEY (`pay_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `online_pay_refund` (
  `refund_id` varchar(30) NOT NULL COMMENT '退费ID',
  `pay_id` varchar(30) NOT NULL COMMENT '支付ID',
  `refund_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '退款金额',
  `busi_id` varchar(30) DEFAULT NULL COMMENT '业务ID',
  `refund_user_id` varchar(30) DEFAULT NULL COMMENT '退款人',
  `community_id` varchar(30) DEFAULT NULL COMMENT '小区ID',
  `state` varchar(12) NOT NULL DEFAULT 'W' COMMENT '状态 W待支付 C 支付完成 F 通知失败 WT 待退费 CT退费完成',
  `message` varchar(1024) DEFAULT NULL COMMENT '状态原因',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  PRIMARY KEY (`refund_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

### 2026-01-08 支持内部临时车 和 按车牌配置收费
```sql
alter table car_inout add COLUMN `color_type` varchar(12) NOT NULL default '1' COMMENT '车牌颜色';
delete from t_dict where table_name = 'temp_car_fee_config' and table_columns = 'car_type';
INSERT INTO `hc_iot`.`t_dict` (`status_cd`, `name`, `description`, `create_time`, `table_name`, `table_columns`) VALUES ( '9901', '通用车辆', '通用车辆', '2019-10-15 09:47:21', 'temp_car_fee_config', 'car_type');
INSERT INTO `hc_iot`.`t_dict` (`status_cd`, `name`, `description`, `create_time`, `table_name`, `table_columns`) VALUES ( '9902', '内部临时车', '内部临时车', '2019-10-15 09:47:21', 'temp_car_fee_config', 'car_type');
INSERT INTO `hc_iot`.`t_dict` (`status_cd`, `name`, `description`, `create_time`, `table_name`, `table_columns`) VALUES ( '9905', '三轮车', '三轮车', '2019-10-15 09:47:21', 'temp_car_fee_config', 'car_type');
INSERT INTO `hc_iot`.`t_dict` (`status_cd`, `name`, `description`, `create_time`, `table_name`, `table_columns`) VALUES ( '1', '蓝牌车', '蓝牌车', '2019-10-15 09:47:21', 'temp_car_fee_config', 'car_type');
INSERT INTO `hc_iot`.`t_dict` (`status_cd`, `name`, `description`, `create_time`, `table_name`, `table_columns`) VALUES ( '2', '黄牌车', '黄牌车', '2019-10-15 09:47:21', 'temp_car_fee_config', 'car_type');
INSERT INTO `hc_iot`.`t_dict` (`status_cd`, `name`, `description`, `create_time`, `table_name`, `table_columns`) VALUES ( '3', '白牌车', '白牌车', '2019-10-15 09:47:21', 'temp_car_fee_config', 'car_type');
INSERT INTO `hc_iot`.`t_dict` (`status_cd`, `name`, `description`, `create_time`, `table_name`, `table_columns`) VALUES ( '4', '黑牌车', '黑牌车', '2019-10-15 09:47:21', 'temp_car_fee_config', 'car_type');
INSERT INTO `hc_iot`.`t_dict` (`status_cd`, `name`, `description`, `create_time`, `table_name`, `table_columns`) VALUES ( '5', '绿牌车', '绿牌车', '2019-10-15 09:47:21', 'temp_car_fee_config', 'car_type');
INSERT INTO `hc_iot`.`t_dict` (`status_cd`, `name`, `description`, `create_time`, `table_name`, `table_columns`) VALUES ( '6', '黄绿牌车', '黄绿牌车', '2019-10-15 09:47:21', 'temp_car_fee_config', 'car_type');
INSERT INTO `hc_iot`.`c_route` (`id`, `app_id`, `service_id`, `order_type_cd`, `invoke_limit_times`, `invoke_model`, `create_time`, `status_cd`) VALUES ('102026010838440012', '992023010100000001', '982026010876460011', 'Q', '1000', 'S', '2026-01-08 16:07:56', '0');
INSERT INTO `hc_iot`.`c_route` (`id`, `app_id`, `service_id`, `order_type_cd`, `invoke_limit_times`, `invoke_model`, `create_time`, `status_cd`) VALUES ('102026010825150010', '992023010100000001', '982026010833940009', 'D', '1000', 'S', '2026-01-08 16:07:25', '0');
INSERT INTO `hc_iot`.`c_route` (`id`, `app_id`, `service_id`, `order_type_cd`, `invoke_limit_times`, `invoke_model`, `create_time`, `status_cd`) VALUES ('102026010829850008', '992023010100000001', '982026010849220007', 'Q', '1000', 'S', '2026-01-08 16:07:01', '0');
INSERT INTO `hc_iot`.`c_route` (`id`, `app_id`, `service_id`, `order_type_cd`, `invoke_limit_times`, `invoke_model`, `create_time`, `status_cd`) VALUES ('102026010832310006', '992023010100000001', '982026010873860005', 'D', '1000', 'S', '2026-01-08 16:06:35', '0');
INSERT INTO `hc_iot`.`c_service` (`service_id`, `service_code`, `business_type_cd`, `name`, `seq`, `is_instance`, `url`, `method`, `timeout`, `retry_count`, `create_time`, `status_cd`) VALUES ('982026010876460011', 'car.updateTempInnerCar', 'API', '修改内部临时车', '1', 'CMD', 'http://iot-car', 'GET', '60', '3', '2026-01-08 16:07:56', '0');
INSERT INTO `hc_iot`.`c_service` (`service_id`, `service_code`, `business_type_cd`, `name`, `seq`, `is_instance`, `url`, `method`, `timeout`, `retry_count`, `create_time`, `status_cd`) VALUES ('982026010833940009', 'car.saveTempInnerCar', 'API', '保存内部临时车', '1', 'CMD', 'http://iot-car', 'POST', '60', '3', '2026-01-08 16:07:24', '0');
INSERT INTO `hc_iot`.`c_service` (`service_id`, `service_code`, `business_type_cd`, `name`, `seq`, `is_instance`, `url`, `method`, `timeout`, `retry_count`, `create_time`, `status_cd`) VALUES ('982026010849220007', 'car.listTempInnerCars', 'API', '查询内部临时车', '1', 'CMD', 'http://iot-car', 'GET', '60', '3', '2026-01-08 16:07:00', '0');
INSERT INTO `hc_iot`.`c_service` (`service_id`, `service_code`, `business_type_cd`, `name`, `seq`, `is_instance`, `url`, `method`, `timeout`, `retry_count`, `create_time`, `status_cd`) VALUES ('982026010873860005', 'car.deleteTempInnerCar', 'API', '删除内部临时车', '1', 'CMD', 'http://iot-car', 'POST', '60', '3', '2026-01-08 16:06:35', '0');
INSERT INTO `hc_iot`.`m_menu` (`m_id`, `name`, `g_id`, `url`, `seq`, `p_id`, `description`, `create_time`, `status_cd`, `is_show`) VALUES ('702026010862210002', '内部临时车', '802023082372950010', '/#/pages/car/tempInnerCar', '12', '502026010844150003', '', '2026-01-08 16:05:05', '0', 'Y');
INSERT INTO `hc_iot`.`p_privilege` (`p_id`, `name`, `description`, `create_time`, `status_cd`, `domain`, `resource`, `m_id`) VALUES ('502026010844150003', '内部临时车', '', '2026-01-08 16:05:05', '0', '800900000003', '/#/pages/car/tempInnerCar', '702026010862210002');
INSERT INTO `hc_iot`.`p_privilege_rel` (`rel_id`, `p_id`, `pg_id`, `level_cd`, `create_time`, `status_cd`) VALUES ('102026010828670004', '502026010844150003', '600201904002', '1001', '2026-01-08 16:05:05', '0');
CREATE TABLE `temp_inner_car` (
  `tic_id` varchar(30) NOT NULL COMMENT '编号',
  `car_num` varchar(12) NOT NULL COMMENT '车牌号',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `config_id` varchar(30) NOT NULL COMMENT '收费标准ID',
  `fee_name` varchar(64) NOT NULL COMMENT '收费名称',
  `community_id` varchar(30) NOT NULL COMMENT '小区ID',
  `pa_id` varchar(30) NOT NULL COMMENT '停车场ID',
  `pa_num` varchar(12) NOT NULL COMMENT '停车场编号',
  `remark` varchar(512)  COMMENT '说明',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，S 保存，0, 在用 1失效'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

### 2026-01-15 停车场加入异常入场
```sql
INSERT INTO `p_privilege` VALUES ('502024072439940003', '异常入场', '', '2024-07-24 19:39:38', '0', '800900000003', '/iot/api/carInout.exceptionCarOut', '702023092199790001');
INSERT INTO `p_privilege_rel` VALUES ('102024072471860004', '502024072439940003', '600201904002', '1001', '2024-07-24 19:39:38', '0');
```

### 2026-05-27 门禁加入精度维度
```sql
alter table access_control add lat varchar(32) NOT NULL DEFAULT '39.907881821019075'COMMENT '经度';
alter table access_control add lng varchar(32) NOT NULL DEFAULT '116.39803241981451'COMMENT '纬度';
```

### 2026-06-27 物联网修改为滑动验证码
```sql
INSERT INTO `c_service` VALUES ('982026062700010001', 'login.generatorSliderCode', 'API', '滑块验证码接口', '1', 'CMD', 'http://iot-user', 'GET', '60', '3', '2026-06-27 11:35:35', '0');
INSERT INTO `c_route` VALUES ('102026062700010001', '992023010100000001', '982026062700010001', 'Q', '1000', 'S', '2026-06-27 11:35:35', '0');
```

### 2026-07-24 无牌车扫码自动生成车牌号
```sql
CREATE TABLE `unlicensed_car_plate` (
  `plate_id` varchar(30) NOT NULL COMMENT '主键ID',
  `open_id` varchar(200) NOT NULL COMMENT '微信/支付宝 openId',
  `car_num` varchar(16) NOT NULL COMMENT '自动生成的车牌号',
  `community_id` varchar(30) DEFAULT NULL COMMENT '小区ID',
  `open_type` varchar(10) DEFAULT 'WECHAT' COMMENT '开放平台类型 WECHAT/ALIPAY',
  `status_cd` varchar(2) DEFAULT '0' COMMENT '状态 0有效 1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`plate_id`),
  KEY `idx_open_id` (`open_id`),
  KEY `idx_car_num` (`car_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='无牌车虚拟车牌号映射表';

-- c_service 注册（service_id 需用 GenerateCodeFactory 生成后替换）
INSERT INTO `hc_iot`.`c_service` (`service_id`, `service_code`, `business_type_cd`, `name`, `seq`, `is_instance`, `url`, `method`, `timeout`, `retry_count`, `create_time`, `status_cd`) VALUES ('982026072424600009', 'car.getOrGenerateUnlicensedCarNum', 'API', '生成无牌车车牌号', '1', 'CMD', 'http://iot-car', 'GET', '60', '3', '2026-07-24 15:47:38', '0');

-- c_route 注册（关联到物联网用户 appId: 992023111817510002）
INSERT INTO `hc_iot`.`c_route` (`id`, `app_id`, `service_id`, `order_type_cd`, `invoke_limit_times`, `invoke_model`, `create_time`, `status_cd`) VALUES ('102026072463810012', '992023111817510002', '982026072424600009', 'Q', '1000', 'S', '2026-07-24 15:49:32', '0');

```

### 2026-07-24 车辆出入场记录历史归档
```sql
-- 创建历史表
CREATE TABLE `car_inout_his` LIKE `car_inout`;
CREATE INDEX idx_car_inout_his_car_num ON car_inout_his(car_num);
CREATE INDEX idx_car_inout_his_community ON car_inout_his(community_id);
CREATE INDEX idx_car_inout_his_open_time ON car_inout_his(open_time);
CREATE INDEX idx_car_inout_his_create_time ON car_inout_his(create_time);

-- c_service 历史查询
INSERT INTO `hc_iot`.`c_service` (`service_id`, `service_code`, `business_type_cd`, `name`, `seq`, `is_instance`, `url`, `method`, `timeout`, `retry_count`, `create_time`, `status_cd`) VALUES ('982026072400000001', 'carInoutHis.listCarInoutHis', 'API', '车辆出入历史记录查询', '1', 'CMD', 'http://iot-barrier', 'GET', '60', '3', now(), '0');
-- c_service 历史删除
INSERT INTO `hc_iot`.`c_service` (`service_id`, `service_code`, `business_type_cd`, `name`, `seq`, `is_instance`, `url`, `method`, `timeout`, `retry_count`, `create_time`, `status_cd`) VALUES ('982026072400000002', 'carInoutHis.deleteCarInoutHis', 'API', '车辆出入历史记录删除', '1', 'CMD', 'http://iot-barrier', 'POST', '60', '3', now(), '0');

-- c_route 历史查询路由
INSERT INTO `hc_iot`.`c_route` (`id`, `app_id`, `service_id`, `order_type_cd`, `invoke_limit_times`, `invoke_model`, `create_time`, `status_cd`) VALUES ('102026072400000001', '992023010100000001', '982026072400000001', 'Q', '1000', 'S', now(), '0');
-- c_route 历史删除路由
INSERT INTO `hc_iot`.`c_route` (`id`, `app_id`, `service_id`, `order_type_cd`, `invoke_limit_times`, `invoke_model`, `create_time`, `status_cd`) VALUES ('102026072400000002', '992023010100000001', '982026072400000002', 'D', '1000', 'S', now(), '0');
INSERT INTO `hc_iot`.`task_template` (`template_id`, `template_name`, `template_desc`, `create_time`, `status_cd`, `class_bean`) VALUES ('3', '车辆进场转移历史', '车辆进场转移历史', '2026-07-27 08:42:54', '0', 'carInoutHistoryMigrateTask');
INSERT INTO `hc_iot`.`task` (`task_id`, `b_id`, `task_name`, `template_id`, `task_cron`, `state`, `status_cd`, `create_time`) VALUES ('522026072758420187', '-1', '车辆进场转移历史', '3', '0 30 3 * * ?', '001', '0', '2026-07-27 08:44:00');
INSERT INTO `hc_iot`.`m_menu` (`m_id`, `name`, `g_id`, `url`, `seq`, `p_id`, `description`, `create_time`, `status_cd`, `is_show`) VALUES ('702026072731800001', '历史进出', '802023082372950010', '/#/pages/car/listCarInoutHis', '13', '502026072706260002', '', '2026-07-27 09:02:56', '0', 'Y');
INSERT INTO `hc_iot`.`p_privilege` (`p_id`, `name`, `description`, `create_time`, `status_cd`, `domain`, `resource`, `m_id`) VALUES ('502026072706260002', '历史进出', '', '2026-07-27 09:02:56', '0', '800900000003', '/#/views/car/listCarInoutHis', '702026072731800001');
INSERT INTO `hc_iot`.`p_privilege_rel` (`rel_id`, `p_id`, `pg_id`, `level_cd`, `create_time`, `status_cd`) VALUES ('102026072743270003', '502026072706260002', '600201904002', '1001', '2026-07-27 09:02:56', '0');

```

## 2026-07-28 - 逃费检测功能

### 场景说明

**逃费检测**：车辆出厂失败（未缴费/跟车）时触发尾随逃费检测，若后续有其他车辆在同一岗亭正常出厂，则判定前车为尾随逃费，自动生成逃费记录。

**无进场记录补录**：车辆出厂时查不到进场记录的，系统根据历史出场数据推算进场时间并补录虚拟进场记录，避免因缺失进场数据导致无法计费。

**逃费补缴**：用户支付临停费时，前端展示该车未处理的逃费记录，用户可勾选一并支付。支付完成后逃费记录状态变更为"已处理"。

### DDL

```sql
-- 逃费检测配置表
CREATE TABLE `car_inout_evasion_config` (
  `config_id` varchar(30) NOT NULL COMMENT '配置ID',
  `community_id` varchar(30) NOT NULL COMMENT '小区ID',
  `evasion_open` varchar(2) NOT NULL DEFAULT 'N' COMMENT '是否开启逃费检测 Y/N',
  `query_days` varchar(4) NOT NULL DEFAULT '1' COMMENT '查询天数',
  `query_minutes` varchar(4) NOT NULL DEFAULT '30' COMMENT '停车时长阈值（分钟）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '状态 0在用 1失效',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='逃费检测配置表';

-- 逃费记录表
CREATE TABLE `car_inout_evasion` (
  `evasion_id` varchar(30) NOT NULL COMMENT '逃费记录ID',
  `car_num` varchar(16) NOT NULL COMMENT '车牌号',
  `community_id` varchar(30) NOT NULL COMMENT '小区ID',
  `pa_id` varchar(30) NOT NULL COMMENT '停车场ID',
  `pa_num` varchar(12) NOT NULL COMMENT '停车场编号',
  `box_id` varchar(30) DEFAULT NULL COMMENT '岗亭ID',
  `inout_id` varchar(30) DEFAULT NULL COMMENT '出场记录ID',
  `ci_id` varchar(30) DEFAULT NULL COMMENT '进场记录ID',
  `in_time` datetime DEFAULT NULL COMMENT '进场时间',
  `evasion_time` datetime NOT NULL COMMENT '逃费时间',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '应缴金额',
  `state` varchar(2) NOT NULL DEFAULT 'W' COMMENT '状态 W待处理 D已处理',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `photo_jpg` varchar(500) DEFAULT NULL COMMENT '抓拍图片URL',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '状态 0在用 1失效',
  PRIMARY KEY (`evasion_id`),
  KEY `idx_car_num` (`car_num`),
  KEY `idx_community_id` (`community_id`),
  KEY `idx_evasion_time` (`evasion_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='车辆逃费记录表';

-- car_inout_payment 增加 remark 字段
ALTER TABLE `car_inout_payment` ADD COLUMN `remark` varchar(500) DEFAULT NULL COMMENT '备注';
```

### c_service

```sql
INSERT INTO `hc_iot`.`c_service` (`service_id`, `service_code`, `business_type_cd`, `name`, `seq`, `is_instance`, `url`, `method`, `timeout`, `retry_count`, `create_time`, `status_cd`) VALUES ('98202607280000001', 'carInoutEvasionConfig.listCarInoutEvasionConfig', 'API', '查询逃费检测配置', '1', 'CMD', 'http://iot-barrier', 'GET', '60', '3', now(), '0');
INSERT INTO `hc_iot`.`c_service` (`service_id`, `service_code`, `business_type_cd`, `name`, `seq`, `is_instance`, `url`, `method`, `timeout`, `retry_count`, `create_time`, `status_cd`) VALUES ('98202607280000002', 'carInoutEvasionConfig.saveCarInoutEvasionConfig', 'API', '保存逃费检测配置', '1', 'CMD', 'http://iot-barrier', 'POST', '60', '3', now(), '0');
INSERT INTO `hc_iot`.`c_service` (`service_id`, `service_code`, `business_type_cd`, `name`, `seq`, `is_instance`, `url`, `method`, `timeout`, `retry_count`, `create_time`, `status_cd`) VALUES ('98202607280000003', 'carInoutEvasion.listCarInoutEvasion', 'API', '查询车辆逃费记录', '1', 'CMD', 'http://iot-barrier', 'GET', '60', '3', now(), '0');
INSERT INTO `hc_iot`.`c_service` (`service_id`, `service_code`, `business_type_cd`, `name`, `seq`, `is_instance`, `url`, `method`, `timeout`, `retry_count`, `create_time`, `status_cd`) VALUES ('98202607280000004', 'carInoutEvasion.deleteCarInoutEvasion', 'API', '删除车辆逃费记录', '1', 'CMD', 'http://iot-barrier', 'POST', '60', '3', now(), '0');
```

### c_route

```sql
INSERT INTO `hc_iot`.`c_route` (`id`, `app_id`, `service_id`, `order_type_cd`, `invoke_limit_times`, `invoke_model`, `create_time`, `status_cd`) VALUES ('10202607280000001', '992023010100000001', '98202607280000001', 'Q', '1000', 'S', now(), '0');
INSERT INTO `hc_iot`.`c_route` (`id`, `app_id`, `service_id`, `order_type_cd`, `invoke_limit_times`, `invoke_model`, `create_time`, `status_cd`) VALUES ('10202607280000002', '992023010100000001', '98202607280000002', 'D', '1000', 'S', now(), '0');
INSERT INTO `hc_iot`.`c_route` (`id`, `app_id`, `service_id`, `order_type_cd`, `invoke_limit_times`, `invoke_model`, `create_time`, `status_cd`) VALUES ('10202607280000003', '992023010100000001', '98202607280000003', 'Q', '1000', 'S', now(), '0');
INSERT INTO `hc_iot`.`c_route` (`id`, `app_id`, `service_id`, `order_type_cd`, `invoke_limit_times`, `invoke_model`, `create_time`, `status_cd`) VALUES ('10202607280000004', '992023010100000001', '98202607280000004', 'D', '1000', 'S', now(), '0');
```

### 菜单 & 权限

```sql
INSERT INTO `hc_iot`.`m_menu` (`m_id`, `name`, `g_id`, `url`, `seq`, `p_id`, `description`, `create_time`, `status_cd`, `is_show`) VALUES ('702026072836750001', '逃费车辆', '802023082372950010', '/#/pages/car/listCarInoutEvasion', '14', '502026072865190002', '', '2026-07-28 14:50:06', '0', 'Y');

INSERT INTO `hc_iot`.`p_privilege` (`p_id`, `name`, `description`, `create_time`, `status_cd`, `domain`, `resource`, `m_id`) VALUES ('502026072865190002', '逃费车辆', '', '2026-07-28 14:50:06', '0', '800900000003', '/#/pages/car/listCarInoutEvasion', '702026072836750001');
INSERT INTO `hc_iot`.`p_privilege_rel` (`rel_id`, `p_id`, `pg_id`, `level_cd`, `create_time`, `status_cd`) VALUES ('102026072805330003', '502026072865190002', '600201904002', '1001', '2026-07-28 14:50:06', '0');
```

### 2026-07-28 - 道闸摄像头远程管理控制台

```sql
-- c_service
INSERT INTO `hc_iot`.`c_service` (`service_id`, `service_code`, `business_type_cd`, `name`, `seq`, `is_instance`, `url`, `method`, `timeout`, `retry_count`, `create_time`, `status_cd`) VALUES ('98202607281550001', 'barrier.openBarrierCameraConsole', 'API', '道闸摄像头远程管理控制台', '1', 'CMD', 'http://iot-barrier', 'GET', '60', '3', now(), '0');

-- c_route
INSERT INTO `hc_iot`.`c_route` (`id`, `app_id`, `service_id`, `order_type_cd`, `invoke_limit_times`, `invoke_model`, `create_time`, `status_cd`) VALUES ('10202607281550001', '992023010100000001', '98202607281550001', 'Q', '1000', 'S', now(), '0');
INSERT INTO `hc_iot`.`c_mapping_domain` (`id`, `domain`, `domain_name`, `seq`, `remark`, `create_time`, `status_cd`) VALUES ('102026072872520002', 'VZICLOUD', '臻识云', '15', '主要用于 管理相机', '2026-07-28 15:27:18', '0');
INSERT INTO `hc_iot`.`c_mapping` (`id`, `domain`, `name`, `mapping_key`, `value`, `remark`, `create_time`, `status_cd`) VALUES ('102026072861120005', 'VZICLOUD', 'AccessKeySecret', 'AccessKeySecret', 'xx', '', '2026-07-28 15:27:45', '0');
INSERT INTO `hc_iot`.`c_mapping` (`id`, `domain`, `name`, `mapping_key`, `value`, `remark`, `create_time`, `status_cd`) VALUES ('102026072872680004', 'VZICLOUD', 'AccessKey', 'AccessKey', 'xx', '', '2026-07-28 15:27:34', '0');

```

### 2026-07-28 - MQTT下发日志记录与自动清理

```sql
-- MQTT下发日志表
CREATE TABLE `mqtt_send_log` (
  `log_id` varchar(64) NOT NULL COMMENT '主键ID',
  `business_id` varchar(64) DEFAULT NULL COMMENT '业务ID（taskId）',
  `machine_code` varchar(64) DEFAULT NULL COMMENT '相机编码（设备序列号）',
  `car_num` varchar(16) DEFAULT NULL COMMENT '车牌号',
  `cmd_type` varchar(16) NOT NULL COMMENT '指令类型：OPEN_DOOR-开闸 CLOSE_DOOR-关闸 VOICE-语音 TEXT-文字显示 SNAPSHOT-抓拍',
  `topic` varchar(256) DEFAULT NULL COMMENT 'MQTT topic',
  `context` longtext COMMENT '下发内容（JSON）',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_machine_create` (`machine_code`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='MQTT下发日志表';

-- c_service
INSERT INTO `hc_iot`.`c_service` (`service_id`, `service_code`, `business_type_cd`, `name`, `seq`, `is_instance`, `url`, `method`, `timeout`, `retry_count`, `create_time`, `status_cd`) VALUES ('982026072810010001', 'barrier.listMqttSendLog', 'API', 'MQTT下发日志查询', '1', 'CMD', 'http://iot-barrier', 'GET', '60', '3', now(), '0');

-- c_route
INSERT INTO `hc_iot`.`c_route` (`id`, `app_id`, `service_id`, `order_type_cd`, `invoke_limit_times`, `invoke_model`, `create_time`, `status_cd`) VALUES ('102026072810010001', '992023010100000001', '982026072810010001', 'Q', '1000', 'S', now(), '0');
```

### 2026-08-11 - 车辆与停车场关联升级（pa_id/contact/tel）

```sql
ALTER TABLE `owner_car`
  ADD COLUMN `pa_id` varchar(30) NOT NULL DEFAULT '-1' COMMENT '停车场ID' AFTER `ps_id`,
  ADD COLUMN `contact` varchar(64) DEFAULT '' COMMENT '联系人' AFTER `pa_id`,
  ADD COLUMN `tel` varchar(20) DEFAULT '' COMMENT '联系电话' AFTER `contact`,
  ADD KEY `idx_oc_pa_id` (`pa_id`);
```

### 2026-08-13 - 商铺管理功能（位置/商铺/业主关联）

DDL 与 11 条 c_service/c_route 注册 SQL、菜单权限 SQL 已追加到 `document/db/hc_iot.sql` 尾部，此处完整记录变更脚本：

```sql
-- 1. 建表：商铺位置表
CREATE TABLE `shop_location` (
  `location_id` varchar(50) NOT NULL COMMENT '位置ID',
  `location_name` varchar(100) NOT NULL COMMENT '位置名称',
  `parent_id` varchar(50) NOT NULL DEFAULT '-1' COMMENT '父位置ID',
  `level` int NOT NULL DEFAULT 1 COMMENT '层级',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `community_id` varchar(50) NOT NULL COMMENT '小区ID',
  `seq` int NOT NULL DEFAULT 1 COMMENT '排序',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`location_id`),
  KEY `idx_community` (`community_id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商铺位置表';

-- 2. 建表：商铺表
CREATE TABLE `building_shop` (
  `shop_id` varchar(50) NOT NULL COMMENT '商铺ID',
  `shop_num` varchar(50) NOT NULL COMMENT '商铺编号',
  `location_id` varchar(50) DEFAULT NULL COMMENT '位置ID',
  `location_name` varchar(100) DEFAULT NULL COMMENT '位置名称',
  `built_up_area` varchar(20) DEFAULT NULL COMMENT '建筑面积',
  `shop_area` varchar(20) DEFAULT NULL COMMENT '使用面积',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `state` varchar(4) NOT NULL DEFAULT '2008' COMMENT '状态 2006已出租 2007已出售 2008空置',
  `community_id` varchar(50) NOT NULL COMMENT '小区ID',
  `shop_rent` varchar(20) DEFAULT NULL COMMENT '租金',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`shop_id`),
  KEY `idx_community` (`community_id`),
  KEY `idx_location` (`location_id`),
  KEY `idx_shop_num` (`shop_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商铺表';

-- 3. 建表：商铺业主关联表（租赁/出售记录）
CREATE TABLE `building_shop_owner` (
  `shop_owner_id` varchar(50) NOT NULL COMMENT '关联ID',
  `shop_id` varchar(50) NOT NULL COMMENT '商铺ID',
  `owner_id` varchar(50) DEFAULT NULL COMMENT '业主ID',
  `owner_name` varchar(100) DEFAULT NULL COMMENT '业主姓名',
  `link` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `state` varchar(4) DEFAULT NULL COMMENT '状态 2006已出租 2007已出售',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `community_id` varchar(50) NOT NULL COMMENT '小区ID',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '状态',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`shop_owner_id`),
  KEY `idx_shop` (`shop_id`),
  KEY `idx_owner` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商铺业主关联表';

-- 4. c_service 注册 11 条（url = http://iot-community，查询 GET / 写 POST）
INSERT INTO `c_service` VALUES ('982026081300000001', 'shop.listShopLocations', 'API', '查询商铺位置', '1', 'CMD', 'http://iot-community', 'GET', '60', '3', '2026-08-13 12:00:01', '0');
INSERT INTO `c_service` VALUES ('982026081300000002', 'shop.saveShopLocation', 'API', '添加商铺位置', '1', 'CMD', 'http://iot-community', 'POST', '60', '3', '2026-08-13 12:00:02', '0');
INSERT INTO `c_service` VALUES ('982026081300000003', 'shop.updateShopLocation', 'API', '修改商铺位置', '1', 'CMD', 'http://iot-community', 'POST', '60', '3', '2026-08-13 12:00:03', '0');
INSERT INTO `c_service` VALUES ('982026081300000004', 'shop.deleteShopLocation', 'API', '删除商铺位置', '1', 'CMD', 'http://iot-community', 'POST', '60', '3', '2026-08-13 12:00:04', '0');
INSERT INTO `c_service` VALUES ('982026081300000005', 'shop.listBuildingShops', 'API', '查询商铺', '1', 'CMD', 'http://iot-community', 'GET', '60', '3', '2026-08-13 12:00:05', '0');
INSERT INTO `c_service` VALUES ('982026081300000006', 'shop.saveBuildingShop', 'API', '保存商铺', '1', 'CMD', 'http://iot-community', 'POST', '60', '3', '2026-08-13 12:00:06', '0');
INSERT INTO `c_service` VALUES ('982026081300000007', 'shop.updateBuildingShop', 'API', '修改商铺', '1', 'CMD', 'http://iot-community', 'POST', '60', '3', '2026-08-13 12:00:07', '0');
INSERT INTO `c_service` VALUES ('982026081300000008', 'shop.deleteBuildingShop', 'API', '删除商铺', '1', 'CMD', 'http://iot-community', 'POST', '60', '3', '2026-08-13 12:00:08', '0');
INSERT INTO `c_service` VALUES ('982026081300000009', 'shop.listBuildingShopOwners', 'API', '查询商铺业主', '1', 'CMD', 'http://iot-community', 'GET', '60', '3', '2026-08-13 12:00:09', '0');
INSERT INTO `c_service` VALUES ('982026081300000010', 'shop.saveBuildingShopOwner', 'API', '交铺', '1', 'CMD', 'http://iot-community', 'POST', '60', '3', '2026-08-13 12:00:10', '0');
INSERT INTO `c_service` VALUES ('982026081300000011', 'shop.deleteBuildingShopOwner', 'API', '退铺', '1', 'CMD', 'http://iot-community', 'POST', '60', '3', '2026-08-13 12:00:11', '0');

-- 5. c_route 注册 11 条（app_id = 992023010100000001，查询 order_type_cd='Q' / 写 'D'，invoke_model='S'）
INSERT INTO `c_route` VALUES ('102026081300000001', '992023010100000001', '982026081300000001', 'Q', '1000', 'S', '2026-08-13 12:00:01', '0');
INSERT INTO `c_route` VALUES ('102026081300000002', '992023010100000001', '982026081300000002', 'D', '1000', 'S', '2026-08-13 12:00:02', '0');
INSERT INTO `c_route` VALUES ('102026081300000003', '992023010100000001', '982026081300000003', 'D', '1000', 'S', '2026-08-13 12:00:03', '0');
INSERT INTO `c_route` VALUES ('102026081300000004', '992023010100000001', '982026081300000004', 'D', '1000', 'S', '2026-08-13 12:00:04', '0');
INSERT INTO `c_route` VALUES ('102026081300000005', '992023010100000001', '982026081300000005', 'Q', '1000', 'S', '2026-08-13 12:00:05', '0');
INSERT INTO `c_route` VALUES ('102026081300000006', '992023010100000001', '982026081300000006', 'D', '1000', 'S', '2026-08-13 12:00:06', '0');
INSERT INTO `c_route` VALUES ('102026081300000007', '992023010100000001', '982026081300000007', 'D', '1000', 'S', '2026-08-13 12:00:07', '0');
INSERT INTO `c_route` VALUES ('102026081300000008', '992023010100000001', '982026081300000008', 'D', '1000', 'S', '2026-08-13 12:00:08', '0');
INSERT INTO `c_route` VALUES ('102026081300000009', '992023010100000001', '982026081300000009', 'Q', '1000', 'S', '2026-08-13 12:00:09', '0');
INSERT INTO `c_route` VALUES ('102026081300000010', '992023010100000001', '982026081300000010', 'D', '1000', 'S', '2026-08-13 12:00:10', '0');
INSERT INTO `c_route` VALUES ('102026081300000011', '992023010100000001', '982026081300000011', 'D', '1000', 'S', '2026-08-13 12:00:11', '0');

-- 6. 菜单权限：p_privilege + m_menu（挂 802023021604710005 房产组，url=/#/pages/shop/listShopManage）+ p_privilege_rel（600201904002 物业权限组）
INSERT INTO `p_privilege` VALUES ('502026081300000001', '商铺管理', '商铺管理', '2026-08-13 12:00:00', '0', '800900000003', '/iot/api/shop.listBuildingShops', '702026081300000001');
INSERT INTO `m_menu` VALUES ('702026081300000001', '商铺管理', '802023021604710005', '/#/pages/shop/listShopManage', '4', '502026081300000001', '', '2026-08-13 12:00:00', '0', 'Y');
INSERT INTO `p_privilege_rel` VALUES ('102026081300000001', '502026081300000001', '600201904002', '1001', '2026-08-13 12:00:00', '0');

-- 注：shop.updateBuildingShopOwner（第 12 条 c_service/c_route）注册见下一节「商铺功能补齐」
```

### 2026-08-13 - 商铺功能补齐（详情页 5 Tab + updateBuildingShopOwner）

```sql
-- 1. 商铺详情页 5 Tab（业主/房屋/成员/车辆/商铺）仅前端改造，复用现有接口，无新增后端
-- 2. 新增 c_service：shop.updateBuildingShopOwner（修改商铺租赁，只更新 building_shop_owner，不改商铺状态）
INSERT INTO `c_service` VALUES ('982026081300000012', 'shop.updateBuildingShopOwner', 'API', '修改商铺租赁', '1', 'CMD', 'http://iot-community', 'POST', '60', '3', '2026-08-13 14:00:12', '0');
-- 3. 新增 c_route（app_id = 992023010100000001，写操作 order_type_cd='D'）
INSERT INTO `c_route` VALUES ('102026081300000012', '992023010100000001', '982026081300000012', 'D', '1000', 'S', '2026-08-13 14:00:12', '0');
-- 4. 前端：components/shop 新增 ShopDetailOwner/Room/Member/Car/Shop 5 个内嵌组件，
--    views/shop/shopDetail.vue 重构为 5 Tab；listShopManage.vue 树宽拖拽 + 编号/业主链接；AddShop.vue 补齐 shopArea/shopRent 必填 + 位置全路径
-- 5. 详情 Tab 复用接口明细：业主/成员 Tab 走 owner.queryOwners（ownerTypeCd=1001 人员 / 2002 家庭成员），
--    房屋 Tab 走 room.queryRoomsByOwner，车辆 Tab 走 ownerCar.queryOwnerCars，商铺 Tab 走 shop.listBuildingShops（ownerId 过滤）
--    （owner.listOwnerMember 后端无注册，未采用）；RoomServiceDaoImplMapper.getRoomInfoByOwner 补齐 stateName/roomSubTypeName 字典列
```

### 2026-08-17 - 修复组织删除员工报错 `请求发生异常，AppId 没有权限访问 serviceCode = org.deleteOrgRelStaff` 的 bug

```sql
INSERT INTO `c_route` VALUES ('102026081732260102', '992023010100000001', '982026081732260101', 'D', '1000', 'S', '2026-08-17 16:40:00', '0');
INSERT INTO `c_service` VALUES ('982026081732260101', 'org.deleteOrgRelStaff', 'API', '删除组织员工关系', '1', 'CMD', 'http://iot-user', 'POST', '60', '3', '2026-08-17 16:40:00', '0');
```


### 2026-08-18 - 商铺授权门禁（accessControlShop）

```sql
-- 1. 新增商铺门禁授权表
DROP TABLE IF EXISTS `access_control_shop`;
CREATE TABLE `access_control_shop` (
  `acs_id` varchar(30) NOT NULL COMMENT '编号',
  `machine_id` varchar(30) NOT NULL COMMENT '设备ID',
  `community_id` varchar(30) NOT NULL COMMENT '小区ID',
  `location_id` varchar(30) NOT NULL COMMENT '位置ID',
  `location_name` varchar(64) NOT NULL COMMENT '位置名称',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，0在用 1失效',
  PRIMARY KEY (`acs_id`),
  KEY `i_m_community_id` (`community_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- 2. 服务注册（c_service）
INSERT INTO `c_service` VALUES ('982026081800000001', 'accessControlShop.listAccessControlShop', 'API', '查询商铺授权', '1', 'CMD', 'http://iot-accessControl', 'GET', '60', '3', '2026-08-18 12:00:01', '0');
INSERT INTO `c_service` VALUES ('982026081800000002', 'accessControlShop.saveAccessControlShop', 'API', '商铺门禁授权', '1', 'CMD', 'http://iot-accessControl', 'POST', '60', '3', '2026-08-18 12:00:02', '0');
INSERT INTO `c_service` VALUES ('982026081800000003', 'accessControlShop.deleteAccessControlShop', 'API', '删除商铺授权', '1', 'CMD', 'http://iot-accessControl', 'POST', '60', '3', '2026-08-18 12:00:03', '0');
INSERT INTO `c_service` VALUES ('982026081800000004', 'accessControlShop.listAdminAccessControlShop', 'API', '运营访问商铺授权', '1', 'CMD', 'http://iot-accessControl', 'GET', '60', '3', '2026-08-18 12:00:04', '0');
-- 3. 路由注册（c_route，app_id=992023010100000001）
INSERT INTO `c_route` VALUES ('102026081800000001', '992023010100000001', '982026081800000001', 'Q', '1000', 'S', '2026-08-18 12:00:01', '0');
INSERT INTO `c_route` VALUES ('102026081800000002', '992023010100000001', '982026081800000002', 'D', '1000', 'S', '2026-08-18 12:00:02', '0');
INSERT INTO `c_route` VALUES ('102026081800000003', '992023010100000001', '982026081800000003', 'D', '1000', 'S', '2026-08-18 12:00:03', '0');
INSERT INTO `c_route` VALUES ('102026081800000004', '992023010100000001', '982026081800000004', 'Q', '1000', 'S', '2026-08-18 12:00:04', '0');
-- 4. 菜单权限（挂在门禁管理组下）
INSERT INTO `p_privilege` VALUES ('502026081800000001', '商铺授权', '商铺授权', '2026-08-18 12:00:00', '0', '800900000003', '/#/pages/accessControl/accessControlShop', '702026081800000001');
INSERT INTO `m_menu` VALUES ('702026081800000001', '商铺授权', '802023081520910004', '/#/pages/accessControl/accessControlShop', '11', '502026081800000001', '', '2026-08-18 12:00:00', '0', 'Y');
INSERT INTO `p_privilege_rel` VALUES ('102026081800000001', '502026081800000001', '600201904002', '1001', '2026-08-18 12:00:00', '0');
```

### 2026-08-18 水电表支持绑定商铺（对象类型）
```sql
alter table meter_machine add COLUMN `obj_type` varchar(2) NOT NULL DEFAULT '0' COMMENT '对象类型 0 房屋 1 商铺' AFTER `room_name`;
```

### 2026-08-28 停车位租售功能（出租/出售/修改车辆/释放）

```sql
-- 1. parking_space 表新增业主/车辆/租期字段
ALTER TABLE `parking_space`
  ADD COLUMN `owner_id` varchar(30) DEFAULT NULL COMMENT '业主ID' AFTER `pr_id`,
  ADD COLUMN `owner_name` varchar(64) DEFAULT NULL COMMENT '业主名称' AFTER `owner_id`,
  ADD COLUMN `car_id` varchar(30) DEFAULT NULL COMMENT '车辆ID' AFTER `owner_name`,
  ADD COLUMN `car_num` varchar(12) DEFAULT NULL COMMENT '车牌号' AFTER `car_id`,
  ADD COLUMN `start_time` datetime DEFAULT NULL COMMENT '出租开始时间' AFTER `car_num`,
  ADD COLUMN `end_time` datetime DEFAULT NULL COMMENT '出租结束时间' AFTER `start_time`;
-- 2. 服务注册（c_service）
INSERT INTO `c_service` VALUES ('982026082800000001', 'parkingArea.queryParkingAreaAndRegions', 'API', '查询停车场及区域', '1', 'CMD', 'http://iot-car', 'GET', '60', '3', '2026-08-28 10:00:01', '0');
INSERT INTO `c_service` VALUES ('982026082800000002', 'parkingSpace.sellParkingSpace', 'API', '出租/出售车位', '1', 'CMD', 'http://iot-car', 'POST', '60', '3', '2026-08-28 10:00:02', '0');
INSERT INTO `c_service` VALUES ('982026082800000003', 'parkingSpace.releaseParkingSpace', 'API', '释放车位', '1', 'CMD', 'http://iot-car', 'POST', '60', '3', '2026-08-28 10:00:03', '0');
INSERT INTO `c_service` VALUES ('982026082800000004', 'parkingSpace.updateParkingSpaceCar', 'API', '修改车位绑定车辆', '1', 'CMD', 'http://iot-car', 'POST', '60', '3', '2026-08-28 10:00:04', '0');
INSERT INTO `c_service` VALUES ('982026082800000005', 'ownerCar.queryCarsWithoutParkingSpace', 'API', '查询无车位车辆', '1', 'CMD', 'http://iot-car', 'GET', '60', '3', '2026-08-28 10:00:05', '0');
-- 3. 路由注册（c_route，app_id=992023010100000001）
INSERT INTO `c_route` VALUES ('102026082800000001', '992023010100000001', '982026082800000001', 'Q', '1000', 'S', '2026-08-28 10:00:01', '0');
INSERT INTO `c_route` VALUES ('102026082800000002', '992023010100000001', '982026082800000002', 'D', '1000', 'S', '2026-08-28 10:00:02', '0');
INSERT INTO `c_route` VALUES ('102026082800000003', '992023010100000001', '982026082800000003', 'D', '1000', 'S', '2026-08-28 10:00:03', '0');
INSERT INTO `c_route` VALUES ('102026082800000004', '992023010100000001', '982026082800000004', 'D', '1000', 'S', '2026-08-28 10:00:04', '0');
INSERT INTO `c_route` VALUES ('102026082800000005', '992023010100000001', '982026082800000005', 'Q', '1000', 'S', '2026-08-28 10:00:05', '0');

```

### 2026-08-28 业主车辆与车位解耦（owner_car 删除 ps_id）

```sql
-- 1. owner_car 删除车位ID字段及索引（车辆不再绑定车位，只保留 pa_id 关联停车场；车辆-车位绑定由 parking_space.car_id 单向维护）
ALTER TABLE `owner_car` DROP INDEX `i_oc_ps_id`;
ALTER TABLE `owner_car` DROP COLUMN `ps_id`;
-- 2. car_month_order 删除车位ID字段（月卡订单记录车辆购卡，不再写车位）
-- 3. 废弃车辆绑车位/释放车位接口（车辆不再绑车位，释放统一走 parkingSpace.releaseParkingSpace）

```

### 2026-08-28 月卡订单停车场化（car_month_order ps_id → pa_id）

```sql
-- 1. car_month_order 表定义 ps_id（车位ID）改为 pa_id（停车场ID）
-- 若父方案（2026-08-28 业主车辆与车位解耦）ALTER DROP ps_id 尚未执行，执行：
ALTER TABLE `car_month_order` CHANGE COLUMN `ps_id` `pa_id` varchar(30) DEFAULT NULL COMMENT '停车场ID';
-- 若父方案 ALTER DROP ps_id 已执行（ps_id 列已删），则改为执行：
-- ALTER TABLE `car_month_order` ADD COLUMN `pa_id` varchar(30) DEFAULT NULL COMMENT '停车场ID' AFTER `car_id`;
-- 2. 历史订单 ps_id 原为车位ID，无停车场语义，统一置空
UPDATE `car_month_order` SET `pa_id` = NULL WHERE `pa_id` IS NOT NULL;
```

### 2026-08-28 车辆颜色字段下线（business_owner_car 删除 car_color）

```sql
-- 1. business_owner_car 删除颜色字段（owner_car 表文档结构中无 car_color 列）
ALTER TABLE `business_owner_car` DROP COLUMN `car_color`;
-- 2. 若生产环境 owner_car 表存在 car_color 字段，请一并执行：
ALTER TABLE `owner_car` DROP COLUMN `car_color`;
```

### 2026-09-01 充电订单页（chargeOrders）对齐原型 + 真实接口改造

```sql
-- 服务注册（需在运行库执行）
INSERT INTO `c_service` VALUES ('982026090100000001', 'account.queryAppUserAccountDetail', 'API', '手机端查询账户明细', '1', 'CMD', 'http://iot-acct', 'GET', '60', '3', '2026-09-01 10:00:00', '0');
INSERT INTO `c_route` VALUES ('102026090100000001', '992023111817510002', '982026090100000001', 'Q', '1000', 'S', '2026-09-01 10:00:00', '0');
```


### 2026-09-01 系统信息/隐私协议查询接口与「关于页、联系客服」改造

```sql
-- 服务注册（需在运行库执行）
INSERT INTO `c_service` VALUES ('982026090100000002', 'system.listSystemPrivacy', 'API', '查询系统隐私信息', '1', 'CMD', 'http://iot-system', 'GET', '60', '3', '2026-09-01 14:12:52', '0');
INSERT INTO `c_route` VALUES ('102026090100000002', '992023111817510002', '982026090100000002', 'Q', '1000', 'S', '2026-09-01 14:12:52', '0');
```

### 2026-09-01 carService 车辆服务页 UI 间距与车牌输入 Bug 修复

```sql

-- 服务注册（需在运行库执行）
INSERT INTO `c_service` VALUES ('982026090100000003', 'system.listRegisterProtocol', 'API', '查询注册协议', '1', 'CMD', 'http://iot-system', 'GET', '60', '3', '2026-09-01 15:07:07', '0');
INSERT INTO `c_service` VALUES ('982026090100000004', 'system.updateRegisterProtocol', 'API', '修改注册协议', '1', 'CMD', 'http://iot-system', 'POST', '60', '3', '2026-09-01 15:07:07', '0');
INSERT INTO `c_route` VALUES ('102026090100000003', '992023010100000001', '982026090100000003', 'Q', '1000', 'S', '2026-09-01 15:07:07', '0');
INSERT INTO `c_route` VALUES ('102026090100000004', '992023010100000001', '982026090100000004', 'D', '1000', 'S', '2026-09-01 15:07:07', '0');

-- 服务注册（需在运行库执行）
INSERT INTO `c_service` VALUES ('982026090100000005', 'user.updateUserPwd', 'API', '修改登录密码', '1', 'CMD', 'http://iot-user', 'POST', '60', '3', '2026-09-01 16:04:17', '0');
INSERT INTO `c_service` VALUES ('982026090100000006', 'user.resetUserPwd', 'API', '忘记密码重置', '1', 'CMD', 'http://iot-user', 'POST', '60', '3', '2026-09-01 16:04:17', '0');
INSERT INTO `c_route` VALUES ('102026090100000005', '992023111817510002', '982026090100000005', 'D', '1000', 'S', '2026-09-01 16:04:17', '0');
INSERT INTO `c_route` VALUES ('102026090100000006', '992023111817510002', '982026090100000006', 'D', '1000', 'S', '2026-09-01 16:04:17', '0');
```

### 2026-09-02 扫码充电「扫码即打开内容」改造（小程序扫码直达充电页）

```sql
-- 服务注册（需在运行库执行，用户端 appId 992023111817510002）
INSERT INTO `c_service` VALUES ('982026090200000001', 'chargeMachine.getAppChargeMachineByCode', 'API', '按设备编码查询充电桩', '1', 'CMD', 'http://iot-charge', 'GET', '60', '3', '2026-09-02 23:26:45', '0');
INSERT INTO `c_route` VALUES ('102026090200000001', '992023111817510002', '982026090200000001', 'Q', '1000', 'S', '2026-09-02 23:26:45', '0');
```

### 2026-09-03 充电小程序电子发票（invoice_order / invoice_order_item）

```sql
-- 1. 建表 invoice_order（开票订单，通用开票表，充电业务复用）
CREATE TABLE `invoice_order` (
  `invoice_order_id` varchar(30) NOT NULL COMMENT '开票订单ID',
  `user_id`          varchar(30)          COMMENT '用户ID',
  `phone`            varchar(16)  NOT NULL COMMENT '登录账号手机号',
  `community_id`     varchar(30)          COMMENT '小区ID(communityId)',
  `heading_id`       varchar(32)          COMMENT '商城抬头ID(同步后回写)',
  `heading_type`     varchar(8)           COMMENT '抬头类型 个人/公司',
  `buyer_name`       varchar(128)         COMMENT '购买方名称(抬头名称)',
  `buyer_tax_no`     varchar(64)          COMMENT '税号',
  `buyer_address`    varchar(256)         COMMENT '地址(选填)',
  `buyer_bank`       varchar(128)         COMMENT '开户行(选填)',
  `buyer_bank_no`    varchar(64)          COMMENT '银行账号(选填)',
  `email`            varchar(128)         COMMENT '邮箱(接收发票)',
  `invoice_type`     varchar(8)   DEFAULT '普票' COMMENT '普票/专票',
  `biz_category`     varchar(64)  DEFAULT '充电费' COMMENT '业务类目',
  `amount`           decimal(14,2) NOT NULL COMMENT '开票金额(元)',
  `invoice_id`       varchar(32)          COMMENT '商城开票单号(INV..)',
  `state`            varchar(8)   DEFAULT 'WAIT' COMMENT 'WAIT-待开票 DONE-已开票 FAIL-开票失败',
  `invoice_no`       varchar(64)          COMMENT '发票号码',
  `invoice_code`     varchar(64)          COMMENT '发票代码',
  `pdf_url`          varchar(256)         COMMENT '发票PDF地址',
  `fail_reason`      varchar(512)         COMMENT '失败原因',
  `remark`           varchar(512)         COMMENT '幂等标记(业务前缀:orderId1,orderId2 如 CHARGE:...)',
  `status_cd`        varchar(2)   DEFAULT '0' COMMENT '0-正常 1-删除',
  `create_time`      datetime     DEFAULT CURRENT_TIMESTAMP,
  `update_time`      datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`invoice_order_id`),
  KEY idx_user (user_id, phone, status_cd),
  KEY idx_state (state, status_cd)
) COMMENT='开票订单';

-- 2. 建表 invoice_order_item（开票明细）
CREATE TABLE `invoice_order_item` (
  `item_id`          varchar(30) NOT NULL COMMENT '明细ID',
  `invoice_order_id` varchar(30) NOT NULL COMMENT '开票订单ID',
  `order_id`         varchar(64) NOT NULL COMMENT '业务订单ID(充电订单 charge_machine_order.order_id)',
  `community_id`     varchar(30)         COMMENT '小区ID(冗余)',
  `biz_category`     varchar(64) DEFAULT '充电费' COMMENT '业务类目',
  `invoice_category` varchar(64)         COMMENT '开票类目(商城回填)',
  `tax_code`         varchar(32)         COMMENT '税收分类编码',
  `tax_rate`         varchar(8)          COMMENT '税率',
  `amount`           decimal(14,2) NOT NULL COMMENT '明细金额(元)',
  `seq`              int         DEFAULT 1 COMMENT '明细序号',
  `status_cd`        varchar(2)  DEFAULT '0' COMMENT '0-正常 1-删除',
  `create_time`      datetime    DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`item_id`),
  KEY idx_order (order_id, status_cd),
  KEY idx_invoice (invoice_order_id)
) COMMENT='开票明细';

-- 3. 服务注册（用户端 appId 992023111817510002）
INSERT INTO `c_service` VALUES ('982026090300000001', 'invoice.listMyInvoiceOrders', 'API', '查询我的开票订单(未开票/已开票)', '1', 'CMD', 'http://iot-acct', 'GET', '60', '3', '2026-09-03 10:00:01', '0');
INSERT INTO `c_service` VALUES ('982026090300000002', 'invoice.getMyInvoiceHeading', 'API', '按登录手机号查询商城发票抬头', '1', 'CMD', 'http://iot-acct', 'GET', '60', '3', '2026-09-03 10:00:02', '0');
INSERT INTO `c_service` VALUES ('982026090300000003', 'invoice.saveInvoice', 'API', '提交开票申请', '1', 'CMD', 'http://iot-acct', 'POST', '60', '3', '2026-09-03 10:00:03', '0');
INSERT INTO `c_route` VALUES ('102026090300000001', '992023111817510002', '982026090300000001', 'Q', '1000', 'S', '2026-09-03 10:00:01', '0');
INSERT INTO `c_route` VALUES ('102026090300000002', '992023111817510002', '982026090300000002', 'Q', '1000', 'S', '2026-09-03 10:00:02', '0');
INSERT INTO `c_route` VALUES ('102026090300000003', '992023111817510002', '982026090300000003', 'D', '1000', 'S', '2026-09-03 10:00:03', '0');

-- 4. 商城接入配置（c_mapping MALL 域；MALL_SWITCH=ON 才会调用商城，其余为商城接入参数）
INSERT INTO `c_mapping` VALUES ('102026090300000004', 'MALL', '商城开关', 'MALL_SWITCH', 'OFF', 'ON 开启，OFF 关闭', '2026-09-03 10:00:04', '0');
INSERT INTO `c_mapping` VALUES ('102026090300000005', 'MALL', '商城网关地址', 'MALL_URL', 'https://mall.homecommunity.cn', '', '2026-09-03 10:00:05', '0');
INSERT INTO `c_mapping` VALUES ('102026090300000006', 'MALL', '调用方应用ID', 'APP_ID', '', '', '2026-09-03 10:00:06', '0');
INSERT INTO `c_mapping` VALUES ('102026090300000007', 'MALL', '调用方应用秘钥', 'APP_SECRET', '', '', '2026-09-03 10:00:07', '0');
INSERT INTO `c_mapping` VALUES ('102026090300000008', 'MALL', '商城管理员账号', 'MALL_USERNAME', '', '', '2026-09-03 10:00:08', '0');
INSERT INTO `c_mapping` VALUES ('102026090300000009', 'MALL', '商城管理员密码', 'MALL_PASSWORD', '', '', '2026-09-03 10:00:09', '0');
```

> 注：`invoice.saveInvoice`（`982026090300000003` / `102026090300000003`）已于 2026-09-17 随手机端切换到
> `invoice.appOpenInvoice` 一并下线（见 2026-09-17 记录中的 DELETE 语句）。

### 2026-09-07 停车场默认车牌设置（默认省份 + 默认字母）

```sql
-- 1. parking_area 表增加默认省份/默认字母字段
ALTER TABLE `parking_area`
  ADD COLUMN `default_prov` varchar(4) DEFAULT '粤' COMMENT '默认省份（车牌第1位）',
  ADD COLUMN `default_char` varchar(4) DEFAULT 'A' COMMENT '默认字母（车牌第2位）';

-- 2. 服务注册（用户端 appId 992023111817510002，扫码免登录）
INSERT INTO `c_service` VALUES ('982026090700000001', 'parkingArea.getAppParkingArea', 'API', '手机端查询停车场信息', '1', 'CMD', 'http://iot-car', 'GET', '60', '3', '2026-09-07 12:00:00', '0');
INSERT INTO `c_route` VALUES ('102026090700000001', '992023111817510002', '982026090700000001', 'Q', '1000', 'S', '2026-09-07 12:00:00', '0');
```

### 2026-09-08 用户端修改密码改为登录态免验证码

```sql
-- 1. 服务注册（用户端 appId 992023111817510002，需登录态，免原密码/免验证码）
INSERT INTO `c_service` VALUES ('982026090800000001', 'user.appUserModifyPasswd', 'API', '用户端修改密码(登录态免验证码)', '1', 'CMD', 'http://iot-user', 'POST', '60', '3', '2026-09-08 10:58:16', '0');
INSERT INTO `c_route` VALUES ('102026090800000001', '992023111817510002', '982026090800000001', 'D', '1000', 'S', '2026-09-08 10:58:16', '0');
```


### 2026-09-12 业主钱包余额微信原路退款（account.ownerRefundAccount）

```sql
-- 1. 服务注册（用户端 appId 992023111817510002，需登录态）
INSERT INTO `c_service` VALUES ('982026091200000001', 'account.ownerRefundAccount', 'API', '业主账户余额退款', '1', 'CMD', 'http://iot-acct', 'POST', '60', '3', '2026-09-12 13:30:08', '0');
INSERT INTO `c_route` VALUES ('102026091200000001', '992023111817510002', '982026091200000001', 'D', '1000', 'S', '2026-09-12 13:30:08', '0');
```

### 2026-09-14 修复多次预存后余额微信退款报错（REFUND_FEE_MISMATCH）

> 无表结构变更、无服务注册变更（`account.ownerRefundAccount` 已于 2026-09-12 注册）。

**一、根因修复：支付成功回调按主键精确更新（不再全表刷新 online_pay）**

- 缺陷：`WechatPaymentFactoryAdapt#doUpdateOnlinePay`、`ChangKPaymentFactoryAdapt#doUpdateOnlinePay` 只设置 `orderId`，
  而 `updateOnlinePayInfo` 的 WHERE 只支持 `pay_id` → 回调实际执行了无过滤条件的全表 UPDATE，把整张表的
  `order_id`/`transaction_id`/`state` 刷成同一笔订单；多笔预存逐笔退款时每笔取到的 `out_trade_no` 相同，
  第一笔退成功后第二笔变成"对同一微信订单再退一次" → 微信返回 REFUND_FEE_MISMATCH（订单金额或退款金额与之前请求不一致）。
- 修复：回调改为"先按 `orderId` + `state=W` 查出唯一 `payId`（0 条记 warn 不更新；多条取最新一条并告警），再按 `payId` 精确更新"；
  Mapper `updateOnlinePayInfo` 的 SET 移除 `order_id`（商户订单号生成后不可变更）、WHERE 增加 `order_id` 条件；
  `OnlinePayV1InnerServiceSMOImpl#updateOnlinePay` 增加 `payId` 必填校验，从服务层杜绝无过滤条件更新。
- 调用方审计：`updateOnlinePay` 共 4 处调用（微信回调、畅快回调、账户余额退款、充电退款）均已按 `payId` 更新。

**二、退款链路加固（退款单号/金额显式化 + 逐笔闭环）**

- `IRefundMoneyAdapt#refund` 增加退款单参数（复用 `OnlinePayRefundDto`）：`refundId` 作为 `out_refund_no`、`refundFee` 作为
  `refund_fee`，`total_fee` 始终取原订单金额；删除适配器"按 `pay_id`+`state=W` 反查历史退款单"的逻辑（该逻辑会复用历史退款单号与历史金额，
  且反查为空时直接 NPE）；`WechatRefundMoneyAdapt`、`ChangKRefundAdapt` 增加"退款金额 > 0 且不超过原订单金额"校验，
  畅快退款金额由原"按 total_fee 全额退"改为本次退款金额。
- `IReturnMoneyV1InnerServiceSMO#returnMoney` 改为接收 `OnlinePayRefundDto`（Feign 每个方法只支持一个 `@RequestBody`，
  不能同时传支付记录与退款单），SMO 内按 `payId` 反查原支付记录后透传给退款适配器。
- `AccountRefundV1InnerServiceSMOImpl#refundAccountAmount` 重构为"逐笔闭环（退一笔、扣一笔）"：账户级分布式锁（防重复提交）→
  逐笔落 W 退款单 → 调用厂家退款 → 成功后**立即**"扣减账户余额 + 写 `detail_type=3003` 明细（同一调用内生效）→ 扣减该笔 `remaining_fee`
  → 回写退款单 C"，再处理下一笔；任一笔失败即回写 FT（落厂家原始失败原因）并停止，已成功笔不回退，一笔都未退回时抛出失败原因。
- `AppRefundOwnerAccountCmd` 去掉 `@Java110Transactional`（外部资金操作不可回退），不传 `amount` 时按"可退金额（Σ`remaining_fee`）"口径全退，
  金额比较统一 `setScale(2)`；返回结构仍为 `refundId`/`refundFee`，前端无需改动。
- 记账精度：`AccountInnerServiceSMOImpl` 预存/扣款改为 BigDecimal 运算，消除 `200.00000000000003` 之类浮点尾差。
- `ChargeCoreImpl#refundAmount`（充电退款）适配 `returnMoney` 新签名，并修复退款异常时残留 W 退款记录的问题（异常时回写 FT）。

**三、只读核查（不做数据变更）**

历史已被污染的 `online_pay`（同一账号多行 `order_id` 相同、`transaction_id` 重复、`state` 被刷成 C、`remaining_fee` 异常）本期不修复：
代码侧表现为"报错但不产生错误扣款/重复退款"，此类账户走人工退款或另行安排数据修复；上线前后各执行一次只读排查 SQL 记录污染范围
（同一账号多行 `order_id`、`transaction_id` 重复、`remaining_fee > total_fee`、`online_pay_refund` 残留 W、`refund_id` 复用、`c_order` 回退痕迹）。

### 2026-09-17 房产-发票管理（物联网开票 + 发票红冲）


```sql
-- 手机端提交开票（新增，替代 invoice.saveInvoice）
INSERT INTO `c_service` VALUES ('982026091700000001', 'invoice.appOpenInvoice', 'API', '手机端提交开票申请(调商城开票)', '1', 'CMD', 'http://iot-acct', 'POST', '60', '3', '2026-09-17 10:00:01', '0');
INSERT INTO `c_route` VALUES ('102026091700000001', '992023111817510002', '982026091700000001', 'D', '1000', 'S', '2026-09-17 10:00:01', '0');
-- PC 管理端（房产-发票管理，app_id 992023010100000001，查询 Q / 写 D）
INSERT INTO `c_service` VALUES ('982026091700000002', 'invoice.listInvoiceOrders', 'API', '查询开票记录(管理端)', '1', 'CMD', 'http://iot-acct', 'GET', '60', '3', '2026-09-17 10:00:02', '0');
INSERT INTO `c_service` VALUES ('982026091700000003', 'invoice.listUnInvoicedOrders', 'API', '按手机号查询未开票充电订单', '1', 'CMD', 'http://iot-acct', 'GET', '60', '3', '2026-09-17 10:00:03', '0');
INSERT INTO `c_service` VALUES ('982026091700000004', 'invoice.queryInvoiceHeadings', 'API', '按手机号查询商城发票抬头', '1', 'CMD', 'http://iot-acct', 'GET', '60', '3', '2026-09-17 10:00:04', '0');
INSERT INTO `c_service` VALUES ('982026091700000005', 'invoice.saveInvoiceOrder', 'API', '管理端提交开票申请(调商城开票)', '1', 'CMD', 'http://iot-acct', 'POST', '60', '3', '2026-09-17 10:00:05', '0');
INSERT INTO `c_service` VALUES ('982026091700000006', 'invoice.reSubmitInvoice', 'API', '开票失败单重新提交', '1', 'CMD', 'http://iot-acct', 'POST', '60', '3', '2026-09-17 10:00:06', '0');
INSERT INTO `c_service` VALUES ('982026091700000007', 'invoice.deleteInvoiceOrder', 'API', '删除未成功开票记录', '1', 'CMD', 'http://iot-acct', 'POST', '60', '3', '2026-09-17 10:00:07', '0');
INSERT INTO `c_service` VALUES ('982026091700000008', 'invoice.listInvoiceReds', 'API', '查询发票红冲记录(读商城)', '1', 'CMD', 'http://iot-acct', 'GET', '60', '3', '2026-09-17 10:00:08', '0');
INSERT INTO `c_service` VALUES ('982026091700000009', 'invoice.saveInvoiceRed', 'API', '发起发票红冲(商城执行)', '1', 'CMD', 'http://iot-acct', 'POST', '60', '3', '2026-09-17 10:00:09', '0');
INSERT INTO `c_route` VALUES ('102026091700000002', '992023010100000001', '982026091700000002', 'Q', '1000', 'S', '2026-09-17 10:00:02', '0');
INSERT INTO `c_route` VALUES ('102026091700000003', '992023010100000001', '982026091700000003', 'Q', '1000', 'S', '2026-09-17 10:00:03', '0');
INSERT INTO `c_route` VALUES ('102026091700000004', '992023010100000001', '982026091700000004', 'Q', '1000', 'S', '2026-09-17 10:00:04', '0');
INSERT INTO `c_route` VALUES ('102026091700000005', '992023010100000001', '982026091700000005', 'D', '1000', 'S', '2026-09-17 10:00:05', '0');
INSERT INTO `c_route` VALUES ('102026091700000006', '992023010100000001', '982026091700000006', 'D', '1000', 'S', '2026-09-17 10:00:06', '0');
INSERT INTO `c_route` VALUES ('102026091700000007', '992023010100000001', '982026091700000007', 'D', '1000', 'S', '2026-09-17 10:00:07', '0');
INSERT INTO `c_route` VALUES ('102026091700000008', '992023010100000001', '982026091700000008', 'Q', '1000', 'S', '2026-09-17 10:00:08', '0');
INSERT INTO `c_route` VALUES ('102026091700000009', '992023010100000001', '982026091700000009', 'D', '1000', 'S', '2026-09-17 10:00:09', '0');


-- m_menu + p_privilege + p_privilege_rel（600201904002 物业权限组）
INSERT INTO `m_menu` VALUES ('702026091700000001', '发票管理', '802023021604710005', '/#/pages/property/invoiceManage', '6', '502026091700000001', '', '2026-09-17 10:00:00', '0', 'Y');
INSERT INTO `p_privilege` VALUES ('502026091700000001', '发票管理', '', '2026-09-17 10:00:00', '0', '800900000003', '/#/pages/property/invoiceManage', '702026091700000001');
INSERT INTO `p_privilege_rel` VALUES ('102026091700000010', '502026091700000001', '600201904002', '1001', '2026-09-17 10:00:00', '0');
-- m_menu_group_community 无需新增：802023021604710005（房产）已与各小区关联，新增 m_menu 直接生效
```
