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

