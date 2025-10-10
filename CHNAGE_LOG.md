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
