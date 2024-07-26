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

### 2024-07-24 道闸加入监控功能

```sql
alter table barrier add COLUMN monitor_id varchar(30) comment '监控ID';
alter table barrier add COLUMN monitor_name varchar(64) comment '监控名称'; 
```

### 2024-07-24 充电桩加入监控功能

```sql
alter table charge_machine add COLUMN monitor_id varchar(30) comment '监控ID';
alter table charge_machine add COLUMN monitor_name varchar(64) comment '监控名称'; 
```

### 2024-07-24 考勤机加入监控功能

```sql
alter table attendance_machine add COLUMN monitor_id varchar(30) comment '监控ID';
alter table attendance_machine add COLUMN monitor_name varchar(64) comment '监控名称'; 
```

### 2024-07-24 电梯加入监控功能

```sql
alter table lift_machine add COLUMN monitor_id varchar(30) comment '监控ID';
alter table lift_machine add COLUMN monitor_name varchar(64) comment '监控名称'; 
```
### 2024-07-24 车位加入监控功能

```sql
alter table parking_space add COLUMN monitor_id varchar(30) comment '监控ID';
alter table parking_space add COLUMN monitor_name varchar(64) comment '监控名称'; 
```

### 2024-07-26 监控加入人员关系
```sql
CREATE TABLE `monitor_machine_person` (
  `mmu_id` varchar(30) NOT NULL COMMENT '监控用户',
  `machine_id` varchar(30) NOT NULL COMMENT '设备ID',
  `machine_name` varchar(200) NOT NULL COMMENT '名称',
  `machine_code` varchar(30) NOT NULL COMMENT '设备编号',
	 person_type varchar(12) not null comment '人员类型，1001 业主 2002 员工',
	 person_id varchar(30) not null comment '人员ID',
   person_name varchar(30) not null comment '人员',
  `community_id` varchar(30) NOT NULL COMMENT '小区ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，S 保存，0, 在用 1失效',
  PRIMARY KEY (`mmu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

