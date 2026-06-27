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