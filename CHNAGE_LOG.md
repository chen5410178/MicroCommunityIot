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
INSERT INTO `hc_iot`.`m_menu` (`m_id`, `name`, `g_id`, `url`, `seq`, `p_id`, `description`, `create_time`, `status_cd`, `is_show`) VALUES ('70202607280000001', '逃费检测设置', '802023082372950010', '/#/pages/car/listCarInoutEvasionConfig', '14', '50202607280000001', '', now(), '0', 'Y');
INSERT INTO `hc_iot`.`m_menu` (`m_id`, `name`, `g_id`, `url`, `seq`, `p_id`, `description`, `create_time`, `status_cd`, `is_show`) VALUES ('70202607280000002', '逃费记录', '802023082372950010', '/#/pages/car/listCarInoutEvasion', '15', '50202607280000002', '', now(), '0', 'Y');

INSERT INTO `hc_iot`.`p_privilege` (`p_id`, `name`, `description`, `create_time`, `status_cd`, `domain`, `resource`, `m_id`) VALUES ('50202607280000001', '逃费检测设置', '', now(), '0', '800900000003', '/#/pages/car/listCarInoutEvasionConfig', '70202607280000001');
INSERT INTO `hc_iot`.`p_privilege` (`p_id`, `name`, `description`, `create_time`, `status_cd`, `domain`, `resource`, `m_id`) VALUES ('50202607280000002', '逃费记录', '', now(), '0', '800900000003', '/#/pages/car/listCarInoutEvasion', '70202607280000002');

INSERT INTO `hc_iot`.`p_privilege_rel` (`rel_id`, `p_id`, `pg_id`, `level_cd`, `create_time`, `status_cd`) VALUES ('10202607280000005', '50202607280000001', '600201904002', '1001', now(), '0');
INSERT INTO `hc_iot`.`p_privilege_rel` (`rel_id`, `p_id`, `pg_id`, `level_cd`, `create_time`, `status_cd`) VALUES ('10202607280000006', '50202607280000002', '600201904002', '1001', now(), '0');
```

### 变更说明

**后端**：
- **新建表** `car_inout_evasion_config`：逃费检测配置（开启开关、查询天数、停车时长阈值）
- **新建表** `car_inout_evasion`：逃费记录（车牌、岗亭、进出时间、金额、状态、抓拍图片）
- **新建引擎** `CarInoutEvasionEngine`：
  - `detectTailgatingEvasion()`：出厂失败时检测尾随逃费，自动生成逃费记录
  - `tryFillEntryAndReQuery()`：无进场记录时补录虚拟进场记录
- **新建Cmd**：
  - `ListCarInoutEvasionConfigCmd`：查询逃费配置
  - `SaveCarInoutEvasionConfigCmd`：保存/修改逃费配置
  - `ListCarInoutEvasionCmd`：查询逃费记录列表
  - `DeleteCarInoutEvasionCmd`：删除逃费记录
- **引擎集成**：
  - `CarOutLogEngine.saveCarOutLog()`：出厂失败后触发逃费检测
  - `OutCarEngine.outParkingArea()`：无进场记录时尝试补录
  - `BarrierEngineImpl`：查询出场记录时补录；支付时处理逃费补缴
- **支付链路**：`TempCarFeeCreateOrderV1InnerServiceSMOImpl` 支持 `evasionIds`/`evasionAmount` 参数
- `CarInoutEvasionPo`：新增 `photoJpg` 字段
- `CarInoutEvasionDto`：新增 `photoJpg` 字段
- `TempCarPayOrderDto`：新增 `evasionIds`、`evasionAmount` 字段
- `CarInoutPaymentPo`：新增 `remark` 字段
- `CarInoutDto`：新增 `boxId` 字段用于岗亭级查询
- Mapper XML 同步更新：`CarInoutEvasionV1ServiceDaoImplMapper.xml`、`CarInoutEvasionConfigV1ServiceDaoImplMapper.xml`、`CarInoutPaymentV1ServiceDaoImplMapper.xml`、`CarInoutV1ServiceDaoImplMapper.xml`

**前端（PC端）**：
- `listCarInoutEvasionConfig.vue`：逃费检测设置页面（开关、查询天数、阈值）
- `listCarInoutEvasion.vue`：逃费记录列表页（含抓拍图片预览列）
- `parkingBox.vue`：岗亭页增加"逃费设置"按钮入口
- `carApi.js`：新增 4 个 API 函数（配置保存/查询、记录查询/删除）
- `carRouter.js`：新增 2 条路由（逃费检测设置、逃费记录）

**前端（H5/小程序）**：
- `tempCarFee.vue`：临停缴费页展示逃费记录列表，支持勾选逃费记录一并支付
