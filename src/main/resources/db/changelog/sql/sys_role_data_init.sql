INSERT INTO `sys_role` ( `delete_status`, `code`, `name`, `remark`) VALUES (0, 'test_admin', '测试管理员', '测试管理员') ON DUPLICATE KEY UPDATE `delete_status` = 0;

COMMIT;