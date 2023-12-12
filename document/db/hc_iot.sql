/*
Navicat MySQL Data Transfer

Source Server         : hc_iot
Source Server Version : 80021
Source Host           : 192.168.100.108:3306
Source Database       : hc_iot

Target Server Type    : MYSQL
Target Server Version : 80021
File Encoding         : 65001

Date: 2023-02-13 01:31:14
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for c_app
-- ----------------------------
DROP TABLE IF EXISTS `c_app`;
CREATE TABLE `c_app` (
  `app_id` varchar(30) NOT NULL COMMENT '应用ID',
  `name` varchar(50) NOT NULL COMMENT '名称 对应系统名称',
  `security_code` varchar(64) DEFAULT NULL COMMENT '签名码 sign签名时用',
  `while_list_ip` varchar(200) DEFAULT NULL COMMENT '白名单ip 多个之间用;隔开',
  `black_list_ip` varchar(200) DEFAULT NULL COMMENT '黑名单ip 多个之间用;隔开',
  `remark` varchar(200) DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  PRIMARY KEY (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='应用ID';

-- ----------------------------
-- Records of c_app
-- ----------------------------
INSERT INTO `c_app` VALUES ('992023010100000001', '物联网web端', '', '', '', '物联网web端', '2019-11-10 23:12:38', '0');
INSERT INTO `c_app` VALUES ('992023021231380002', '测试应用', '', '', '', '1231234', '2023-02-12 14:19:41', '1');

-- ----------------------------
-- Table structure for c_business
-- ----------------------------
DROP TABLE IF EXISTS `c_business`;
CREATE TABLE `c_business` (
  `b_id` varchar(30) NOT NULL COMMENT '业务Id',
  `o_id` varchar(30) NOT NULL COMMENT '订单ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `business_type_cd` varchar(12) DEFAULT NULL,
  `finish_time` date DEFAULT NULL COMMENT '完成时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `status_cd` varchar(2) NOT NULL COMMENT '数据状态，详细参考c_status表',
  UNIQUE KEY `b_id` (`b_id`) USING BTREE,
  KEY `i_cb_o_id` (`o_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of c_business
-- ----------------------------

-- ----------------------------
-- Table structure for c_business_databus
-- ----------------------------
DROP TABLE IF EXISTS `c_business_databus`;
CREATE TABLE `c_business_databus` (
  `databus_id` varchar(30) NOT NULL COMMENT 'databus ID',
  `databus_name` varchar(128) NOT NULL COMMENT '名称',
  `business_type_cd` varchar(255) NOT NULL COMMENT '业务类型',
  `bean_name` varchar(256) NOT NULL COMMENT '适配器',
  `seq` int NOT NULL DEFAULT '1' COMMENT '顺序',
  `state` varchar(12) NOT NULL DEFAULT '1001' COMMENT '1001 代表在用 2002 表示停用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态',
  PRIMARY KEY (`databus_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of c_business_databus
-- ----------------------------

-- ----------------------------
-- Table structure for c_business_table_his
-- ----------------------------
DROP TABLE IF EXISTS `c_business_table_his`;
CREATE TABLE `c_business_table_his` (
  `his_id` varchar(30) NOT NULL COMMENT '历史ID',
  `action` varchar(12) DEFAULT NULL COMMENT '动作表，ADD 添加记录 MOD 修改记录 DEL删除记录',
  `action_obj` varchar(64) NOT NULL COMMENT '动作对象，一般为表名',
  `action_obj_his` varchar(64) NOT NULL COMMENT 'his表',
  `business_type_cd` varchar(64) NOT NULL COMMENT '业务动作',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表',
  UNIQUE KEY `his_id` (`his_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of c_business_table_his
-- ----------------------------
INSERT INTO `c_business_table_his` VALUES ('102023021205810018', 'ADD', 'ss', 'ss', 'abcvb', 'ss', '2023-02-12 20:20:23', '1');

-- ----------------------------
-- Table structure for c_business_type
-- ----------------------------
DROP TABLE IF EXISTS `c_business_type`;
CREATE TABLE `c_business_type` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `business_type_cd` varchar(12) NOT NULL COMMENT '业务项类型',
  `name` varchar(50) NOT NULL COMMENT '名称',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `business_type_cd` (`business_type_cd`) USING BTREE,
  UNIQUE KEY `business_type_cd_2` (`business_type_cd`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=293 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of c_business_type
-- ----------------------------
INSERT INTO `c_business_type` VALUES ('292', 'abc', 'abcn', 'sd', '2023-02-12 20:12:36');

-- ----------------------------
-- Table structure for c_cache
-- ----------------------------
DROP TABLE IF EXISTS `c_cache`;
CREATE TABLE `c_cache` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '缓存ID',
  `cache_code` varchar(10) NOT NULL COMMENT '缓存编码 开始于1001',
  `service_code` varchar(50) NOT NULL COMMENT '调用服务编码 对应 c_service',
  `name` varchar(50) NOT NULL COMMENT '前台显示名称',
  `seq` int NOT NULL COMMENT '列顺序',
  `group` varchar(10) NOT NULL DEFAULT 'COMMON' COMMENT '组，缓存属于哪个组',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  `url` varchar(500) NOT NULL COMMENT '地址',
  PRIMARY KEY (`id`),
  UNIQUE KEY `cache_code` (`cache_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of c_cache
-- ----------------------------
INSERT INTO `c_cache` VALUES ('1', '1001', 'flush.center.cache', '映射缓存（c_mapping表）', '1', 'COMMON', '2018-11-14 13:28:51', '0', 'MAPPING');
INSERT INTO `c_cache` VALUES ('2', '1002', 'flush.center.cache', '业务配置缓存（c_app,c_service,c_route表）', '2', 'COMMON', '2018-11-14 13:28:51', '0', 'APP_ROUTE_SERVICE');
INSERT INTO `c_cache` VALUES ('3', '1003', 'flush.center.cache', '公用服务缓存（c_service_sql表）', '3', 'COMMON', '2018-11-14 13:28:51', '0', 'SERVICE_SQL');
INSERT INTO `c_cache` VALUES ('4', '1004', 'flush.center.cache', '刷新所有缓存（ALL）', '7', 'COMMON', '2019-08-11 05:44:40', '0', 'All');
INSERT INTO `c_cache` VALUES ('5', '1005', 'flush.center.cache', '服务业务信息（c_service_business）', '5', 'COMMON', '2019-08-11 06:00:23', '0', 'SERVICE_BUSINESS');
INSERT INTO `c_cache` VALUES ('6', '1006', 'flush.center.cache', '基础权限信息（p_privilege）', '6', 'COMMON', '2019-08-11 06:00:23', '0', 'PRIVILEGE');
INSERT INTO `c_cache` VALUES ('7', '1007', 'flush.center.cache', 'Databus（business_databus）', '4', 'COMMON', '2019-08-11 06:00:23', '0', 'DATABUS');

-- ----------------------------
-- Table structure for c_mapping
-- ----------------------------
DROP TABLE IF EXISTS `c_mapping`;
CREATE TABLE `c_mapping` (
  `id` varchar(30) NOT NULL COMMENT 'id',
  `domain` varchar(50) NOT NULL COMMENT '域',
  `name` varchar(50) NOT NULL COMMENT '名称',
  `mapping_key` varchar(100) NOT NULL COMMENT 'key',
  `value` varchar(1000) NOT NULL COMMENT 'value',
  `remark` longtext COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of c_mapping
-- ----------------------------
INSERT INTO `c_mapping` VALUES ('102023021308780004', 'DOMAIN.COMMON', '2333', '2', '2', '2', '2023-02-13 01:24:56', '1');
INSERT INTO `c_mapping` VALUES ('102023021357700001', 'ENV_DOMAIN', '环境标识', 'HC_ENV', 'PROD', 'ENV1', '2023-02-13 01:22:20', '0');
INSERT INTO `c_mapping` VALUES ('28', 'DEFAULT_PRIVILEGE_ADMIN', '物业对应的管理员权限组', '800900000003', '600201904002', '物业对应的管理员权限组', '2019-04-01 12:24:36', '0');
INSERT INTO `c_mapping` VALUES ('34', 'DEFAULT_PRIVILEGE_ADMIN', '运营团队对应的管理员权限组', '800900000001', '600201906005', '运营团队对应的管理员权限组', '2019-04-01 12:24:36', '0');
INSERT INTO `c_mapping` VALUES ('37', 'DEFAULT_PRIVILEGE_ADMIN', '开发团队对应的管理员权限组', '800900000000', '600201906007', '开发团队对应的管理员权限组', '2019-04-01 12:24:36', '0');

-- ----------------------------
-- Table structure for c_mapping_domain
-- ----------------------------
DROP TABLE IF EXISTS `c_mapping_domain`;
CREATE TABLE `c_mapping_domain` (
  `id` varchar(30) NOT NULL COMMENT 'id',
  `domain` varchar(50) NOT NULL COMMENT '域',
  `domain_name` varchar(64) NOT NULL COMMENT '配置项',
  `seq` int NOT NULL COMMENT '排序',
  `remark` longtext COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of c_mapping_domain
-- ----------------------------
INSERT INTO `c_mapping_domain` VALUES ('102023021346210019', '1', '1', '1', '1', '2023-02-13 01:06:12', '1');
INSERT INTO `c_mapping_domain` VALUES ('102023021377130023', 'ENV_DOMAIN', '环境配置', '1', '环境相关配置，如演示环境 生产环境 切换', '2023-02-13 01:08:44', '0');
INSERT INTO `c_mapping_domain` VALUES ('102023021380610022', 'SYSTEM_SWITCH', '系统开关', '2', '系统相关开关，比如日志开关，耗时开关等', '2023-02-13 01:08:19', '0');
INSERT INTO `c_mapping_domain` VALUES ('102023021383440013', 'DOMAIN.COMMON', '通用配置', '10', '通用配置，一般很少配置1', '2023-02-13 01:03:08', '0');

-- ----------------------------
-- Table structure for c_order_item
-- ----------------------------
DROP TABLE IF EXISTS `c_order_item`;
CREATE TABLE `c_order_item` (
  `b_id` varchar(30) NOT NULL COMMENT '业务Id',
  `o_id` varchar(30) NOT NULL COMMENT '订单ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `action` varchar(12) DEFAULT NULL COMMENT '动作表，ADD 添加记录 MOD 修改记录 DEL删除记录',
  `action_obj` varchar(64) NOT NULL COMMENT '动作对象，一般为表名',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `status_cd` varchar(2) NOT NULL COMMENT '数据状态，详细参考c_status表',
  UNIQUE KEY `b_id` (`b_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of c_order_item
-- ----------------------------
INSERT INTO `c_order_item` VALUES ('202023021212340042', '102023021271470041', '2023-02-12 23:19:43', 'MOD', 'm_menu_group_catalog', '2023-02-12 23:19:43', null, 'C');
INSERT INTO `c_order_item` VALUES ('202023021214110025', '102023021228030024', '2023-02-12 20:22:53', 'MOD', 'c_business_table_his', '2023-02-12 20:22:55', null, 'C');
INSERT INTO `c_order_item` VALUES ('202023021234900019', '102023021296490018', '2023-02-12 20:20:23', 'ADD', 'c_business_table_his', '2023-02-12 20:20:23', null, 'C');
INSERT INTO `c_order_item` VALUES ('202023021240720032', '102023021280680031', '2023-02-12 20:36:59', 'MOD', 'm_menu_catalog', '2023-02-12 20:37:00', null, 'C');
INSERT INTO `c_order_item` VALUES ('202023021253500022', '102023021297930021', '2023-02-12 20:21:42', 'MOD', 'c_business_table_his', '2023-02-12 20:21:42', null, 'C');
INSERT INTO `c_order_item` VALUES ('202023021253960035', '102023021267850034', '2023-02-12 20:39:03', 'ADD', 'm_menu_catalog', '2023-02-12 20:39:04', null, 'C');
INSERT INTO `c_order_item` VALUES ('202023021287000050', '102023021244850049', '2023-02-12 23:32:44', 'ADD', 'm_menu_catalog', '2023-02-12 23:32:44', null, 'C');
INSERT INTO `c_order_item` VALUES ('202023021293030030', '102023021202770029', '2023-02-12 20:36:44', 'MOD', 'm_menu_catalog', '2023-02-12 20:36:45', null, 'C');
INSERT INTO `c_order_item` VALUES ('202023021295050038', '102023021255710037', '2023-02-12 20:39:57', 'MOD', 'm_menu_catalog', '2023-02-12 20:39:58', null, 'C');
INSERT INTO `c_order_item` VALUES ('202023021310210069', '102023021352500068', '2023-02-13 01:04:15', 'MOD', 'c_mapping_domain', '2023-02-13 01:04:15', null, 'C');
INSERT INTO `c_order_item` VALUES ('202023021332970079', '102023021301050078', '2023-02-13 01:08:43', 'ADD', 'c_mapping_domain', '2023-02-13 01:08:43', null, 'C');
INSERT INTO `c_order_item` VALUES ('202023021333320075', '102023021397120074', '2023-02-13 01:06:59', 'MOD', 'c_mapping_domain', '2023-02-13 01:06:59', null, 'C');
INSERT INTO `c_order_item` VALUES ('202023021337390072', '102023021365380071', '2023-02-13 01:06:11', 'ADD', 'c_mapping_domain', '2023-02-13 01:06:11', null, 'C');
INSERT INTO `c_order_item` VALUES ('202023021352010066', '102023021342120065', '2023-02-13 01:03:07', 'ADD', 'c_mapping_domain', '2023-02-13 01:03:07', null, 'C');
INSERT INTO `c_order_item` VALUES ('202023021388900077', '102023021348070076', '2023-02-13 01:08:18', 'ADD', 'c_mapping_domain', '2023-02-13 01:08:18', null, 'C');

-- ----------------------------
-- Table structure for c_order_type
-- ----------------------------
DROP TABLE IF EXISTS `c_order_type`;
CREATE TABLE `c_order_type` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `order_type_cd` varchar(4) NOT NULL COMMENT '订单类型',
  `name` varchar(50) NOT NULL COMMENT '名称',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_type_cd` (`order_type_cd`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of c_order_type
-- ----------------------------

-- ----------------------------
-- Table structure for c_orders
-- ----------------------------
DROP TABLE IF EXISTS `c_orders`;
CREATE TABLE `c_orders` (
  `o_id` varchar(30) NOT NULL COMMENT '订单ID',
  `app_id` varchar(30) NOT NULL COMMENT '应用ID',
  `ext_transaction_id` varchar(36) DEFAULT NULL,
  `user_id` varchar(30) NOT NULL COMMENT '用户ID',
  `request_time` varchar(16) NOT NULL COMMENT '外部系统请求时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `order_type_cd` varchar(4) NOT NULL COMMENT '订单类型，参考c_order_type表',
  `finish_time` date DEFAULT NULL COMMENT '订单完成时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `status_cd` varchar(2) NOT NULL COMMENT '数据状态，详细参考c_status表',
  UNIQUE KEY `o_id` (`o_id`) USING BTREE,
  UNIQUE KEY `o_id_2` (`o_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of c_orders
-- ----------------------------
INSERT INTO `c_orders` VALUES ('102023021202770029', '992023010100000001', 'd73faf9a-f42b-4ff1-81f6-856a8b23751c', '30520751775595118592', '20230212203642', '2023-02-12 20:36:43', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021204620056', '992023010100000001', 'ac0afefd-cb95-4c64-b008-7fcae0011e3d', '30520751775595118592', '20230212235728', '2023-02-12 23:57:30', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021206310036', '992023010100000001', '5fc8d722-41cb-4c13-87e9-8805abac5a61', '30520751775595118592', '20230212203945', '2023-02-12 20:39:45', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021207870015', '992023010100000001', 'f26a2a48-3589-4654-8aa7-af3b2c03809b', '30520751775595118592', '20230212201503', '2023-02-12 20:15:03', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021217400003', '992023010100000001', 'b5d6d815-0be4-4f38-a0d2-5051f33e5af6', '30520751775595118592', '20230212143241', '2023-02-12 14:32:41', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021218990053', '992023010100000001', 'c5a6aed8-699a-46b1-80b8-4fce7600cf08', '30520751775595118592', '20230212233729', '2023-02-12 23:37:31', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021221710052', '992023010100000001', '5b34ffd3-f761-43a7-b7ad-8f818e947e68', '30520751775595118592', '20230212233630', '2023-02-12 23:36:31', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021223080011', '992023010100000001', '95cc0ff8-0c88-450e-ad84-4c3d8a7d54ca', '30520751775595118592', '20230212200837', '2023-02-12 20:08:38', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021225740008', '992023010100000001', '3ff24797-c793-4fc6-87b4-0b4e3ae27bc1', '30520751775595118592', '20230212152418', '2023-02-12 15:24:18', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021228030024', '992023010100000001', '3db6511d-447b-4efc-88d4-afb15912423c', '30520751775595118592', '20230212202252', '2023-02-12 20:22:52', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021231580012', '992023010100000001', 'c8122588-156f-4488-8453-70fe2fc22c1e', '30520751775595118592', '20230212201104', '2023-02-12 20:11:04', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021237420054', '992023010100000001', '4a285c7a-a9b7-4050-b183-b9a72d323194', '30520751775595118592', '20230212234408', '2023-02-12 23:44:10', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021239010045', '992023010100000001', '5742c25d-8ff1-4d82-84dc-ab9ce150523a', '30520751775595118592', '20230212232332', '2023-02-12 23:23:33', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021240540016', '992023010100000001', 'd277369f-ce51-40ed-8529-241e8da04882', '30520751775595118592', '20230212201853', '2023-02-12 20:18:54', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021242560040', '992023010100000001', 'a8551a52-936b-4443-8a1d-b3dc393cfaaf', '30520751775595118592', '20230212231922', '2023-02-12 23:19:24', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021243230051', '992023010100000001', 'bb3214f7-9a6a-4790-9521-74e9985039ca', '30520751775595118592', '20230212233543', '2023-02-12 23:35:44', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021244850049', '992023010100000001', '4b66f6dc-e81e-479a-904d-7c93b230506b', '30520751775595118592', '20230212233242', '2023-02-12 23:32:43', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021245310009', '992023010100000001', '784c23a9-e48f-40ea-837f-c0e503e97e83', '30520751775595118592', '20230212153714', '2023-02-12 15:37:14', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021247920043', '992023010100000001', '52365114-ddb4-4404-9c97-b10fe9c864df', '30520751775595118592', '20230212232038', '2023-02-12 23:20:39', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021251540044', '992023010100000001', '616030cd-20cd-4acf-ab4c-865ef8b60ca7', '30520751775595118592', '20230212232130', '2023-02-12 23:21:31', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021252790046', '992023010100000001', '253c2132-3f31-4434-9ad0-bd36bb45ed47', '30520751775595118592', '20230212232346', '2023-02-12 23:23:47', 'D', null, null, 'S');
INSERT INTO `c_orders` VALUES ('102023021255710037', '992023010100000001', '4359e722-b1ef-41cd-8aa1-d061835331c9', '30520751775595118592', '20230212203955', '2023-02-12 20:39:56', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021260140028', '992023010100000001', 'f2110f2e-0739-4f51-81f2-f0a4da69801f', '30520751775595118592', '20230212203239', '2023-02-12 20:32:40', 'D', null, null, 'S');
INSERT INTO `c_orders` VALUES ('102023021263830020', '992023010100000001', 'a1a3a3e9-d690-4aee-ba14-b8b0dffcfc0e', '30520751775595118592', '20230212202126', '2023-02-12 20:21:26', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021264450013', '992023010100000001', '5b8b6c52-0fdf-43e9-ad88-b96f86a22ae6', '30520751775595118592', '20230212201220', '2023-02-12 20:12:21', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021267590047', '992023010100000001', 'f218e8e1-8f3f-4c66-8f3a-9beae21aeec1', '30520751775595118592', '20230212232634', '2023-02-12 23:26:36', 'D', null, null, 'S');
INSERT INTO `c_orders` VALUES ('102023021267850034', '992023010100000001', 'e88fb9b5-852b-458d-80c2-52b3bb178f17', '30520751775595118592', '20230212203902', '2023-02-12 20:39:02', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021268120048', '992023010100000001', '5ad8ad59-5fe6-43f3-8c40-79e35674aa76', '30520751775595118592', '20230212233147', '2023-02-12 23:31:48', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021269110055', '992023010100000001', '2bb50c59-01d6-4582-ad29-5bd952fb299f', '30520751775595118592', '20230212235608', '2023-02-12 23:56:10', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021271180010', '992023010100000001', 'abeed866-3be4-4e34-8b28-34edec8c91c6', '30520751775595118592', '20230212160534', '2023-02-12 16:05:34', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021271470041', '992023010100000001', '20d35a30-c897-41ae-86ae-cb98eb9de3ea', '30520751775595118592', '20230212231941', '2023-02-12 23:19:42', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021271610014', '992023010100000001', 'c407920c-f2ee-44ea-8ee6-03376a76b422', '30520751775595118592', '20230212201409', '2023-02-12 20:14:10', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021271900023', '992023010100000001', 'c3540dcb-9283-4346-a3d0-6359300977d6', '30520751775595118592', '20230212202239', '2023-02-12 20:22:39', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021272500005', '992023010100000001', 'c2aeb304-0ea0-47ca-9677-6f9967773b52', '30520751775595118592', '20230212145521', '2023-02-12 14:55:21', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021275010004', '992023010100000001', 'b9aa2aa3-0390-4d4a-83f2-879d468e961a', '30520751775595118592', '20230212143635', '2023-02-12 14:36:34', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021275450017', '992023010100000001', 'cc784a3a-b3e2-4d2a-8b4e-7a7e55661f0d', '30520751775595118592', '20230212202011', '2023-02-12 20:20:12', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021277530006', '992023010100000001', '1311ed57-71ee-4d90-8e43-9519546dd3fa', '30520751775595118592', '20230212151234', '2023-02-12 15:12:34', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021280680031', '992023010100000001', 'aaadb95d-c0e3-4177-87c9-3540748015d2', '30520751775595118592', '20230212203657', '2023-02-12 20:36:58', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021283320001', '992023010100000001', '0e4da57a-5c8a-4cd6-826e-c2d4d9f3c7d1', '30520751775595118592', '20230212141414', '2023-02-12 14:14:15', 'D', null, null, 'S');
INSERT INTO `c_orders` VALUES ('102023021284060039', '992023010100000001', 'cf128cd1-0df9-40fd-afcd-f9d60ec40ec9', '30520751775595118592', '20230212204044', '2023-02-12 20:40:45', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021285640007', '992023010100000001', '5161eed5-20c7-49e9-9868-fddeebd4b725', '30520751775595118592', '20230212152414', '2023-02-12 15:24:14', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021290120002', '992023010100000001', 'e64d48cb-d066-4956-8ae7-2c8d386a6273', '30520751775595118592', '20230212141859', '2023-02-12 14:18:58', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021292590026', '992023010100000001', 'ec0fde43-3b73-4536-9251-cdc8527e5178', '30520751775595118592', '20230212203016', '2023-02-12 20:30:16', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021293700027', '992023010100000001', '57eeb95f-6bdb-44fe-886e-673ce5c15003', '30520751775595118592', '20230212203227', '2023-02-12 20:32:28', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021294390033', '992023010100000001', '075ddeff-9684-4957-9350-7f9e173a2659', '30520751775595118592', '20230212203848', '2023-02-12 20:38:49', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021296490018', '992023010100000001', '2a0795c4-d6d5-4935-8f4f-cefcaad74185', '30520751775595118592', '20230212202022', '2023-02-12 20:20:22', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021297930021', '992023010100000001', '9a4f7729-f2ae-422a-b003-6cd057d45031', '30520751775595118592', '20230212202140', '2023-02-12 20:21:41', 'D', '2023-02-12', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021301050078', '992023010100000001', 'b590981b-8816-421e-8d18-3c423ed3671a', '30520751775595118592', '20230213010841', '2023-02-13 01:08:42', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021302250070', '992023010100000001', 'e885910e-613c-4ff4-8c88-ed7cb1d5bc5f', '30520751775595118592', '20230213010547', '2023-02-13 01:05:48', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021310800058', '992023010100000001', 'a3478762-ec0d-4c15-afb2-f32d932f3758', '30520751775595118592', '20230213003837', '2023-02-13 00:38:39', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021312430082', '992023010100000001', '7bd7ceb7-f492-40ad-b581-82da90271963', '30520751775595118592', '20230213012539', '2023-02-13 01:25:40', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021315320081', '992023010100000001', '4acc4788-d721-4064-9405-c18f068a3fbe', '30520751775595118592', '20230213012423', '2023-02-13 01:24:25', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021316540067', '992023010100000001', '53c13366-66b3-4f45-8aae-b03f2342ecbf', '30520751775595118592', '20230213010348', '2023-02-13 01:03:50', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021328910062', '992023010100000001', 'cc408b84-0a05-468b-8cab-5441dd69e36e', '30520751775595118592', '20230213010006', '2023-02-13 01:00:07', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021341220083', '992023010100000001', '180cc0af-6ec0-4acc-8c23-62de86cf5019', '30520751775595118592', '20230213012855', '2023-02-13 01:28:57', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021342120065', '992023010100000001', '2f59dcec-bde4-4683-8f47-0ed8a873898b', '30520751775595118592', '20230213010305', '2023-02-13 01:03:06', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021348070076', '992023010100000001', 'f013b8bd-d4e6-4bf0-8b46-352ffdfa154e', '30520751775595118592', '20230213010816', '2023-02-13 01:08:18', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021350720059', '992023010100000001', '4797d14b-dce0-472d-8795-c7426c189ba4', '30520751775595118592', '20230213004009', '2023-02-13 00:40:10', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021352500068', '992023010100000001', 'e4d0adb9-8bb3-46c2-879d-4eb59b5e3e08', '30520751775595118592', '20230213010413', '2023-02-13 01:04:14', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021359780064', '992023010100000001', '984bbb7b-50fc-42b6-8689-67dc702fc7d2', '30520751775595118592', '20230213010257', '2023-02-13 01:02:59', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021365380071', '992023010100000001', '8609a18f-24af-49d8-8267-ce0e8e08af89', '30520751775595118592', '20230213010609', '2023-02-13 01:06:10', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021365520057', '992023010100000001', 'dfda2742-de79-49a8-8865-67c72a22d8be', '30520751775595118592', '20230213000306', '2023-02-13 00:03:07', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021366400080', '992023010100000001', '125662e4-0aa2-4bcd-8945-ebe9d19b7fe6', '30520751775595118592', '20230213011009', '2023-02-13 01:10:11', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021372540063', '992023010100000001', 'ecc61846-962c-4131-9896-0bb71d7d69f4', '30520751775595118592', '20230213010143', '2023-02-13 01:01:44', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021383360073', '992023010100000001', 'cea77b15-8d6f-440c-b1e2-cfe6e2c2661a', '30520751775595118592', '20230213010646', '2023-02-13 01:06:47', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021386600060', '992023010100000001', '6a32fa9e-ecd9-441a-8b7c-cc61bd4facdb', '30520751775595118592', '20230213004346', '2023-02-13 00:43:47', 'D', '2023-02-13', null, 'C');
INSERT INTO `c_orders` VALUES ('102023021394090061', '992023010100000001', 'a1c300d1-8e1d-419b-9f93-a6ec87711f6c', '30520751775595118592', '20230213005917', '2023-02-13 00:59:18', 'D', null, null, 'S');
INSERT INTO `c_orders` VALUES ('102023021397120074', '992023010100000001', 'c37e18e0-011d-4292-9d32-05c416a722b0', '30520751775595118592', '20230213010657', '2023-02-13 01:06:58', 'D', '2023-02-13', null, 'C');

-- ----------------------------
-- Table structure for c_route
-- ----------------------------
DROP TABLE IF EXISTS `c_route`;
CREATE TABLE `c_route` (
  `id` varchar(30) NOT NULL COMMENT 'id',
  `app_id` varchar(30) NOT NULL COMMENT '应用ID',
  `service_id` varchar(30) NOT NULL COMMENT '服务ID',
  `order_type_cd` varchar(4) NOT NULL COMMENT '订单类型，参考c_order_type表',
  `invoke_limit_times` int DEFAULT NULL COMMENT '接口调用一分钟调用次数',
  `invoke_model` varchar(1) NOT NULL COMMENT '1-同步方式 2-异步方式',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效，2 表示下线（当组件调用服务超过限制时自动下线）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of c_route
-- ----------------------------
INSERT INTO `c_route` VALUES ('1', '992023010100000001', '1', 'Q', '100', 'S', '2023-02-11 16:26:01', '0');
INSERT INTO `c_route` VALUES ('10', '992023010100000001', '10', 'Q', '100', 'S', '2023-02-11 16:26:02', '0');
INSERT INTO `c_route` VALUES ('102023021211350036', '992023010100000001', '982023021223650035', 'Q', '1000', 'S', '2023-02-12 23:20:33', '0');
INSERT INTO `c_route` VALUES ('102023021225690038', '992023010100000001', '982023021203470037', 'D', '1000', 'S', '2023-02-12 23:23:28', '0');
INSERT INTO `c_route` VALUES ('102023021230340022', '992023010100000001', '982023021223940021', 'D', '1000', 'S', '2023-02-12 20:22:35', '0');
INSERT INTO `c_route` VALUES ('102023021232260003', '992023010100000001', '982023021258190002', 'D', '1000', 'S', '2023-02-12 19:42:30', '0');
INSERT INTO `c_route` VALUES ('102023021238440032', '992023010100000001', '982023021214060031', 'Q', '1000', 'S', '2023-02-12 20:40:39', '0');
INSERT INTO `c_route` VALUES ('102023021240560020', '992023010100000001', '982023021227650019', 'D', '1000', 'S', '2023-02-12 20:21:16', '0');
INSERT INTO `c_route` VALUES ('102023021242430005', '992023010100000001', '982023021248040004', 'D', '1000', 'S', '2023-02-12 23:35:41', '0');
INSERT INTO `c_route` VALUES ('102023021249110013', '992023010100000001', '982023021204510012', 'Q', '1000', 'S', '2023-02-12 23:44:04', '0');
INSERT INTO `c_route` VALUES ('102023021251050011', '992023010100000001', '982023021273350010', 'D', '1000', 'S', '2023-02-12 20:13:57', '0');
INSERT INTO `c_route` VALUES ('102023021253270005', '992023010100000001', '982023021296170004', 'D', '1000', 'S', '2023-02-12 23:57:27', '0');
INSERT INTO `c_route` VALUES ('102023021254470028', '992023010100000001', '982023021235240027', 'D', '1000', 'S', '2023-02-12 20:38:41', '0');
INSERT INTO `c_route` VALUES ('102023021255530024', '992023010100000001', '982023021216390023', 'Q', '1000', 'S', '2023-02-12 20:26:31', '0');
INSERT INTO `c_route` VALUES ('102023021259300015', '992023010100000001', '982023021256680014', 'Q', '1000', 'S', '2023-02-12 20:18:25', '0');
INSERT INTO `c_route` VALUES ('102023021260040007', '992023010100000001', '982023021237010006', 'Q', '1000', 'S', '2023-02-12 20:11:01', '0');
INSERT INTO `c_route` VALUES ('102023021263470013', '992023010100000001', '982023021260000012', 'D', '1000', 'S', '2023-02-12 20:14:58', '0');
INSERT INTO `c_route` VALUES ('102023021268920026', '992023010100000001', '982023021296210025', 'D', '1000', 'S', '2023-02-12 20:32:22', '0');
INSERT INTO `c_route` VALUES ('102023021273060001', '992023010100000001', '982023021245840003', 'D', '1000', 'S', '2023-02-12 16:05:21', '0');
INSERT INTO `c_route` VALUES ('102023021275390003', '992023010100000001', '982023021241540002', 'Q', '1000', 'S', '2023-02-12 23:56:02', '0');
INSERT INTO `c_route` VALUES ('102023021275500030', '992023010100000001', '982023021259860029', 'D', '1000', 'S', '2023-02-12 20:39:41', '0');
INSERT INTO `c_route` VALUES ('102023021278870008', '992023010100000001', '982023021247300007', 'D', '1000', 'S', '2023-02-12 23:36:27', '0');
INSERT INTO `c_route` VALUES ('102023021281530005', '992023010100000001', '982023021216270004', 'D', '1000', 'S', '2023-02-12 20:09:35', '0');
INSERT INTO `c_route` VALUES ('102023021283770009', '992023010100000001', '982023021276940008', 'D', '1000', 'S', '2023-02-12 20:12:13', '0');
INSERT INTO `c_route` VALUES ('102023021287430017', '992023010100000001', '982023021259400016', 'D', '1000', 'S', '2023-02-12 20:20:01', '0');
INSERT INTO `c_route` VALUES ('102023021289240034', '992023010100000001', '982023021285420033', 'D', '1000', 'S', '2023-02-12 23:19:20', '0');
INSERT INTO `c_route` VALUES ('102023021293120011', '992023010100000001', '982023021296240010', 'D', '1000', 'S', '2023-02-12 23:37:28', '0');
INSERT INTO `c_route` VALUES ('102023021302460021', '992023010100000001', '982023021368120020', 'D', '1000', 'S', '2023-02-13 01:06:45', '0');
INSERT INTO `c_route` VALUES ('102023021306950006', '992023010100000001', '982023021370120005', 'D', '1000', 'S', '2023-02-13 01:25:29', '0');
INSERT INTO `c_route` VALUES ('102023021309550011', '992023010100000001', '982023021334130010', 'D', '1000', 'S', '2023-02-13 00:38:34', '0');
INSERT INTO `c_route` VALUES ('102023021319320013', '992023010100000001', '982023021357210012', 'D', '1000', 'S', '2023-02-13 00:40:03', '0');
INSERT INTO `c_route` VALUES ('102023021326040008', '992023010100000001', '982023021323660007', 'D', '1000', 'S', '2023-02-13 01:28:53', '0');
INSERT INTO `c_route` VALUES ('102023021341130010', '992023010100000001', '982023021321230009', 'Q', '1000', 'S', '2023-02-13 01:01:38', '0');
INSERT INTO `c_route` VALUES ('102023021350340018', '992023010100000001', '982023021325460017', 'Q', '1000', 'S', '2023-02-13 01:05:44', '0');
INSERT INTO `c_route` VALUES ('102023021365630009', '992023010100000001', '982023021360120008', 'D', '1000', 'S', '2023-02-13 00:03:03', '0');
INSERT INTO `c_route` VALUES ('102023021373790003', '992023010100000001', '982023021302420002', 'D', '1000', 'S', '2023-02-13 01:24:21', '0');
INSERT INTO `c_route` VALUES ('102023021379350012', '992023010100000001', '982023021395440011', 'D', '1000', 'S', '2023-02-13 01:02:55', '0');
INSERT INTO `c_route` VALUES ('102023021387790025', '992023010100000001', '982023021326930024', 'D', '1000', 'S', '2023-02-13 01:10:06', '0');
INSERT INTO `c_route` VALUES ('102023021387900015', '992023010100000001', '982023021386140014', 'D', '1000', 'S', '2023-02-13 00:43:44', '0');
INSERT INTO `c_route` VALUES ('102023021390830015', '992023010100000001', '982023021372850014', 'D', '1000', 'S', '2023-02-13 01:03:44', '0');
INSERT INTO `c_route` VALUES ('102023021399890004', '992023010100000001', '982023021322750003', 'D', '1000', 'S', '2023-02-13 00:59:16', '0');
INSERT INTO `c_route` VALUES ('11', '992023010100000001', '11', 'Q', '100', 'S', '2023-02-11 16:26:03', '0');
INSERT INTO `c_route` VALUES ('12', '992023010100000001', '12', 'Q', '100', 'S', '2023-02-11 16:26:04', '0');
INSERT INTO `c_route` VALUES ('13', '992023010100000001', '13', 'Q', '100', 'S', '2023-02-11 16:26:05', '0');
INSERT INTO `c_route` VALUES ('14', '992023010100000001', '14', 'Q', '100', 'S', '2023-02-11 16:26:06', '0');
INSERT INTO `c_route` VALUES ('15', '992023010100000001', '15', 'Q', '100', 'S', '2023-02-11 16:26:07', '0');
INSERT INTO `c_route` VALUES ('16', '992023010100000001', '16', 'Q', '100', 'S', '2023-02-11 16:26:08', '0');
INSERT INTO `c_route` VALUES ('17', '992023010100000001', '17', 'Q', '100', 'S', '2023-02-11 16:26:09', '0');
INSERT INTO `c_route` VALUES ('18', '992023010100000001', '982023021277800001', 'Q', '100', 'S', '2023-02-11 16:26:10', '0');
INSERT INTO `c_route` VALUES ('19', '992023010100000001', '982023021278440002', 'Q', '100', 'S', '2023-02-11 16:26:11', '0');
INSERT INTO `c_route` VALUES ('2', '992023010100000001', '2', 'Q', '100', 'S', '2023-02-11 16:26:12', '0');
INSERT INTO `c_route` VALUES ('20', '992023010100000001', '982023021210330001', 'D', '100', 'S', '2023-02-11 16:26:13', '0');
INSERT INTO `c_route` VALUES ('3', '992023010100000001', '3', 'Q', '100', 'S', '2023-02-11 16:26:14', '0');
INSERT INTO `c_route` VALUES ('4', '992023010100000001', '4', 'Q', '100', 'S', '2023-02-11 16:26:15', '0');
INSERT INTO `c_route` VALUES ('5', '992023010100000001', '5', 'Q', '100', 'S', '2023-02-11 16:26:16', '0');
INSERT INTO `c_route` VALUES ('6', '992023010100000001', '6', 'Q', '100', 'S', '2023-02-11 16:26:17', '0');
INSERT INTO `c_route` VALUES ('7', '992023010100000001', '7', 'Q', '100', 'S', '2023-02-11 16:26:18', '0');
INSERT INTO `c_route` VALUES ('8', '992023010100000001', '8', 'Q', '100', 'S', '2023-02-11 16:26:19', '0');
INSERT INTO `c_route` VALUES ('9', '992023010100000001', '9', 'Q', '100', 'S', '2023-02-11 16:26:53', '0');

-- ----------------------------
-- Table structure for c_service
-- ----------------------------
DROP TABLE IF EXISTS `c_service`;
CREATE TABLE `c_service` (
  `service_id` varchar(30) NOT NULL COMMENT '服务ID',
  `service_code` varchar(100) NOT NULL COMMENT '自定义，命名方式查询类query.+目标系统+.+业务名称 保存类 save.+目标系统+.+业务名称 修改类 modify.+目标系统+.+业务名称 删除类 remove.+目标系统+.+业务名称 例如：query.user.userinfo save.user.adduserinfo',
  `business_type_cd` varchar(12) DEFAULT NULL,
  `name` varchar(50) NOT NULL COMMENT '服务名称',
  `seq` int DEFAULT '1' COMMENT '顺序 只有同步方式下根据seq从小到大调用接口',
  `is_instance` varchar(12) NOT NULL DEFAULT 'CMD' COMMENT '是否Instance Y 需要，N不需要，NT透传类',
  `url` varchar(200) DEFAULT NULL COMMENT '目标地址',
  `method` varchar(50) DEFAULT NULL COMMENT '方法 空 为http post LOCAL_SERVICE 为调用本地服务 其他为webservice方式调用',
  `timeout` int NOT NULL DEFAULT '60' COMMENT '超时时间',
  `retry_count` int NOT NULL DEFAULT '3' COMMENT '重试次数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  PRIMARY KEY (`service_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of c_service
-- ----------------------------
INSERT INTO `c_service` VALUES ('1', 'login.generatorValidateCode', 'API', '验证码接口', '1', 'CMD', 'http://iot-user', 'GET', '60', '3', '2023-02-11 16:25:53', '0');
INSERT INTO `c_service` VALUES ('10', 'app.saveApp', 'API', '添加应用', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2019-07-03 15:31:30', '0');
INSERT INTO `c_service` VALUES ('11', 'cache.listCache', 'API', '查询缓存', '1', 'CMD', 'http://iot-dev', 'GET', '60', '3', '2019-07-03 15:31:56', '0');
INSERT INTO `c_service` VALUES ('12', 'cache.flushCache', 'API', '刷新缓存', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2019-07-03 15:31:51', '0');
INSERT INTO `c_service` VALUES ('13', 'app.updateApp', 'API', '刷新缓存', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2019-07-03 15:31:44', '0');
INSERT INTO `c_service` VALUES ('14', 'app.deleteApp', 'API', '刷新缓存', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2019-07-03 15:31:01', '0');
INSERT INTO `c_service` VALUES ('15', 'service.listServices', 'API', '查询服务', '1', 'CMD', 'http://iot-dev', 'GET', '60', '3', '2019-07-03 15:31:02', '0');
INSERT INTO `c_service` VALUES ('16', 'dict.listDict', 'API', '查询字典', '1', 'CMD', 'http://iot-dev', 'GET', '60', '3', '2019-07-03 15:31:03', '0');
INSERT INTO `c_service` VALUES ('17', 'service.saveService', 'API', '添加服务', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2019-07-03 15:31:04', '0');
INSERT INTO `c_service` VALUES ('2', 'login.pcUserLogin', 'API', '登录接口', '1', 'CMD', 'http://iot-user', 'POST', '60', '3', '2023-02-11 16:25:06', '0');
INSERT INTO `c_service` VALUES ('3', 'store.listMyStore', 'API', '查询我的商户', '1', 'CMD', 'http://iot-user', 'GET', '60', '3', '2023-02-11 16:25:07', '0');
INSERT INTO `c_service` VALUES ('4', 'privilege.queryStaffPrivileges', 'API', '查询员工权限', '1', 'CMD', 'http://iot-user', 'GET', '60', '3', '2023-02-11 16:25:08', '0');
INSERT INTO `c_service` VALUES ('5', 'community.listMyEnteredCommunitys', 'API', '查询入驻小区', '1', 'CMD', 'http://iot-community', 'GET', '60', '3', '2023-02-11 16:25:09', '0');
INSERT INTO `c_service` VALUES ('6', 'menu.listCatalog', 'API', '查询入驻小区', '1', 'CMD', 'http://iot-user', 'GET', '60', '3', '2023-02-11 16:25:10', '0');
INSERT INTO `c_service` VALUES ('7', 'user.getUserInfo', 'API', '查询登录用户', '1', 'CMD', 'http://iot-user', 'GET', '60', '3', '2023-02-10 16:25:11', '0');
INSERT INTO `c_service` VALUES ('8', 'menu.listCatalogMenus', 'API', '查询目录菜单', '1', 'CMD', 'http://iot-user', 'GET', '60', '3', '2023-02-11 16:25:12', '0');
INSERT INTO `c_service` VALUES ('9', 'app.listApps', 'API', '查询应用', '1', 'CMD', 'http://iot-dev', 'GET', '60', '3', '2019-07-03 15:31:14', '0');
INSERT INTO `c_service` VALUES ('982023021203470037', 'menuGroupCatalog.saveMenuGroupCatalog', 'API', '保存菜单组目录', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-12 23:23:28', '0');
INSERT INTO `c_service` VALUES ('982023021204510012', 'menu.listMenus', 'API', '查询菜单', '1', 'CMD', 'http://iot-dev', 'GET', '60', '3', '2023-02-12 23:44:04', '0');
INSERT INTO `c_service` VALUES ('982023021210330001', 'service.bindingService', 'API', '服务绑定', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-12 15:34:55', '0');
INSERT INTO `c_service` VALUES ('982023021214060031', 'menuGroupCatalog.listMenuGroupCatalog', 'API', '查询目录菜单组', '1', 'CMD', 'http://iot-user', 'GET', '60', '3', '2023-02-12 20:40:39', '0');
INSERT INTO `c_service` VALUES ('982023021216270004', 'serviceRegister.deleteServiceRegister', 'API', '删除服务注册', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-12 20:09:35', '0');
INSERT INTO `c_service` VALUES ('982023021216390023', 'menuCatalog.listMenuCatalog', 'API', '查询菜单目录', '1', 'CMD', 'http://iot-user', 'GET', '60', '3', '2023-02-12 20:26:31', '0');
INSERT INTO `c_service` VALUES ('982023021223650035', 'menuGroup.listMenuGroups', 'API', '查询菜单组', '1', 'CMD', 'http://iot-dev', 'GET', '60', '3', '2023-02-12 23:20:33', '0');
INSERT INTO `c_service` VALUES ('982023021223940021', 'businessTableHis.deleteBusinessTableHis', 'API', '删除业务轨迹', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-12 20:22:34', '0');
INSERT INTO `c_service` VALUES ('982023021227650019', 'businessTableHis.updateBusinessTableHis', 'API', '修改业务轨迹', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-12 20:21:15', '0');
INSERT INTO `c_service` VALUES ('982023021235240027', 'menuCatalog.saveMenuCatalog', 'API', '添加菜单目录', '1', 'CMD', 'http://iot-user', 'POST', '60', '3', '2023-02-12 20:38:41', '0');
INSERT INTO `c_service` VALUES ('982023021237010006', 'serviceImpl.listServiceImpls', 'API', '查询服务实现', '1', 'CMD', 'http://iot-dev', 'GET', '60', '3', '2023-02-12 20:11:01', '0');
INSERT INTO `c_service` VALUES ('982023021241540002', 'basePrivilege.listBasePrivileges', 'API', '查询菜单权限', '1', 'CMD', 'http://iot-dev', 'GET', '60', '3', '2023-02-12 23:56:02', '0');
INSERT INTO `c_service` VALUES ('982023021245840003', 'service.deleteService', 'API', '删除服务', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-12 15:38:22', '0');
INSERT INTO `c_service` VALUES ('982023021247300007', 'menuGroup.updateMenuGroup', 'API', '修改菜单组', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-12 23:36:27', '0');
INSERT INTO `c_service` VALUES ('982023021248040004', 'menuGroup.saveMenuGroup', 'API', '添加菜单组', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-12 23:35:40', '0');
INSERT INTO `c_service` VALUES ('982023021256680014', 'businessTableHis.listBusinessTableHis', 'API', '查询业务轨迹', '1', 'CMD', 'http://iot-dev', 'GET', '60', '3', '2023-02-12 20:18:25', '0');
INSERT INTO `c_service` VALUES ('982023021258190002', 'serviceRegister.updateServiceRegister', 'API', '修改服务注册', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-12 19:42:29', '0');
INSERT INTO `c_service` VALUES ('982023021259400016', 'businessTableHis.saveBusinessTableHis', 'API', '保存业务轨迹', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-12 20:20:01', '0');
INSERT INTO `c_service` VALUES ('982023021259860029', 'menuCatalog.deleteMenuCatalog', 'API', '删除目录', '1', 'CMD', 'http://iot-user', 'POST', '60', '3', '2023-02-12 20:39:41', '0');
INSERT INTO `c_service` VALUES ('982023021260000012', 'serviceImpl.deleteServiceImpl', 'API', '删除服务提供', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-12 20:14:58', '0');
INSERT INTO `c_service` VALUES ('982023021264030002', '123', 'API', '123', '1', 'CMD', 'http://iot-community', 'GET', '60', '3', '2023-02-12 15:37:37', '1');
INSERT INTO `c_service` VALUES ('982023021273350010', 'serviceImpl.updateServiceImpl', 'API', '修改服务实现', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-12 20:13:57', '0');
INSERT INTO `c_service` VALUES ('982023021276940008', 'serviceImpl.saveServiceImpl', 'API', '添加服务注册', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-12 20:12:13', '0');
INSERT INTO `c_service` VALUES ('982023021277800001', 'service.updateService', 'API', '修改服务', '1', 'CMD', 'http://iot-dev', 'POST', '61', '3', '2023-02-12 15:22:43', '0');
INSERT INTO `c_service` VALUES ('982023021278440002', 'serviceRegister.listServiceRegisters', 'API', '查询服务注册', '1', 'CMD', 'http://iot-dev', 'GET', '60', '3', '2023-02-12 15:23:18', '0');
INSERT INTO `c_service` VALUES ('982023021285420033', 'menuGroupCatalog.deleteMenuGroupCatalog', 'API', '删除菜单组目录', '1', 'CMD', 'http://iot-user', 'POST', '60', '3', '2023-02-12 23:19:19', '0');
INSERT INTO `c_service` VALUES ('982023021296170004', 'basePrivilege.saveBasePrivilege', 'API', '添加权限', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-12 23:57:27', '0');
INSERT INTO `c_service` VALUES ('982023021296210025', 'menuCatalog.updateMenuCatalog', 'API', '修改菜单目录', '1', 'CMD', 'http://iot-user', 'POST', '60', '3', '2023-02-12 20:32:22', '0');
INSERT INTO `c_service` VALUES ('982023021296240010', 'menuGroup.deleteMenuGroup', 'API', '删除菜单组', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-12 23:37:27', '0');
INSERT INTO `c_service` VALUES ('982023021302420002', 'mapping.updateMapping', 'API', '修改映射', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-13 01:24:21', '0');
INSERT INTO `c_service` VALUES ('982023021321230009', 'mapping.listMappingDomain', 'API', '查询配置', '1', 'CMD', 'http://iot-dev', 'GET', '60', '3', '2023-02-13 01:01:38', '0');
INSERT INTO `c_service` VALUES ('982023021322750003', 'menu.deleteMenu', 'API', '删除菜单', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-13 00:59:15', '0');
INSERT INTO `c_service` VALUES ('982023021323660007', 'user.changeStaffPwd', 'API', '修改密码', '1', 'CMD', 'http://iot-user', 'POST', '60', '3', '2023-02-13 01:28:52', '0');
INSERT INTO `c_service` VALUES ('982023021325460017', 'mapping.listMappings', 'API', '查询配置', '1', 'CMD', 'http://iot-dev', 'GET', '60', '3', '2023-02-13 01:05:44', '0');
INSERT INTO `c_service` VALUES ('982023021326930024', 'mapping.saveMapping', 'API', '添加映射', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-13 01:10:06', '0');
INSERT INTO `c_service` VALUES ('982023021334130010', 'basePrivilege.updateBasePrivilege', 'API', '修改权限', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-13 00:38:33', '0');
INSERT INTO `c_service` VALUES ('982023021357210012', 'menu.updateMenu', 'API', '修改菜单', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-13 00:40:03', '0');
INSERT INTO `c_service` VALUES ('982023021360120008', 'basePrivilege.deleteBasePrivilege', 'API', '删除权限', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-13 00:03:02', '0');
INSERT INTO `c_service` VALUES ('982023021368120020', 'mapping.deleteMappingDomain', 'API', '删除配置域', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-13 01:06:45', '0');
INSERT INTO `c_service` VALUES ('982023021370120005', 'mapping.deleteMapping', 'API', '删除映射', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-13 01:25:29', '0');
INSERT INTO `c_service` VALUES ('982023021372850014', 'mapping.updateMappingDomain', 'API', '修改配置域', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-13 01:03:43', '0');
INSERT INTO `c_service` VALUES ('982023021386140014', 'menu.configMenu', 'API', '配置菜单', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-13 00:43:44', '0');
INSERT INTO `c_service` VALUES ('982023021395440011', 'mapping.saveMappingDomain', 'API', '保存配置域', '1', 'CMD', 'http://iot-dev', 'POST', '60', '3', '2023-02-13 01:02:55', '0');

-- ----------------------------
-- Table structure for c_service_business
-- ----------------------------
DROP TABLE IF EXISTS `c_service_business`;
CREATE TABLE `c_service_business` (
  `service_business_id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `business_type_cd` varchar(12) DEFAULT NULL,
  `invoke_type` varchar(4) NOT NULL COMMENT '调用类型，1 http-post(微服务内应用) 2 webservice 3 http-post(微服务之外应用)',
  `url` varchar(200) DEFAULT NULL COMMENT '目标地址',
  `message_topic` varchar(50) DEFAULT NULL COMMENT '异步时的消息topic名称',
  `timeout` int NOT NULL DEFAULT '60' COMMENT '超时时间',
  `retry_count` int NOT NULL DEFAULT '3' COMMENT '重试次数',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  PRIMARY KEY (`service_business_id`),
  UNIQUE KEY `business_type_cd` (`business_type_cd`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of c_service_business
-- ----------------------------
INSERT INTO `c_service_business` VALUES ('1', 'abc', '1', 'sdf', 's', '60', '3', '2023-02-12 20:12:36', '1');

-- ----------------------------
-- Table structure for c_service_sql
-- ----------------------------
DROP TABLE IF EXISTS `c_service_sql`;
CREATE TABLE `c_service_sql` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `service_code` varchar(50) NOT NULL COMMENT '对应c_service表',
  `name` varchar(50) NOT NULL COMMENT '名称',
  `params` varchar(500) NOT NULL COMMENT '参数',
  `query_model` varchar(1) NOT NULL COMMENT '查询方式 1、sql,2、存储过程',
  `sql` longtext COMMENT '执行sql',
  `proc` varchar(200) DEFAULT NULL COMMENT '存储过程名称',
  `java_script` longtext COMMENT '执行java脚本代码',
  `template` longtext COMMENT '输出模板',
  `remark` varchar(200) DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  PRIMARY KEY (`id`),
  UNIQUE KEY `service_code` (`service_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of c_service_sql
-- ----------------------------

-- ----------------------------
-- Table structure for m_menu
-- ----------------------------
DROP TABLE IF EXISTS `m_menu`;
CREATE TABLE `m_menu` (
  `m_id` varchar(30) NOT NULL COMMENT '菜单ID',
  `name` varchar(10) NOT NULL COMMENT '菜单名称',
  `g_id` varchar(30) NOT NULL COMMENT '菜单组ID',
  `url` varchar(200) NOT NULL COMMENT '打开地址',
  `seq` int NOT NULL COMMENT '列顺序',
  `p_id` varchar(30) NOT NULL COMMENT '权限ID',
  `description` varchar(200) DEFAULT NULL COMMENT '菜单描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  `is_show` varchar(2) NOT NULL DEFAULT 'Y' COMMENT '菜单页面是否显示 Y显示N不显示',
  PRIMARY KEY (`m_id`),
  KEY `index_m_id` (`m_id`) USING BTREE,
  KEY `index_g_id` (`g_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of m_menu
-- ----------------------------
INSERT INTO `m_menu` VALUES ('700201906025', '应用信息', '800201907014', '/#/pages/dev/appManage', '1', '500201906026', '应用信息', '2019-06-19 02:16:11', '0', 'Y');
INSERT INTO `m_menu` VALUES ('700201906026', '服务信息', '800201907014', '/#/pages/dev/serviceManage', '1', '500201906027', '服务信息', '2019-06-19 02:16:11', '0', 'Y');
INSERT INTO `m_menu` VALUES ('700201906027', '服务实现', '800201907014', '/#/pages/dev/serviceImplManage', '4', '500201906028', '服务实现', '2019-07-07 08:49:51', '0', 'Y');
INSERT INTO `m_menu` VALUES ('700201906028', '配置中心', '800201907016', '/#/pages/dev/mappingManage', '2', '500201906029', '编码映射', '2019-06-19 02:16:11', '0', 'Y');
INSERT INTO `m_menu` VALUES ('700201907029', '服务绑定', '800201907014', '/#/pages/dev/serviceBinding', '3', '500201907030', '服务绑定', '2019-07-11 11:46:59', '0', 'N');
INSERT INTO `m_menu` VALUES ('700201908030', '服务注册', '800201907014', '/#/pages/dev/serviceRegisterManage', '2', '500201907032', '服务注册', '2019-08-10 09:49:38', '0', 'Y');
INSERT INTO `m_menu` VALUES ('700201908032', '刷新缓存', '800201908018', '/#/pages/dev/cacheManage', '1', '500201908034', '刷新缓存', '2019-08-13 11:35:35', '0', 'Y');
INSERT INTO `m_menu` VALUES ('700201908033', '菜单组', '800201908019', '/#/pages/dev/menuGroupManage', '2', '500201908035', '菜单组', '2019-08-19 12:17:46', '0', 'Y');
INSERT INTO `m_menu` VALUES ('700201908034', '基础权限', '800201908019', '/#/pages/dev/basePrivilegeManage', '3', '500201908036', '基础权限', '2019-08-19 12:17:46', '0', 'N');
INSERT INTO `m_menu` VALUES ('700201908035', '菜单维护', '800201908019', '/#/pages/dev/menuManage', '3', '502019082521880003', '菜单维护', '2019-08-25 10:38:16', '0', 'Y');
INSERT INTO `m_menu` VALUES ('700201908036', '配置菜单', '800201908019', '/#/pages/dev/configMenu', '4', '502019083153490001', '配置菜单', '2019-08-31 14:36:57', '0', 'N');
INSERT INTO `m_menu` VALUES ('702019090161010006', '服务提供', '800201907014', '/#/pages/dev/serviceProvideManage', '5', '502019090149430005', '服务提供', '2019-09-01 14:14:08', '0', 'Y');
INSERT INTO `m_menu` VALUES ('702019090210710002', '开发服务', '800201907014', '/#/pages/dev/devServiceProvide', '6', '502019090277380001', '开发服务提供', '2019-09-02 15:33:31', '0', 'N');
INSERT INTO `m_menu` VALUES ('702019090293430003', '定时任务', '802019090224100001', '/#/pages/dev/jobManage', '1', '502019090260990002', '管理定时任务', '2019-09-02 12:35:51', '0', 'Y');
INSERT INTO `m_menu` VALUES ('702019092962440002', '配置菜单', '800201908019', '/#/pages/configMenu', '4', '502019092956560001', '配置菜单', '2019-09-29 12:18:14', '1', 'N');
INSERT INTO `m_menu` VALUES ('702020081441030209', '属性', '802020081417800208', '/#/pages/dev/attrSpecManage', '1', '-1', '', '2020-08-14 22:17:25', '0', 'Y');
INSERT INTO `m_menu` VALUES ('702020081494380219', '属性值', '802020081417800208', '/#/pages/dev/attrValueManage', '2', '-1', '', '2020-08-14 23:22:48', '0', 'N');
INSERT INTO `m_menu` VALUES ('702020111628740005', '交互日志', '802020111618520004', '/#/pages/dev/transactionLogManage', '1', '-1', '', '2020-11-16 01:09:05', '0', 'Y');
INSERT INTO `m_menu` VALUES ('702020120773940298', 'Databus', '800201907016', '/#/pages/dev/businessDatabusManage', '3', '-1', '', '2020-12-07 18:14:35', '0', 'Y');
INSERT INTO `m_menu` VALUES ('702021031606850071', '修改密码', '802021031653410070', '/#/pages/frame/changeStaffPwd', '1', '-1', '', '2021-03-16 11:14:16', '0', 'Y');
INSERT INTO `m_menu` VALUES ('702021060521430094', '手机商城菜单', '800201908019', '/#/pages/admin/housekeepingTypeManage', '4', '-1', '', '2021-06-05 15:38:42', '0', 'Y');
INSERT INTO `m_menu` VALUES ('702021080500070003', '初始化小区', '802021031653410070', '/#/pages/dev/initializeCommunityManage', '6', '-1', '', '2021-08-05 19:09:43', '0', 'Y');
INSERT INTO `m_menu` VALUES ('702021091240630201', '业务轨迹', '800201907014', '/#/pages/dev/businessTableHisManage', '6', '-1', '', '2021-09-12 15:48:39', '0', 'Y');
INSERT INTO `m_menu` VALUES ('702021110981050126', '报表组', '802021110952690125', '/#/pages/dev/reportCustomGroupManage', '1', '-1', '', '2021-11-09 14:38:40', '0', 'Y');
INSERT INTO `m_menu` VALUES ('702021111211780405', '报表组件', '802021110952690125', '/#/pages/dev/reportCustomComponentManage', '3', '-1', '报表组件', '2021-11-12 10:00:34', '0', 'Y');
INSERT INTO `m_menu` VALUES ('702021111293630375', '报表信息', '802021110952690125', '/#/pages/dev/reportCustomManage', '2', '-1', '', '2021-11-12 09:03:45', '0', 'Y');
INSERT INTO `m_menu` VALUES ('702022022626440646', '菜单目录', '800201908019', '/#/pages/dev/menuCatalogManage', '1', '-1', '', '2022-02-26 10:32:55', '0', 'Y');
INSERT INTO `m_menu` VALUES ('702022070767920037', '字典', '800201907016', '/#/pages/dev/dictManage', '4', '-1', '字典', '2022-07-07 09:26:35', '0', 'Y');
INSERT INTO `m_menu` VALUES ('702022081723230003', '系统配置', '802021031653410070', '/#/pages/frame/systemInfoManage', '2', '-1', '', '2022-08-17 00:15:09', '0', 'Y');
INSERT INTO `m_menu` VALUES ('702023011061770001', '注册协议', '802021031653410070', '/#/pages/frame/registerProtocol', '3', '-1', '', '2023-01-10 17:20:57', '0', 'Y');
INSERT INTO `m_menu` VALUES ('702023012914280004', '配置项', '800201907016', '/#/pages/dev/mappingDomainManage', '1', '-1', '', '2023-01-29 14:38:19', '0', 'Y');
INSERT INTO `m_menu` VALUES ('702023021322800001', '小区信息', '802023021272240006', '/#/pages/common/communityManage', '1', '502023021381120002', '1', '2023-02-13 00:56:50', '0', 'Y');
INSERT INTO `m_menu` VALUES ('702023021396940007', 'sdf', '802023021272240006', 'sdf', '15', '502023021308870008', '', '2023-02-13 01:00:28', '1', 'Y');

-- ----------------------------
-- Table structure for m_menu_catalog
-- ----------------------------
DROP TABLE IF EXISTS `m_menu_catalog`;
CREATE TABLE `m_menu_catalog` (
  `ca_id` varchar(30) NOT NULL COMMENT '目录名称',
  `name` varchar(10) NOT NULL COMMENT '菜单组名称',
  `icon` varchar(20) NOT NULL COMMENT '菜单图片',
  `seq` int NOT NULL COMMENT '列顺序',
  `store_type` varchar(12) DEFAULT NULL COMMENT '商户类型',
  `url` varchar(256) NOT NULL DEFAULT '#' COMMENT '点击后进入那个页面，#表示不进入',
  `is_show` varchar(12) NOT NULL DEFAULT 'Y' COMMENT '是否显示 Y 是 F否',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  PRIMARY KEY (`ca_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of m_menu_catalog
-- ----------------------------
INSERT INTO `m_menu_catalog` VALUES ('102022022695140008', '首页', 'fa fa-home', '1', '800900000000', '/', 'Y', '2022-02-26 11:39:18', '0');
INSERT INTO `m_menu_catalog` VALUES ('102023021260140001', '首页', 'fa fa-home', '1', '800900000001', '/', 'Y', '2023-02-12 23:32:45', '0');
INSERT INTO `m_menu_catalog` VALUES ('102023021279740001', 'sss', 'ss', '1', '800900000001', 's', 'Y', '2023-02-12 20:39:04', '1');

-- ----------------------------
-- Table structure for m_menu_group
-- ----------------------------
DROP TABLE IF EXISTS `m_menu_group`;
CREATE TABLE `m_menu_group` (
  `g_id` varchar(30) NOT NULL COMMENT '菜单组ID',
  `name` varchar(10) NOT NULL COMMENT '菜单组名称',
  `icon` varchar(20) NOT NULL COMMENT '菜单图片',
  `label` varchar(20) NOT NULL COMMENT '菜单标签',
  `seq` int NOT NULL COMMENT '列顺序',
  `description` varchar(200) DEFAULT NULL COMMENT '菜单描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  `group_type` varchar(12) NOT NULL DEFAULT 'P_WEB' COMMENT ' 菜单类型',
  `store_type` varchar(12) DEFAULT NULL COMMENT '商户类型',
  PRIMARY KEY (`g_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of m_menu_group
-- ----------------------------
INSERT INTO `m_menu_group` VALUES ('800201907014', '服务', 'fa fa-edit', '', '1', '服务管理', '2019-04-01 07:55:51', '0', 'P_WEB', '800900000000');
INSERT INTO `m_menu_group` VALUES ('800201907015', '配置', 'fa-flask', '', '1', '服务配置管理', '2019-07-07 16:47:42', '0', 'P_WEB', '800900000000');
INSERT INTO `m_menu_group` VALUES ('800201907016', '配置', 'fa fa-globe', '', '4', '映射管理', '2019-07-07 16:47:42', '0', 'P_WEB', '800900000000');
INSERT INTO `m_menu_group` VALUES ('800201908018', '缓存', 'fa-flask', '', '5', '缓存管理', '2019-08-13 11:30:15', '0', 'P_WEB', '800900000000');
INSERT INTO `m_menu_group` VALUES ('800201908019', '菜单', 'fa fa-th-large', '', '1', '菜单管理', '2019-08-19 12:13:35', '0', 'P_WEB', '800900000000');
INSERT INTO `m_menu_group` VALUES ('802019090224100001', '任务', 'fa fa-diamond', '', '10', '管理定时任务', '2019-09-02 12:34:40', '0', 'P_WEB', '800900000000');
INSERT INTO `m_menu_group` VALUES ('802020081417800208', '属性', 'fa-flash', '', '4', '', '2020-08-14 22:16:28', '0', 'P_WEB', '800900000000');
INSERT INTO `m_menu_group` VALUES ('802020111618520004', '日志', 'fa fa-diamond', '', '18', '', '2020-11-16 01:08:07', '0', 'P_WEB', '800900000000');
INSERT INTO `m_menu_group` VALUES ('802021031653410070', '系统', 'fa fa-globe', '', '19', '', '2021-03-16 11:13:47', '0', 'P_WEB', '800900000000');
INSERT INTO `m_menu_group` VALUES ('802021110952690125', '报表', 'fa fa-globe', '', '4', '', '2021-11-09 14:34:45', '0', 'P_WEB', '800900000000');
INSERT INTO `m_menu_group` VALUES ('802023021272240006', '数据', 'fa fa-laptop', '', '1', '1', '2023-02-12 23:35:51', '0', 'P_WEB', '800900000001');
INSERT INTO `m_menu_group` VALUES ('802023021279410009', 'ss', 'ss', '', '1', 'ss', '2023-02-12 23:36:53', '1', 'P_WEB', '800900000002');

-- ----------------------------
-- Table structure for m_menu_group_catalog
-- ----------------------------
DROP TABLE IF EXISTS `m_menu_group_catalog`;
CREATE TABLE `m_menu_group_catalog` (
  `gc_id` varchar(30) NOT NULL COMMENT '目录名称',
  `ca_id` varchar(30) NOT NULL COMMENT '目录ID',
  `g_id` varchar(30) NOT NULL COMMENT '菜单组ID',
  `store_type` varchar(12) DEFAULT NULL COMMENT '商户类型',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  PRIMARY KEY (`gc_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of m_menu_group_catalog
-- ----------------------------
INSERT INTO `m_menu_group_catalog` VALUES ('102022022626540021', '102022022695140008', '800201907015', '800900000000', '2022-02-26 13:59:02', '0');
INSERT INTO `m_menu_group_catalog` VALUES ('102022022629600028', '102022022695140008', '802020111618520004', '800900000000', '2022-02-26 13:59:39', '1');
INSERT INTO `m_menu_group_catalog` VALUES ('102022022635600025', '102022022695140008', '800201907016', '800900000000', '2022-02-26 13:59:24', '0');
INSERT INTO `m_menu_group_catalog` VALUES ('102022022641060020', '102022022695140008', '800201907014', '800900000000', '2022-02-26 13:58:58', '0');
INSERT INTO `m_menu_group_catalog` VALUES ('102022022647400024', '102022022695140008', '802020081417800208', '800900000000', '2022-02-26 13:59:17', '0');
INSERT INTO `m_menu_group_catalog` VALUES ('102022022655030022', '102022022695140008', '800201908019', '800900000000', '2022-02-26 13:59:07', '0');
INSERT INTO `m_menu_group_catalog` VALUES ('102022022661130023', '102022022695140008', '802021110952690125', '800900000000', '2022-02-26 13:59:11', '0');
INSERT INTO `m_menu_group_catalog` VALUES ('102022022662880029', '102022022695140008', '802021031653410070', '800900000000', '2022-02-26 13:59:44', '0');
INSERT INTO `m_menu_group_catalog` VALUES ('102022022680080027', '102022022695140008', '802019090224100001', '800900000000', '2022-02-26 13:59:34', '0');
INSERT INTO `m_menu_group_catalog` VALUES ('102022022696990026', '102022022695140008', '800201908018', '800900000000', '2022-02-26 13:59:28', '0');
INSERT INTO `m_menu_group_catalog` VALUES ('102023021210680003', '102022022695140008', '802020111618520004', '800900000000', '2023-02-12 23:31:49', '0');

-- ----------------------------
-- Table structure for m_menu_group_community
-- ----------------------------
DROP TABLE IF EXISTS `m_menu_group_community`;
CREATE TABLE `m_menu_group_community` (
  `gc_id` varchar(30) NOT NULL COMMENT '主键ID',
  `g_id` varchar(30) NOT NULL COMMENT '菜单组ID',
  `name` varchar(10) NOT NULL COMMENT '菜单组名称',
  `community_id` varchar(30) NOT NULL COMMENT '小区ID',
  `community_name` varchar(64) NOT NULL COMMENT '小区名称',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  PRIMARY KEY (`gc_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of m_menu_group_community
-- ----------------------------

-- ----------------------------
-- Table structure for p_privilege
-- ----------------------------
DROP TABLE IF EXISTS `p_privilege`;
CREATE TABLE `p_privilege` (
  `p_id` varchar(30) NOT NULL COMMENT '权限ID',
  `name` varchar(10) NOT NULL COMMENT '权限名称',
  `description` varchar(200) DEFAULT NULL COMMENT '权限描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  `domain` varchar(12) NOT NULL DEFAULT '-1' COMMENT '权限域 商户详见store_type store_type_cd',
  `resource` varchar(200) DEFAULT NULL COMMENT '资源路径',
  `m_id` varchar(30) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`p_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of p_privilege
-- ----------------------------
INSERT INTO `p_privilege` VALUES ('500201906026', '应用信息', '应用信息1', '2019-06-19 16:01:38', '0', '800900000000', '/appFlow', '700201906025');
INSERT INTO `p_privilege` VALUES ('500201906027', '服务信息', '服务信息', '2019-06-19 16:01:38', '0', '800900000000', '/serviceFlow', '700201906026');
INSERT INTO `p_privilege` VALUES ('500201906028', '服务实现', '配置信息管理', '2019-07-07 16:42:48', '0', '800900000000', '/serviceImplFlow', '700201906027');
INSERT INTO `p_privilege` VALUES ('500201906029', '编码映射', '映射管理', '2019-06-19 16:01:38', '0', '800900000000', '/mappingFlow', '700201906028');
INSERT INTO `p_privilege` VALUES ('500201907030', '服务绑定', '服务绑定', '2019-07-11 11:45:33', '0', '800900000000', '/serviceBindingFlow', '700201907029');
INSERT INTO `p_privilege` VALUES ('500201907032', '服务注册', '服务注册', '2019-08-10 09:45:34', '0', '800900000000', '/serviceRegisterFlow', '700201908030');
INSERT INTO `p_privilege` VALUES ('500201908034', '刷新缓存', '刷新缓存', '2019-08-13 11:31:48', '0', '800900000000', '/cacheFlow', '700201908032');
INSERT INTO `p_privilege` VALUES ('500201908035', '菜单组', '菜单组管理', '2019-08-19 12:15:20', '0', '800900000000', '/menuGroupFlow', '700201908033');
INSERT INTO `p_privilege` VALUES ('500201908036', '基础权限', '基础权限', '2019-08-19 12:15:20', '0', '800900000000', '/basePrivilegeFlow', '700201908034');
INSERT INTO `p_privilege` VALUES ('502019082521880003', '菜单维护', '菜单管理权限', '2019-08-25 05:47:11', '0', '800900000000', '/menuFlow', '700201908035');
INSERT INTO `p_privilege` VALUES ('502019083153490001', '配置菜单', '配置菜单', '2019-08-31 14:33:39', '0', '800900000000', '/configMenuFlow', '700201908036');
INSERT INTO `p_privilege` VALUES ('502019090149430005', '服务提供', '服务提供', '2019-09-01 14:14:08', '0', '800900000000', '/serviceProvideFlow', '702019090161010006');
INSERT INTO `p_privilege` VALUES ('502019090260990002', '定时任务', '管理定时任务', '2019-09-02 12:35:51', '0', '800900000000', '/HcFtpToFileSystemConfigList/', '702019090293430003');
INSERT INTO `p_privilege` VALUES ('502019090277380001', '开发服务', '开发服务提供', '2019-09-02 15:33:31', '0', '800900000000', '/devServiceProvideFlow', '702019090210710002');
INSERT INTO `p_privilege` VALUES ('502019092956560001', '配置菜单', '配置菜单', '2019-09-29 12:18:14', '1', '800900000000', null, '702019092962440002');
INSERT INTO `p_privilege` VALUES ('502020081421160220', '属性值', '', '2020-08-14 23:22:48', '0', '800900000000', '/admin.html#/pages/dev/attrValueManage', '702020081494380219');
INSERT INTO `p_privilege` VALUES ('502020081431630210', '属性', '', '2020-08-14 22:17:25', '0', '800900000000', '/admin.html#/pages/dev/attrSpecManage', '702020081441030209');
INSERT INTO `p_privilege` VALUES ('502020111627880006', '交互日志', '', '2020-11-16 01:09:05', '0', '800900000000', '/admin.html#/pages/dev/transactionLogManage', '702020111628740005');
INSERT INTO `p_privilege` VALUES ('502020120765630299', 'Databus', '', '2020-12-07 18:14:35', '0', '800900000000', '/admin.html#/pages/dev/businessDatabusManage', '702020120773940298');
INSERT INTO `p_privilege` VALUES ('502021031645040072', '修改密码', '', '2021-03-16 11:14:16', '0', '800900000000', '/admin.html#/pages/frame/changeStaffPwd', '702021031606850071');
INSERT INTO `p_privilege` VALUES ('502021060583310095', '手机商城菜单', '', '2021-06-05 15:38:42', '0', '800900000001', '/admin.html#/pages/admin/housekeepingTypeManage', '702021060521430094');
INSERT INTO `p_privilege` VALUES ('502021080561750004', '初始化小区', '', '2021-08-05 19:09:43', '0', '800900000000', '/admin.html#/pages/dev/initializeCommunityManage', '702021080500070003');
INSERT INTO `p_privilege` VALUES ('502021091295510202', '业务轨迹', '', '2021-09-12 15:48:39', '0', '800900000000', '/admin.html#/pages/dev/businessTableHisManage', '702021091240630201');
INSERT INTO `p_privilege` VALUES ('502021110374581810', '修改菜单组', '', '2021-11-03 20:38:49', '0', '800900000000', '/callComponent/editMenuGroup/update', '700201908033');
INSERT INTO `p_privilege` VALUES ('502021110974670127', '报表组', '', '2021-11-09 14:38:40', '0', '800900000000', '/admin.html#/pages/dev/reportCustomGroupManage', '702021110981050126');
INSERT INTO `p_privilege` VALUES ('502021111231120406', '报表组件', '报表组件', '2021-11-12 10:00:34', '0', '800900000000', '/admin.html#/pages/dev/reportCustomComponentManage', '702021111211780405');
INSERT INTO `p_privilege` VALUES ('502021111280010376', '报表信息', '', '2021-11-12 09:03:45', '0', '800900000000', '/admin.html#/pages/dev/reportCustomManage', '702021111293630375');
INSERT INTO `p_privilege` VALUES ('502021111404460005', '查询服务权限', '', '2021-11-14 23:44:49', '0', '800900000000', '/app/serviceManage/list', '700201906026');
INSERT INTO `p_privilege` VALUES ('502021111501470249', '删除菜单组', '', '2021-11-15 16:59:30', '0', '800900000000', '/callComponent/deleteMenuGroup/delete', '700201908033');
INSERT INTO `p_privilege` VALUES ('502021111503660250', '查询菜单列表', '', '2021-11-15 17:00:53', '0', '800900000000', '/callComponent/menuManage/list', '700201908035');
INSERT INTO `p_privilege` VALUES ('502021111504290091', '新增服务提供', '', '2021-11-15 16:49:36', '0', '800900000000', '/callComponent/devServiceProvideBinding/binding', '702019090161010006');
INSERT INTO `p_privilege` VALUES ('502021111509280272', '保存报表组件', '', '2021-11-15 17:21:59', '0', '800900000000', '/app/reportCustomComponent.saveReportCustomComponent', '702021111211780405');
INSERT INTO `p_privilege` VALUES ('502021111510210072', '删除编码映射', '', '2021-11-15 16:10:03', '0', '800900000000', '/callComponent/deleteMapping/delete', '700201906028');
INSERT INTO `p_privilege` VALUES ('502021111510430063', '刷新缓存', '', '2021-11-15 15:49:22', '0', '800900000000', '/callComponent/cacheManage/flushCache', '700201908032');
INSERT INTO `p_privilege` VALUES ('502021111514690259', '修改手机菜单权限', '', '2021-11-15 17:05:48', '0', '800900000001', '/app/housekeepingType/updateHousekeepingType', '702021060521430094');
INSERT INTO `p_privilege` VALUES ('502021111517720085', '修改服务提供', '', '2021-11-15 16:47:34', '0', '800900000000', '/callComponent/editServiceProvide/update', '702019090161010006');
INSERT INTO `p_privilege` VALUES ('502021111520450266', '查询报表信息', '', '2021-11-15 17:17:40', '1', '800900000000', '/app/reportCustom.listReportCustom', '702021111293630375');
INSERT INTO `p_privilege` VALUES ('502021111529750274', '删除报表组件', '', '2021-11-15 17:22:54', '0', '800900000000', '/app/reportCustomComponent.deleteReportCustomComponent', '702021111211780405');
INSERT INTO `p_privilege` VALUES ('502021111531030252', '修改菜单维护', '', '2021-11-15 17:01:34', '0', '800900000000', '/callComponent/editMenu/update', '700201908035');
INSERT INTO `p_privilege` VALUES ('502021111538930243', '查询菜单组', '', '2021-11-15 16:57:21', '0', '800900000000', '/callComponent/menuGroupManage/list', '700201908033');
INSERT INTO `p_privilege` VALUES ('502021111540410070', '新增编码映射', '', '2021-11-15 16:08:32', '0', '800900000000', '/callComponent/addMapping/save', '700201906028');
INSERT INTO `p_privilege` VALUES ('502021111540820058', '查询服务注册', '', '2021-11-15 15:43:29', '0', '800900000000', '/callComponent/serviceRegisterManage/list', '700201908030');
INSERT INTO `p_privilege` VALUES ('502021111540930062', '缓存查询', '', '2021-11-15 15:48:44', '0', '800900000000', '/callComponent/cacheManage/list', '700201908032');
INSERT INTO `p_privilege` VALUES ('502021111541150265', '删除报表组', '', '2021-11-15 17:15:59', '0', '800900000000', '/app/reportCustomGroup.deleteReportCustomGroup', '702021110981050126');
INSERT INTO `p_privilege` VALUES ('502021111542140083', '查询服务实现', '', '2021-11-15 16:44:37', '0', '800900000000', '/callComponent/serviceImplManage/list', '700201906027');
INSERT INTO `p_privilege` VALUES ('502021111542240273', '修改报表组件', '', '2021-11-15 17:22:31', '0', '800900000000', '/app/reportCustomComponent.updateReportCustomComponent', '702021111211780405');
INSERT INTO `p_privilege` VALUES ('502021111542570057', '添加服务注册', '', '2021-11-15 15:42:51', '0', '800900000000', '/callComponent/serviceBinding/binding', '700201908030');
INSERT INTO `p_privilege` VALUES ('502021111542850075', '删除服务实现', '', '2021-11-15 16:17:04', '0', '800900000000', '/callComponent/deleteServiceImpl/delete', '700201906027');
INSERT INTO `p_privilege` VALUES ('502021111546210262', '查询报表组', '', '2021-11-15 17:13:26', '1', '800900000000', '/app/reportCustomGroup.listReportCustomGroup', '702021110981050126');
INSERT INTO `p_privilege` VALUES ('502021111556820267', '保存报表信息', '', '2021-11-15 17:18:33', '0', '800900000000', '/app/reportCustom.saveReportCustom', '702021111293630375');
INSERT INTO `p_privilege` VALUES ('502021111558720257', '保存手机菜单', '', '2021-11-15 17:05:03', '0', '800900000001', '/app/housekeepingType/saveHousekeepingType', '702021060521430094');
INSERT INTO `p_privilege` VALUES ('502021111559700088', '删除服务提供', '', '2021-11-15 16:48:26', '0', '800900000000', '/callComponent/deleteServiceProvide/delete', '702019090161010006');
INSERT INTO `p_privilege` VALUES ('502021111560550082', '查询编码映射', '', '2021-11-15 16:41:55', '0', '800900000000', '/callComponent/mappingManage/list', '700201906028');
INSERT INTO `p_privilege` VALUES ('502021111562530061', '修改服务', '', '2021-11-15 15:45:53', '0', '800900000000', '/app/editService/update', '700201906026');
INSERT INTO `p_privilege` VALUES ('502021111563500071', '修改编码映射', '', '2021-11-15 16:09:20', '0', '800900000000', '/callComponent/editMapping/update', '700201906028');
INSERT INTO `p_privilege` VALUES ('502021111565140074', '修改服务实现', '', '2021-11-15 16:15:55', '0', '800900000000', '/callComponent/editServiceImpl/update', '700201906027');
INSERT INTO `p_privilege` VALUES ('502021111565780073', '新增服务实现', '', '2021-11-15 16:15:28', '0', '800900000000', '/callComponent/addServiceImpl/save', '700201906027');
INSERT INTO `p_privilege` VALUES ('502021111570020263', '保存报表组', '', '2021-11-15 17:14:38', '0', '800900000000', '/app/reportCustomGroup.saveReportCustomGroup', '702021110981050126');
INSERT INTO `p_privilege` VALUES ('502021111570580056', '删除服务注册', '', '2021-11-15 15:41:46', '0', '800900000000', '/callComponent/deleteServiceRegister/delete', '700201908030');
INSERT INTO `p_privilege` VALUES ('502021111570920060', '删除服务', '', '2021-11-15 15:45:13', '0', '800900000000', '/app/deleteService/delete', '700201906026');
INSERT INTO `p_privilege` VALUES ('502021111575090253', '删除菜单权限', '', '2021-11-15 17:02:00', '0', '800900000000', '/callComponent/deleteMenu/delete', '700201908035');
INSERT INTO `p_privilege` VALUES ('502021111575150270', '查询报表组件', '', '2021-11-15 17:21:32', '1', '800900000000', '/app/reportCustomComponent.listReportCustomComponent', '702021111211780405');
INSERT INTO `p_privilege` VALUES ('502021111575780246', '新增菜单组', '', '2021-11-15 16:58:42', '0', '800900000000', '/callComponent/addMenuGroup/save', '700201908033');
INSERT INTO `p_privilege` VALUES ('502021111577680254', '新增菜单权限', '', '2021-11-15 17:02:55', '0', '800900000000', '/callComponent/configMenuBinding/binding', '700201908035');
INSERT INTO `p_privilege` VALUES ('502021111591850268', '修改报表信息', '', '2021-11-15 17:19:00', '0', '800900000000', '/app/reportCustom.updateReportCustom', '702021111293630375');
INSERT INTO `p_privilege` VALUES ('502021111592830260', '删除手机菜单权限', '', '2021-11-15 17:06:28', '0', '800900000001', '/app/housekeepingType/deleteHousekeepingType', '702021060521430094');
INSERT INTO `p_privilege` VALUES ('502021111594010084', '查询服务提供', '', '2021-11-15 16:47:01', '0', '800900000000', '/callComponent/serviceProvideManage/list', '702019090161010006');
INSERT INTO `p_privilege` VALUES ('502021111596180059', '添加服务', '', '2021-11-15 15:44:46', '0', '800900000000', '/app/addService/save', '700201906026');
INSERT INTO `p_privilege` VALUES ('502021111598200264', '修改报表组', '', '2021-11-15 17:15:01', '0', '800900000000', '/app/reportCustomGroup.updateReportCustomGroup', '702021110981050126');
INSERT INTO `p_privilege` VALUES ('502021111599410269', '删除报表信息', '', '2021-11-15 17:19:31', '0', '800900000000', '/app/reportCustom.deleteReportCustom', '702021111293630375');
INSERT INTO `p_privilege` VALUES ('502022022699820647', '菜单目录', '', '2022-02-26 10:32:55', '0', '800900000000', '/admin.html#/pages/dev/menuCatalogManage', '702022022626440646');
INSERT INTO `p_privilege` VALUES ('502022070752510038', '字典', '字典', '2022-07-07 09:26:35', '0', '800900000000', '/#/pages/dev/dictManage', '702022070767920037');
INSERT INTO `p_privilege` VALUES ('502022081711010004', '系统配置', '', '2022-08-17 00:15:09', '0', '800900000000', '/#/pages/frame/systemInfoManage', '702022081723230003');
INSERT INTO `p_privilege` VALUES ('502023011023480002', '注册协议', '', '2023-01-10 17:20:57', '0', '800900000000', '/#/pages/frame/registerProtocol', '702023011061770001');
INSERT INTO `p_privilege` VALUES ('502023012957620005', '配置项', '', '2023-01-29 14:38:19', '0', '800900000000', '/#/pages/dev/mappingDomainManage', '702023012914280004');
INSERT INTO `p_privilege` VALUES ('502023021306130020', '小区信息', '', '2023-02-13 00:51:29', '0', '800900000001', '/#/pages/common/communityManage', '702023021378840019');
INSERT INTO `p_privilege` VALUES ('502023021308870008', 'sdf', '', '2023-02-13 01:00:28', '0', '800900000001', 'sdf', '702023021396940007');
INSERT INTO `p_privilege` VALUES ('502023021322510009', 'gg', 'gg', '2023-02-13 00:36:56', '1', '800900000000', 'gg', '700201906025');
INSERT INTO `p_privilege` VALUES ('502023021337240017', '小区信息', '', '2023-02-13 00:44:13', '0', '800900000001', '/#/pages/common/communityManage', '702023021326530016');
INSERT INTO `p_privilege` VALUES ('502023021381120002', '小区信息', '', '2023-02-13 00:56:49', '0', '800900000001', '/#/pages/common/communityManage', '702023021322800001');

-- ----------------------------
-- Table structure for p_privilege_group
-- ----------------------------
DROP TABLE IF EXISTS `p_privilege_group`;
CREATE TABLE `p_privilege_group` (
  `pg_id` varchar(30) NOT NULL COMMENT '权限组ID',
  `name` varchar(10) NOT NULL COMMENT '权限组名称',
  `description` varchar(200) DEFAULT NULL COMMENT '权限组描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  `store_id` varchar(30) NOT NULL DEFAULT '9999' COMMENT '商户ID',
  `domain` varchar(12) NOT NULL DEFAULT '-1' COMMENT '权限组域 商户详见store_type store_type_cd',
  `role_type` varchar(12) NOT NULL DEFAULT 'USER' COMMENT 'SYSTEM 系统角色 USER 用户角色',
  PRIMARY KEY (`pg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of p_privilege_group
-- ----------------------------
INSERT INTO `p_privilege_group` VALUES ('600201904002', '物业管理员权限组', '物业管理员权限组', '2019-04-01 00:34:58', '0', '9999', '800900000003', 'SYSTEM');
INSERT INTO `p_privilege_group` VALUES ('600201906005', '运营团队管理员权限组', '运营团队管理员权限组', '2019-03-31 18:22:10', '0', '9999', '800900000001', 'SYSTEM');
INSERT INTO `p_privilege_group` VALUES ('600201906007', '开发团队管理员权限组', '开发团队管理员权限组', '2019-03-31 18:22:10', '0', '9999', '800900000000', 'SYSTEM');

-- ----------------------------
-- Table structure for p_privilege_rel
-- ----------------------------
DROP TABLE IF EXISTS `p_privilege_rel`;
CREATE TABLE `p_privilege_rel` (
  `rel_id` varchar(30) NOT NULL COMMENT '权限关系ID',
  `p_id` varchar(30) NOT NULL COMMENT '权限ID',
  `pg_id` varchar(30) NOT NULL COMMENT '权限组ID',
  `level_cd` varchar(12) NOT NULL DEFAULT '1001' COMMENT '权限级别，1001 系统级 2002 用户级',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  PRIMARY KEY (`rel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of p_privilege_rel
-- ----------------------------
INSERT INTO `p_privilege_rel` VALUES ('102023021366050002', '502023021308870008', '600201906005', '1001', '2023-02-13 01:00:28', '0');
INSERT INTO `p_privilege_rel` VALUES ('102023021368860001', '502023021381120002', '600201906005', '1001', '2023-02-13 00:56:50', '0');
INSERT INTO `p_privilege_rel` VALUES ('102023021371280003', '502023021306130020', '600201906005', '1001', '2023-02-13 00:51:29', '0');
INSERT INTO `p_privilege_rel` VALUES ('102023021396200002', '502023021337240017', '600201906005', '1001', '2023-02-13 00:44:13', '0');
INSERT INTO `p_privilege_rel` VALUES ('10938', '502020100477560041', '600201906007', '1001', '2020-10-04 23:19:33', '0');
INSERT INTO `p_privilege_rel` VALUES ('111', '500201907032', '600201906007', '1001', '2019-08-10 09:52:13', '0');
INSERT INTO `p_privilege_rel` VALUES ('113', '500201908034', '600201906007', '1001', '2019-08-13 11:33:58', '0');
INSERT INTO `p_privilege_rel` VALUES ('114', '500201908035', '600201906007', '1001', '2019-08-19 12:16:34', '0');
INSERT INTO `p_privilege_rel` VALUES ('115', '500201908036', '600201906007', '1001', '2019-08-19 12:16:34', '0');
INSERT INTO `p_privilege_rel` VALUES ('117', '502019082521880003', '600201906007', '1001', '2019-08-25 10:41:16', '0');
INSERT INTO `p_privilege_rel` VALUES ('119', '502019083153490001', '600201906007', '1001', '2019-08-31 14:39:06', '0');
INSERT INTO `p_privilege_rel` VALUES ('124', '502019090149430005', '600201906007', '1001', '2019-09-01 14:14:08', '0');
INSERT INTO `p_privilege_rel` VALUES ('125', '502019090260990002', '600201906007', '1001', '2019-09-02 12:35:51', '0');
INSERT INTO `p_privilege_rel` VALUES ('126', '502019090277380001', '600201906007', '1001', '2019-09-02 15:33:31', '0');
INSERT INTO `p_privilege_rel` VALUES ('17578', '502020111627880006', '600201906007', '1001', '2020-11-16 01:09:05', '0');
INSERT INTO `p_privilege_rel` VALUES ('21544', '502020120765630299', '600201906007', '1001', '2020-12-07 18:14:35', '0');
INSERT INTO `p_privilege_rel` VALUES ('29176', '502021031645040072', '600201906007', '1001', '2021-03-16 11:14:16', '0');
INSERT INTO `p_privilege_rel` VALUES ('31474', '502021041077920105', '600201906007', '1001', '2021-04-10 22:07:21', '0');
INSERT INTO `p_privilege_rel` VALUES ('36874', '502021060583310095', '600201906007', '1001', '2021-06-05 15:38:42', '0');
INSERT INTO `p_privilege_rel` VALUES ('38', '500201906023', '600201906007', '1001', '2019-06-19 16:13:32', '0');
INSERT INTO `p_privilege_rel` VALUES ('41374', '502021080561750004', '600201906007', '1001', '2021-08-05 19:09:43', '0');
INSERT INTO `p_privilege_rel` VALUES ('4143', '502020073172490003', '600201906007', '1001', '2020-07-31 13:30:23', '0');
INSERT INTO `p_privilege_rel` VALUES ('4144', '502020073133960005', '600201906007', '1001', '2020-07-31 13:31:59', '0');
INSERT INTO `p_privilege_rel` VALUES ('4145', '502020073158140002', '600201906007', '1001', '2020-07-31 13:41:05', '0');
INSERT INTO `p_privilege_rel` VALUES ('4485', '502020081431630210', '600201906007', '1001', '2020-08-14 22:17:25', '0');
INSERT INTO `p_privilege_rel` VALUES ('4486', '502020081421160220', '600201906007', '1001', '2020-08-14 23:22:48', '0');
INSERT INTO `p_privilege_rel` VALUES ('46786', '502021091295510202', '600201906007', '1001', '2021-09-12 15:48:39', '0');
INSERT INTO `p_privilege_rel` VALUES ('53598', '502021110374581810', '600201906007', '1001', '2021-11-03 20:38:49', '0');
INSERT INTO `p_privilege_rel` VALUES ('54455', '502021110974670127', '600201906007', '1001', '2021-11-09 14:38:40', '0');
INSERT INTO `p_privilege_rel` VALUES ('55280', '502021111280010376', '600201906007', '1001', '2021-11-12 09:03:45', '0');
INSERT INTO `p_privilege_rel` VALUES ('55281', '502021111231120406', '600201906007', '1001', '2021-11-12 10:00:34', '0');
INSERT INTO `p_privilege_rel` VALUES ('55493', '502021111404460005', '600201906007', '1001', '2021-11-14 23:44:49', '0');
INSERT INTO `p_privilege_rel` VALUES ('55533', '502021111570580056', '600201906007', '1001', '2021-11-15 15:41:46', '0');
INSERT INTO `p_privilege_rel` VALUES ('55534', '502021111542570057', '600201906007', '1001', '2021-11-15 15:42:51', '0');
INSERT INTO `p_privilege_rel` VALUES ('55535', '502021111540820058', '600201906007', '1001', '2021-11-15 15:43:29', '0');
INSERT INTO `p_privilege_rel` VALUES ('55536', '502021111596180059', '600201906007', '1001', '2021-11-15 15:44:46', '0');
INSERT INTO `p_privilege_rel` VALUES ('55537', '502021111570920060', '600201906007', '1001', '2021-11-15 15:45:13', '0');
INSERT INTO `p_privilege_rel` VALUES ('55538', '502021111562530061', '600201906007', '1001', '2021-11-15 15:45:53', '0');
INSERT INTO `p_privilege_rel` VALUES ('55552', '502021111540930062', '600201906007', '1001', '2021-11-15 15:48:44', '0');
INSERT INTO `p_privilege_rel` VALUES ('55553', '502021111510430063', '600201906007', '1001', '2021-11-15 15:49:22', '0');
INSERT INTO `p_privilege_rel` VALUES ('55554', '502021111540410070', '600201906007', '1001', '2021-11-15 16:08:32', '0');
INSERT INTO `p_privilege_rel` VALUES ('55555', '502021111563500071', '600201906007', '1001', '2021-11-15 16:09:20', '0');
INSERT INTO `p_privilege_rel` VALUES ('55556', '502021111510210072', '600201906007', '1001', '2021-11-15 16:10:03', '0');
INSERT INTO `p_privilege_rel` VALUES ('55557', '502021111565780073', '600201906007', '1001', '2021-11-15 16:15:28', '0');
INSERT INTO `p_privilege_rel` VALUES ('55558', '502021111565140074', '600201906007', '1001', '2021-11-15 16:15:55', '0');
INSERT INTO `p_privilege_rel` VALUES ('55559', '502021111542850075', '600201906007', '1001', '2021-11-15 16:17:04', '0');
INSERT INTO `p_privilege_rel` VALUES ('55560', '502021111560550082', '600201906007', '1001', '2021-11-15 16:41:55', '0');
INSERT INTO `p_privilege_rel` VALUES ('55696', '502021111542140083', '600201906007', '1001', '2021-11-15 16:44:37', '0');
INSERT INTO `p_privilege_rel` VALUES ('55697', '502021111594010084', '600201906007', '1001', '2021-11-15 16:47:01', '0');
INSERT INTO `p_privilege_rel` VALUES ('55698', '502021111517720085', '600201906007', '1001', '2021-11-15 16:47:34', '0');
INSERT INTO `p_privilege_rel` VALUES ('55699', '502021111559700088', '600201906007', '1001', '2021-11-15 16:48:26', '0');
INSERT INTO `p_privilege_rel` VALUES ('55700', '502021111504290091', '600201906007', '1001', '2021-11-15 16:49:36', '0');
INSERT INTO `p_privilege_rel` VALUES ('55701', '502021111538930243', '600201906007', '1001', '2021-11-15 16:57:21', '0');
INSERT INTO `p_privilege_rel` VALUES ('55702', '502021111575780246', '600201906007', '1001', '2021-11-15 16:58:42', '0');
INSERT INTO `p_privilege_rel` VALUES ('55703', '502021111501470249', '600201906007', '1001', '2021-11-15 16:59:30', '0');
INSERT INTO `p_privilege_rel` VALUES ('55704', '502021111503660250', '600201906007', '1001', '2021-11-15 17:00:53', '0');
INSERT INTO `p_privilege_rel` VALUES ('55705', '502021111531030252', '600201906007', '1001', '2021-11-15 17:01:34', '0');
INSERT INTO `p_privilege_rel` VALUES ('55706', '502021111575090253', '600201906007', '1001', '2021-11-15 17:02:00', '0');
INSERT INTO `p_privilege_rel` VALUES ('55707', '502021111577680254', '600201906007', '1001', '2021-11-15 17:02:55', '0');
INSERT INTO `p_privilege_rel` VALUES ('55709', '502021111558720257', '600201906007', '1001', '2021-11-15 17:05:03', '0');
INSERT INTO `p_privilege_rel` VALUES ('55710', '502021111514690259', '600201906007', '1001', '2021-11-15 17:05:48', '0');
INSERT INTO `p_privilege_rel` VALUES ('55711', '502021111592830260', '600201906007', '1001', '2021-11-15 17:06:28', '0');
INSERT INTO `p_privilege_rel` VALUES ('55713', '502021111570020263', '600201906007', '1001', '2021-11-15 17:14:38', '0');
INSERT INTO `p_privilege_rel` VALUES ('55714', '502021111598200264', '600201906007', '1001', '2021-11-15 17:15:01', '0');
INSERT INTO `p_privilege_rel` VALUES ('55715', '502021111541150265', '600201906007', '1001', '2021-11-15 17:15:59', '0');
INSERT INTO `p_privilege_rel` VALUES ('55717', '502021111556820267', '600201906007', '1001', '2021-11-15 17:18:33', '0');
INSERT INTO `p_privilege_rel` VALUES ('55718', '502021111591850268', '600201906007', '1001', '2021-11-15 17:19:00', '0');
INSERT INTO `p_privilege_rel` VALUES ('55719', '502021111599410269', '600201906007', '1001', '2021-11-15 17:19:31', '0');
INSERT INTO `p_privilege_rel` VALUES ('55721', '502021111509280272', '600201906007', '1001', '2021-11-15 17:21:59', '0');
INSERT INTO `p_privilege_rel` VALUES ('55722', '502021111542240273', '600201906007', '1001', '2021-11-15 17:22:31', '0');
INSERT INTO `p_privilege_rel` VALUES ('55723', '502021111529750274', '600201906007', '1001', '2021-11-15 17:22:54', '0');
INSERT INTO `p_privilege_rel` VALUES ('56670', '502021112492100038', '600201906007', '1001', '2021-11-24 14:58:46', '0');
INSERT INTO `p_privilege_rel` VALUES ('61', '500201904001', '600201906007', '1001', '2019-04-19 08:05:29', '0');
INSERT INTO `p_privilege_rel` VALUES ('64', '500201904008', '600201906007', '1001', '2019-04-19 08:05:29', '0');
INSERT INTO `p_privilege_rel` VALUES ('65', '500201904009', '600201906007', '1001', '2019-04-19 08:05:29', '0');
INSERT INTO `p_privilege_rel` VALUES ('69146', '502022022699820647', '600201906007', '1001', '2022-02-26 10:32:55', '0');
INSERT INTO `p_privilege_rel` VALUES ('71538', '502022070752510038', '600201906007', '1001', '2022-07-07 09:26:35', '0');
INSERT INTO `p_privilege_rel` VALUES ('71732', '502022081711010004', '600201906007', '1001', '2022-08-17 00:15:09', '0');
INSERT INTO `p_privilege_rel` VALUES ('74518', '502023011023480002', '600201906007', '1001', '2023-01-10 17:20:57', '0');
INSERT INTO `p_privilege_rel` VALUES ('74746', '502023012957620005', '600201906007', '1001', '2023-01-29 14:38:19', '0');
INSERT INTO `p_privilege_rel` VALUES ('80', '500201906026', '600201906007', '1001', '2019-04-19 08:05:29', '0');
INSERT INTO `p_privilege_rel` VALUES ('81', '500201906027', '600201906007', '1001', '2019-04-19 08:05:29', '0');
INSERT INTO `p_privilege_rel` VALUES ('82', '500201906028', '600201906007', '1001', '2019-07-07 16:46:03', '0');
INSERT INTO `p_privilege_rel` VALUES ('83', '500201906029', '600201906007', '1001', '2019-07-07 16:46:03', '0');
INSERT INTO `p_privilege_rel` VALUES ('84', '500201907030', '600201906007', '1001', '2019-07-11 11:47:32', '0');

-- ----------------------------
-- Table structure for p_privilege_user
-- ----------------------------
DROP TABLE IF EXISTS `p_privilege_user`;
CREATE TABLE `p_privilege_user` (
  `pu_id` varchar(30) NOT NULL COMMENT '主键ID',
  `p_id` varchar(30) NOT NULL COMMENT '权限标志 是 1是权限组 0是权限',
  `privilege_flag` varchar(4) NOT NULL DEFAULT '0' COMMENT '权限标志 是 1是权限组 0是权限',
  `user_id` varchar(30) NOT NULL COMMENT '用户ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  `store_id` varchar(30) NOT NULL COMMENT '商户ID',
  PRIMARY KEY (`pu_id`),
  UNIQUE KEY `pu_id` (`pu_id`) USING BTREE,
  KEY `index_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of p_privilege_user
-- ----------------------------
INSERT INTO `p_privilege_user` VALUES ('42', '600201906007', '1', '30520751775595118592', '2019-04-01 08:37:00', '0', '400000000000000002');

-- ----------------------------
-- Table structure for s_community
-- ----------------------------
DROP TABLE IF EXISTS `s_community`;
CREATE TABLE `s_community` (
  `community_id` varchar(30) NOT NULL COMMENT '小区ID',
  `name` varchar(100) NOT NULL COMMENT '小区名称',
  `address` varchar(200) NOT NULL COMMENT '小区地址',
  `city_code` varchar(12) NOT NULL COMMENT '根据定位获取城市编码',
  `tel` varchar(200) DEFAULT NULL COMMENT '联系方式',
  `ext_community_id` varchar(30) NOT NULL COMMENT '外部编码',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，S 保存，0, 在用 1失效',
  PRIMARY KEY (`community_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of s_community
-- ----------------------------

-- ----------------------------
-- Table structure for s_community_member
-- ----------------------------
DROP TABLE IF EXISTS `s_community_member`;
CREATE TABLE `s_community_member` (
  `community_member_id` varchar(30) NOT NULL COMMENT 'ID',
  `community_id` varchar(30) NOT NULL COMMENT '小区ID',
  `member_id` varchar(50) NOT NULL COMMENT '成员ID',
  `member_name` varchar(50) NOT NULL COMMENT '成员名称',
  `member_type_cd` varchar(12) NOT NULL COMMENT '成员类型见 community_member_type表',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(4) NOT NULL DEFAULT '0' COMMENT '数据状态',
  `start_time` datetime NOT NULL COMMENT '起租时间',
  `end_time` datetime NOT NULL COMMENT '截租时间',
  PRIMARY KEY (`community_member_id`),
  KEY `idx_s_community_member_id` (`community_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of s_community_member
-- ----------------------------

-- ----------------------------
-- Table structure for s_store
-- ----------------------------
DROP TABLE IF EXISTS `s_store`;
CREATE TABLE `s_store` (
  `store_id` varchar(30) NOT NULL COMMENT '商店ID',
  `name` varchar(100) NOT NULL COMMENT '店铺名称',
  `address` varchar(200) NOT NULL COMMENT '店铺地址',
  `tel` varchar(11) NOT NULL COMMENT '电话',
  `store_type_cd` varchar(12) DEFAULT NULL,
  `state` varchar(12) NOT NULL DEFAULT '48001' COMMENT '48001 商户正常状态 48002 限制商户登录',
  `create_user_id` varchar(30) NOT NULL COMMENT '用户ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，S 保存，0, 在用 1失效',
  PRIMARY KEY (`store_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商户表';

-- ----------------------------
-- Records of s_store
-- ----------------------------
INSERT INTO `s_store` VALUES ('400000000000000001', 'HC小区运营团队', '广东省东莞市振兴路197号', '15818220066', '800900000001', '48001', '1', '2018-12-12 05:47:34', '0');
INSERT INTO `s_store` VALUES ('400000000000000002', 'HC小区开发团队', '广东省东莞市振兴路197号', '15818220066', '800900000000', '48001', '1', '2018-12-12 05:47:34', '0');

-- ----------------------------
-- Table structure for s_store_staff
-- ----------------------------
DROP TABLE IF EXISTS `s_store_staff`;
CREATE TABLE `s_store_staff` (
  `store_staff_id` varchar(30) NOT NULL COMMENT '代理商用户ID',
  `store_id` varchar(30) NOT NULL COMMENT '商户ID',
  `staff_id` varchar(30) NOT NULL COMMENT '用户ID',
  `staff_name` varchar(64) NOT NULL COMMENT '用户名称',
  `tel` varchar(11) NOT NULL COMMENT '手机号',
  `admin_flag` varchar(30) NOT NULL DEFAULT 'N' COMMENT '管理员 Y 是 N 否',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，S 保存，0, 在用 1失效',
  PRIMARY KEY (`store_staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of s_store_staff
-- ----------------------------
INSERT INTO `s_store_staff` VALUES ('452019013038670004', '400000000000000002', '30520751775595118592', 'dev', '17797173944', 'Y', '2019-03-30 08:31:29', '0');
INSERT INTO `s_store_staff` VALUES ('452019062708080001', '400000000000000001', '30518939884421922816', 'dev', '17797173944', 'Y', '2019-03-30 08:31:29', '0');

-- ----------------------------
-- Table structure for store_type
-- ----------------------------
DROP TABLE IF EXISTS `store_type`;
CREATE TABLE `store_type` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `store_type_cd` varchar(12) NOT NULL COMMENT '店铺编码',
  `name` varchar(50) NOT NULL COMMENT '店铺种类编码',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_show` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `store_type_cd` (`store_type_cd`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of store_type
-- ----------------------------
INSERT INTO `store_type` VALUES ('1', '800900000001', '运营团队', '运营团队', '2018-12-12 12:09:02', '1');
INSERT INTO `store_type` VALUES ('3', '800900000003', '物业', '物业', '2018-12-12 12:27:11', '0');
INSERT INTO `store_type` VALUES ('9', '800900000000', '开发团队', '开发团队', '2018-12-12 12:09:02', '1');

-- ----------------------------
-- Table structure for t_dict
-- ----------------------------
DROP TABLE IF EXISTS `t_dict`;
CREATE TABLE `t_dict` (
  `id` varchar(30) NOT NULL COMMENT 'id',
  `status_cd` varchar(64) NOT NULL COMMENT '表字段状态 取值',
  `name` varchar(50) NOT NULL COMMENT '名称',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `table_name` varchar(50) DEFAULT NULL,
  `table_columns` varchar(50) DEFAULT NULL COMMENT '表字段说明',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of t_dict
-- ----------------------------
INSERT INTO `t_dict` VALUES ('307', 'http://iot-community', '资产服务(iot-community)', '资产服务(iot-community)', '2019-11-11 21:54:14', 'c_service', 'url');
INSERT INTO `t_dict` VALUES ('309', 'http://iot-dev', '开发服务(iot-dev)', '开发服务(iot-dev)', '2019-11-11 21:54:14', 'c_service', 'url');
INSERT INTO `t_dict` VALUES ('310', 'http://iot-user', '用户服务(iot-user)', '用户服务(iot-user)', '2019-11-11 21:54:14', 'c_service', 'url');
INSERT INTO `t_dict` VALUES ('312', 'http://iot-job', '定时服务(iot-job)', '定时服务(iot-job)', '2019-11-11 21:54:14', 'c_service', 'url');

-- ----------------------------
-- Table structure for t_dict_spec
-- ----------------------------
DROP TABLE IF EXISTS `t_dict_spec`;
CREATE TABLE `t_dict_spec` (
  `spec_id` varchar(30) NOT NULL COMMENT '规格ID',
  `spec_name` varchar(64) NOT NULL COMMENT '规格名称',
  `table_name` varchar(64) NOT NULL COMMENT '表名称',
  `table_columns` varchar(64) NOT NULL COMMENT '类名称',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，S 保存，0, 在用 1失效',
  PRIMARY KEY (`spec_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of t_dict_spec
-- ----------------------------

-- ----------------------------
-- Table structure for u_org
-- ----------------------------
DROP TABLE IF EXISTS `u_org`;
CREATE TABLE `u_org` (
  `org_id` varchar(30) NOT NULL COMMENT '组织ID',
  `b_id` varchar(30) DEFAULT NULL COMMENT '业务ID',
  `store_id` varchar(30) NOT NULL COMMENT '商户ID',
  `org_name` varchar(200) NOT NULL COMMENT '组织名称',
  `org_level` varchar(10) NOT NULL COMMENT '组织级别1 公司级 2 分公司级，3 部门级 查看t_dict表',
  `parent_org_id` varchar(30) NOT NULL COMMENT '上级组织ID，一级时填写org_id',
  `description` varchar(200) DEFAULT NULL COMMENT '组织描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  `belong_community_id` varchar(30) NOT NULL COMMENT '归属小区，9999 归属于入驻的所有小区，否则是为小区ID',
  `allow_operation` varchar(12) NOT NULL DEFAULT 'T' COMMENT '允许前台操作数据，T可以，F不可以',
  PRIMARY KEY (`org_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of u_org
-- ----------------------------

-- ----------------------------
-- Table structure for u_org_community
-- ----------------------------
DROP TABLE IF EXISTS `u_org_community`;
CREATE TABLE `u_org_community` (
  `org_community_id` varchar(30) NOT NULL COMMENT '组织ID',
  `org_id` varchar(30) NOT NULL COMMENT '组织ID',
  `community_id` varchar(30) NOT NULL COMMENT '小区ID',
  `community_name` varchar(200) NOT NULL COMMENT '小区名称',
  `b_id` varchar(30) NOT NULL COMMENT '业务Id',
  `store_id` varchar(30) NOT NULL COMMENT '商户ID',
  `org_name` varchar(200) NOT NULL COMMENT '组织名称',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of u_org_community
-- ----------------------------

-- ----------------------------
-- Table structure for u_org_staff_rel
-- ----------------------------
DROP TABLE IF EXISTS `u_org_staff_rel`;
CREATE TABLE `u_org_staff_rel` (
  `rel_id` varchar(30) NOT NULL COMMENT '组织员工ID',
  `b_id` varchar(30) DEFAULT NULL COMMENT '业务ID',
  `org_id` varchar(30) NOT NULL COMMENT '组织ID',
  `staff_id` varchar(30) NOT NULL COMMENT '员工ID',
  `rel_cd` varchar(30) NOT NULL COMMENT '关系角色，10000 普通员工， 20000部门经理 查看t_dict表',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  `store_id` varchar(30) NOT NULL COMMENT '商户ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of u_org_staff_rel
-- ----------------------------

-- ----------------------------
-- Table structure for u_role_community
-- ----------------------------
DROP TABLE IF EXISTS `u_role_community`;
CREATE TABLE `u_role_community` (
  `rc_id` varchar(30) NOT NULL COMMENT '主键ID',
  `role_id` varchar(30) NOT NULL COMMENT '权限标志 是 1是权限组 0是权限',
  `community_id` varchar(30) NOT NULL COMMENT '小区ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，0在用，1失效',
  `store_id` varchar(30) NOT NULL COMMENT '商户ID',
  `community_name` varchar(128) NOT NULL COMMENT '小区名称',
  PRIMARY KEY (`rc_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of u_role_community
-- ----------------------------

-- ----------------------------
-- Table structure for u_user
-- ----------------------------
DROP TABLE IF EXISTS `u_user`;
CREATE TABLE `u_user` (
  `user_id` varchar(30) NOT NULL COMMENT '用户ID',
  `name` varchar(50) NOT NULL COMMENT '名称',
  `email` varchar(30) DEFAULT NULL COMMENT '邮箱地址',
  `address` varchar(200) DEFAULT NULL COMMENT '现居住地址',
  `password` varchar(128) DEFAULT NULL COMMENT '用户密码，加密过后',
  `sex` varchar(1) DEFAULT NULL COMMENT '性别，0表示男孩 1表示女孩',
  `tel` varchar(11) NOT NULL,
  `level_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '用户级别,关联user_level',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status_cd` varchar(2) NOT NULL DEFAULT '0' COMMENT '数据状态，详细参考c_status表，S 保存，0, 在用 1失效',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户';

-- ----------------------------
-- Records of u_user
-- ----------------------------
INSERT INTO `u_user` VALUES ('30518939884421922816', 'admin', 'sss@sf.com', 'ccc地方都是', '3ad352384261cbc2c7462210dbb3ce61', '0', '17797173944', '00', '2018-12-02 16:01:50', '0');
INSERT INTO `u_user` VALUES ('30520751775595118592', 'dev', '129@qq.com', 'Qingdao', '109c333b381580e1fe8a5ff630c398a7', '0', '17797173942', '00', '2018-12-07 16:01:38', '0');

-- ----------------------------
-- Table structure for unitem_log
-- ----------------------------
DROP TABLE IF EXISTS `unitem_log`;
CREATE TABLE `unitem_log` (
  `b_id` varchar(30) NOT NULL COMMENT '业务Id',
  `o_id` varchar(30) NOT NULL COMMENT '订单ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `service_name` varchar(256) DEFAULT NULL COMMENT '服务名称，事务失败时回滚调用微服务名称',
  `log_text` longtext NOT NULL COMMENT '回滚内容',
  `status_cd` varchar(2) NOT NULL COMMENT '数据状态，详细参考c_status表',
  UNIQUE KEY `b_id` (`b_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of unitem_log
-- ----------------------------
INSERT INTO `unitem_log` VALUES ('202023021212340042', '102023021271470041', '2023-02-12 23:19:43', 'iot-user', '{\"afterValue\":[{\"g_id\":\"\'802020111618520004\'\",\"create_time\":\"\'2022-02-26 13:59:39\'\",\"status_cd\":\"\'1\'\",\"gc_id\":\"\'102022022629600028\'\",\"store_type\":\"\'800900000000\'\",\"ca_id\":\"\'102022022695140008\'\"}],\"preValue\":[{\"g_id\":\"\'802020111618520004\'\",\"create_time\":\"\'2022-02-26 13:59:39\'\",\"status_cd\":\"\'0\'\",\"gc_id\":\"\'102022022629600028\'\",\"store_type\":\"\'800900000000\'\",\"ca_id\":\"\'102022022695140008\'\"}]}', '0');
INSERT INTO `unitem_log` VALUES ('202023021214110025', '102023021228030024', '2023-02-12 20:22:54', 'iot-dev', '{\"afterValue\":[{\"action_obj_his\":\"\'ss\'\",\"create_time\":\"\'2023-02-12 20:20:23\'\",\"status_cd\":\"\'1\'\",\"action_obj\":\"\'ss\'\",\"business_type_cd\":\"\'abcvb\'\",\"action\":\"\'ADD\'\",\"remark\":\"\'ss\'\",\"his_id\":\"\'102023021205810018\'\"}],\"preValue\":[{\"action_obj_his\":\"\'ss\'\",\"create_time\":\"\'2023-02-12 20:20:23\'\",\"status_cd\":\"\'0\'\",\"action_obj\":\"\'ss\'\",\"business_type_cd\":\"\'abcvb\'\",\"action\":\"\'ADD\'\",\"remark\":\"\'ss\'\",\"his_id\":\"\'102023021205810018\'\"}]}', '0');
INSERT INTO `unitem_log` VALUES ('202023021234900019', '102023021296490018', '2023-02-12 20:20:23', 'iot-dev', '{\"afterValue\":[{\"action_obj_his\":\"\'ss\'\",\"action_obj\":\"\'ss\'\",\"business_type_cd\":\"\'abc\'\",\"action\":\"\'ADD\'\",\"remark\":\"\'ss\'\",\"his_id\":\"\'102023021205810018\'\"}],\"preValue\":[]}', '0');
INSERT INTO `unitem_log` VALUES ('202023021240720032', '102023021280680031', '2023-02-12 20:36:59', 'iot-user', '{\"afterValue\":[{\"create_time\":\"\'2022-02-26 11:39:18\'\",\"status_cd\":\"\'0\'\",\"name\":\"\'首页\'\",\"icon\":\"\'fa fa-home\'\",\"store_type\":\"\'800900000000\'\",\"ca_id\":\"\'102022022695140008\'\",\"seq\":\"\'1\'\",\"url\":\"\'/\'\",\"is_show\":\"\'Y\'\"}],\"preValue\":[{\"create_time\":\"\'2022-02-26 11:39:18\'\",\"status_cd\":\"\'0\'\",\"name\":\"\'首页\'\",\"icon\":\"\'fa fa-home\'\",\"store_type\":\"\'800900000000\'\",\"ca_id\":\"\'102022022695140008\'\",\"seq\":\"\'2\'\",\"url\":\"\'/\'\",\"is_show\":\"\'Y\'\"}]}', '0');
INSERT INTO `unitem_log` VALUES ('202023021253500022', '102023021297930021', '2023-02-12 20:21:42', 'iot-dev', '{\"afterValue\":[{\"action_obj_his\":\"\'ss\'\",\"create_time\":\"\'2023-02-12 20:20:23\'\",\"status_cd\":\"\'0\'\",\"action_obj\":\"\'ss\'\",\"business_type_cd\":\"\'abcvb\'\",\"action\":\"\'ADD\'\",\"remark\":\"\'ss\'\",\"his_id\":\"\'102023021205810018\'\"}],\"preValue\":[{\"action_obj_his\":\"\'ss\'\",\"create_time\":\"\'2023-02-12 20:20:23\'\",\"status_cd\":\"\'0\'\",\"action_obj\":\"\'ss\'\",\"business_type_cd\":\"\'abc\'\",\"action\":\"\'ADD\'\",\"remark\":\"\'ss\'\",\"his_id\":\"\'102023021205810018\'\"}]}', '0');
INSERT INTO `unitem_log` VALUES ('202023021253960035', '102023021267850034', '2023-02-12 20:39:03', 'iot-user', '{\"afterValue\":[{\"name\":\"\'sss\'\",\"icon\":\"\'ss\'\",\"store_type\":\"\'800900000001\'\",\"ca_id\":\"\'102023021279740001\'\",\"seq\":\"\'1\'\",\"url\":\"\'s\'\",\"is_show\":\"\'Y\'\"}],\"preValue\":[]}', '0');
INSERT INTO `unitem_log` VALUES ('202023021287000050', '102023021244850049', '2023-02-12 23:32:44', 'iot-user', '{\"afterValue\":[{\"name\":\"\'首页\'\",\"icon\":\"\'fa fa-home\'\",\"store_type\":\"\'800900000001\'\",\"ca_id\":\"\'102023021260140001\'\",\"seq\":\"\'1\'\",\"url\":\"\'/\'\",\"is_show\":\"\'Y\'\"}],\"preValue\":[]}', '0');
INSERT INTO `unitem_log` VALUES ('202023021293030030', '102023021202770029', '2023-02-12 20:36:45', 'iot-user', '{\"afterValue\":[{\"create_time\":\"\'2022-02-26 11:39:18\'\",\"status_cd\":\"\'0\'\",\"name\":\"\'首页\'\",\"icon\":\"\'fa fa-home\'\",\"store_type\":\"\'800900000000\'\",\"ca_id\":\"\'102022022695140008\'\",\"seq\":\"\'2\'\",\"url\":\"\'/\'\",\"is_show\":\"\'Y\'\"}],\"preValue\":[{\"create_time\":\"\'2022-02-26 11:39:18\'\",\"status_cd\":\"\'0\'\",\"name\":\"\'首页\'\",\"icon\":\"\'fa fa-home\'\",\"store_type\":\"\'800900000000\'\",\"ca_id\":\"\'102022022695140008\'\",\"seq\":\"\'1\'\",\"url\":\"\'/\'\",\"is_show\":\"\'Y\'\"}]}', '0');
INSERT INTO `unitem_log` VALUES ('202023021295050038', '102023021255710037', '2023-02-12 20:39:57', 'iot-user', '{\"afterValue\":[{\"create_time\":\"\'2023-02-12 20:39:04\'\",\"status_cd\":\"\'1\'\",\"name\":\"\'sss\'\",\"icon\":\"\'ss\'\",\"store_type\":\"\'800900000001\'\",\"ca_id\":\"\'102023021279740001\'\",\"seq\":\"\'1\'\",\"url\":\"\'s\'\",\"is_show\":\"\'Y\'\"}],\"preValue\":[{\"create_time\":\"\'2023-02-12 20:39:04\'\",\"status_cd\":\"\'0\'\",\"name\":\"\'sss\'\",\"icon\":\"\'ss\'\",\"store_type\":\"\'800900000001\'\",\"ca_id\":\"\'102023021279740001\'\",\"seq\":\"\'1\'\",\"url\":\"\'s\'\",\"is_show\":\"\'Y\'\"}]}', '0');
INSERT INTO `unitem_log` VALUES ('202023021310210069', '102023021352500068', '2023-02-13 01:04:15', 'iot-dev', '{\"afterValue\":[{\"domain_name\":\"\'通用配置\'\",\"create_time\":\"\'2023-02-13 01:03:08\'\",\"status_cd\":\"\'0\'\",\"domain\":\"\'DOMAIN.COMMON\'\",\"remark\":\"\'通用配置，一般很少配置1\'\",\"id\":\"\'102023021383440013\'\",\"seq\":\"\'10\'\"}],\"preValue\":[{\"domain_name\":\"\'通用配置\'\",\"create_time\":\"\'2023-02-13 01:03:08\'\",\"status_cd\":\"\'0\'\",\"domain\":\"\'DOMAIN.COMMON\'\",\"remark\":\"\'通用配置，一般很少配置\'\",\"id\":\"\'102023021383440013\'\",\"seq\":\"\'10\'\"}]}', '0');
INSERT INTO `unitem_log` VALUES ('202023021332970079', '102023021301050078', '2023-02-13 01:08:43', 'iot-dev', '{\"afterValue\":[{\"domain_name\":\"\'环境配置\'\",\"domain\":\"\'ENV_DOMAIN\'\",\"remark\":\"\'环境相关配置，如演示环境 生产环境 切换\'\",\"id\":\"\'102023021377130023\'\",\"seq\":\"\'1\'\"}],\"preValue\":[]}', '0');
INSERT INTO `unitem_log` VALUES ('202023021333320075', '102023021397120074', '2023-02-13 01:06:59', 'iot-dev', '{\"afterValue\":[{\"domain_name\":\"\'1\'\",\"create_time\":\"\'2023-02-13 01:06:12\'\",\"status_cd\":\"\'1\'\",\"domain\":\"\'1\'\",\"remark\":\"\'1\'\",\"id\":\"\'102023021346210019\'\",\"seq\":\"\'1\'\"}],\"preValue\":[{\"domain_name\":\"\'1\'\",\"create_time\":\"\'2023-02-13 01:06:12\'\",\"status_cd\":\"\'0\'\",\"domain\":\"\'1\'\",\"remark\":\"\'1\'\",\"id\":\"\'102023021346210019\'\",\"seq\":\"\'1\'\"}]}', '0');
INSERT INTO `unitem_log` VALUES ('202023021337390072', '102023021365380071', '2023-02-13 01:06:11', 'iot-dev', '{\"afterValue\":[{\"domain_name\":\"\'1\'\",\"domain\":\"\'1\'\",\"remark\":\"\'1\'\",\"id\":\"\'102023021346210019\'\",\"seq\":\"\'1\'\"}],\"preValue\":[]}', '0');
INSERT INTO `unitem_log` VALUES ('202023021352010066', '102023021342120065', '2023-02-13 01:03:07', 'iot-dev', '{\"afterValue\":[{\"domain_name\":\"\'通用配置\'\",\"domain\":\"\'DOMAIN.COMMON\'\",\"remark\":\"\'通用配置，一般很少配置\'\",\"id\":\"\'102023021383440013\'\",\"seq\":\"\'10\'\"}],\"preValue\":[]}', '0');
INSERT INTO `unitem_log` VALUES ('202023021388900077', '102023021348070076', '2023-02-13 01:08:19', 'iot-dev', '{\"afterValue\":[{\"domain_name\":\"\'系统开关\'\",\"domain\":\"\'SYSTEM_SWITCH\'\",\"remark\":\"\'系统相关开关，比如日志开关，耗时开关等\'\",\"id\":\"\'102023021380610022\'\",\"seq\":\"\'2\'\"}],\"preValue\":[]}', '0');
