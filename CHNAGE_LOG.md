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

