-- ----------------------------
-- Table structure for `sys_resource`
-- ----------------------------
DROP TABLE IF EXISTS `sys_resource`;
CREATE TABLE `sys_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `add_time` datetime DEFAULT NULL,
  `delete_status` bit(1) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `code` varchar(255) NOT NULL,
  `custom_id_name` varchar(255) DEFAULT NULL,
  `icon_class` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `add_user_id` bigint(20) DEFAULT NULL,
  `update_user_id` bigint(20) DEFAULT NULL,
  `parent_res_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_1unub6a3h9s0afpq1ijdwh69b` (`code`),
  KEY `FK90e0rregesjk3exnwd365md3j` (`add_user_id`),
  KEY `FK2w69q0mspo85jqahj3i1hxo44` (`update_user_id`),
  KEY `FK34lsjn7iqxrma9rhh0cqhfuc` (`parent_res_id`),
  CONSTRAINT `FK34lsjn7iqxrma9rhh0cqhfuc` FOREIGN KEY (`parent_res_id`) REFERENCES `sys_resource` (`id`),
  CONSTRAINT `FK2w69q0mspo85jqahj3i1hxo44` FOREIGN KEY (`update_user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `FK90e0rregesjk3exnwd365md3j` FOREIGN KEY (`add_user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_resource
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_role`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `add_time` datetime DEFAULT NULL,
  `delete_status` bit(1) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `code` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `add_user_id` bigint(20) DEFAULT NULL,
  `update_user_id` bigint(20) DEFAULT NULL,
  `brands_entry` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_plpigyqwsqfn7mn66npgf9ftp` (`code`),
  KEY `FK1wlto2yks5nfa28chqvcfe6j` (`add_user_id`),
  KEY `FKgqk9fwnqc874c1uurae3sekea` (`update_user_id`),
  CONSTRAINT `FKgqk9fwnqc874c1uurae3sekea` FOREIGN KEY (`update_user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `FK1wlto2yks5nfa28chqvcfe6j` FOREIGN KEY (`add_user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_role
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_role_resource`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_resource`;
CREATE TABLE `sys_role_resource` (
  `role_id` bigint(20) NOT NULL,
  `resource_id` bigint(20) NOT NULL,
  KEY `FKkj7e3cva1e2s3nsd0yghpbsnk` (`resource_id`),
  KEY `FK7urjh5xeujvp29nihwbs5b9kr` (`role_id`),
  CONSTRAINT `FK7urjh5xeujvp29nihwbs5b9kr` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`),
  CONSTRAINT `FKkj7e3cva1e2s3nsd0yghpbsnk` FOREIGN KEY (`resource_id`) REFERENCES `sys_resource` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_role_resource
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_user`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `add_time` datetime DEFAULT NULL,
  `delete_status` bit(1) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `dept` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `true_name` varchar(255) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `add_user_id` bigint(20) DEFAULT NULL,
  `update_user_id` bigint(20) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_51bvuyvihefoh4kp5syh2jpi4` (`username`),
  KEY `FKqywkrev10lh9qx6t3aikbquex` (`add_user_id`),
  KEY `FKj2ycjehkdiwfg6jme6ly0cxe0` (`update_user_id`),
  KEY `FK4dm5kxn3potpfgdigehw7pdyu` (`role_id`),
  CONSTRAINT `FK4dm5kxn3potpfgdigehw7pdyu` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`),
  CONSTRAINT `FKj2ycjehkdiwfg6jme6ly0cxe0` FOREIGN KEY (`update_user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `FKqywkrev10lh9qx6t3aikbquex` FOREIGN KEY (`add_user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_user
-- ----------------------------