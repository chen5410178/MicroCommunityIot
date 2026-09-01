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

### 2026-09-01 iot-user首页数据对接真实接口

纯前端改造（无 SQL 变更），涉及 `iot-h5/iot-user`：

1. `api/charge/chargeStore.js`：移除 mock 数据依赖（communities/vehicles/records/myCard/balance/messages 等初始值置空），新增 `machineList`/`machineListCommId` 字段与 `setMachineList()`，`curPiles()`/`pilesByType()` 改为返回接口桩数据（按 chargeType 过滤，无数据返回空数组）；
2. `pages/index/index.vue`：`onShow` 并行加载余额（`account.queryAppUserAccount`）、我的车辆（`car.listUserCar`）、本月充电（`charge.queryPhoneChargeOrder`）、我的月卡（`chargeCard.listUserChargeMonthOrder`）、小区列表（`community.listCommunitys`）、充电桩列表（`chargeMachine.listChargeMachine`）与端口（`chargeMachine.listChargeMachinePort`）；类型切换/小区切换联动刷新并 Toast 反馈；`openPile` 改传 `machineId`；接口失败仅 Toast + 置空对应数据块，不做 mock 兜底；
3. `packageCharge/pages/charge/pileDetail.vue`：`onLoad` 支持按 `machineId` 查找桩，无数据提示"充电桩不存在"并返回。

### 2026-09-01 充电订单页（chargeOrders）对齐原型 + 真实接口改造

纯前端 + iot-charge 小改动（无 SQL 变更，接口已注册 c_service 982025040135040007）：

1. 后端 `iot-charge/.../cmd/charge/QueryPhoneChargeOrderCmd.java`：透传 `state`（在充 1001）、`chargeType`（1001 电瓶车 / 1002 汽车）、`queryStartTime`/`queryEndTime`（时间范围）到查询 Dto（Mapper 已支持对应过滤，仅补 Cmd 透传）；
2. 新增 `iot-h5/iot-user/api/charge/orderAdapter.js`：充电订单接口字段 → 页面字段公共适配函数 `adaptOrder`/`adaptOrderList`（迁移首页 `_adaptOrder` 逻辑，补充 startTime/slot/portName 等，在充卡片复用）；
3. `packageCharge/api/chargeApi.js`：`queryPhoneChargeOrder` 改为返回完整响应 `{code, msg, data, records}`（供订单页取分页总数）；
4. `packageCharge/pages/charge/chargeOrders.vue`：历史订单列表改走真实接口（page/row=100，时间范围 → queryStartTime，类型 → chargeType），统计卡订单数取接口 records、累计消费按接口列表求和；新增「在充订单置顶大卡片」（接口 state=1001 查询，桩名 + 插座/枪号 + 功率标签 + 三列统计 + 停止充电入口）；1s 定时器仅刷新在充时长（Date.now() - startTime），不再调用本地 simulateTick；点击历史订单跳 `orderDetail?orderId=xxx`、在充卡片跳 `orderDetail?mode=running&orderId=xxx`；
5. `packageCharge/pages/charge/orderDetail.vue`：历史订单按 `orderId` 查真实接口展示（订单编号取接口 orderId）；在充模式本地 runningOrder 优先、无本地时按 orderId 查接口只读展示（隐藏停止充电按钮，提示前往「充电中」页面操作）；
6. `pages/index/index.vue`：`_loadMonthOrders` 改用公共 `adaptOrder`，删除本地 `_adaptOrder`/`_fmtTime`/`_pad` 重复实现（行为不变）。

### 2026-09-01 充电订单页 UI/交互修复（3 个问题）

纯前端（`iot-h5/iot-user/packageCharge/pages/charge/chargeOrders.vue`，无 SQL 变更）：

1. 统计卡布局：外层新增 `co-stat-card`（flex + space-between），「全部时间」下拉按钮从统计卡下方移到右侧；
2. 筛选不切换修复：`recRange`/`recType` 由 computed（读非响应式 chargeStore，无法失效）改为 data 本地响应式字段（初始化读 chargeStore），`setRange`/`setType` 同步更新本地字段 + chargeStore + storage，点击「全部/电瓶车/汽车」立即刷新列表与统计；
3. 空态卡片补 `co-mx co-mt` 类，与左右（以及统计卡）产生间距。

### 2026-09-01 业主端账户明细专用接口（account.queryAppUserAccountDetail）

后端 `iot-acct` 新增 Cmd `QueryAppUserAccountDetailCmd`（serviceCode `account.queryAppUserAccountDetail`）：按登录态 userId → 手机号 link → 账户（partId=communityId、acctType 默认 2003）自动定位，一步查询 `account_detail` 明细（支持 detailType 过滤），账户不存在返回空列表，前端无需也不允许传 acctId，杜绝越权。前端 `iot-h5/iot-user` 钱包页（wallet.vue）、账户明细页（myAccountDetail.vue）改调新接口，`myAccount.vue`/`chargeCard.vue` 跳转传 `acctType` 替代 `acctId`，`url.js`/`userApi.js` 中 `queryOwnerAccountDetail` 移除（PC 管理端 iot-webvue 仍用旧接口不受影响）。

```sql
-- 服务注册（需在运行库执行）
INSERT INTO `c_service` VALUES ('982026090100000001', 'account.queryAppUserAccountDetail', 'API', '手机端查询账户明细', '1', 'CMD', 'http://iot-acct', 'GET', '60', '3', '2026-09-01 10:00:00', '0');
INSERT INTO `c_route` VALUES ('102026090100000001', '992023111817510002', '982026090100000001', 'Q', '1000', 'S', '2026-09-01 10:00:00', '0');
```
