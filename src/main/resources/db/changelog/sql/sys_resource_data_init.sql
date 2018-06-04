INSERT INTO `sys_resource` (`id`,`delete_status`, `code`, `name`, `url`, `parent_res_id`,`custom_id_name`,`icon_class`) VALUES (14, 0, 'SYSTEM_MANAGE', '系统管理', NULL, NULL, NULL, 'fa-cogs') ON DUPLICATE KEY UPDATE `delete_status` = 0;

COMMIT;