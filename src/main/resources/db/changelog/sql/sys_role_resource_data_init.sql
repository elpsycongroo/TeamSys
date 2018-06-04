INSERT INTO `sys_role_resource` ( `role_id`, `resource_id`) VALUES ((select `id` from `sys_role` where `name` = '测试管理员'), (select `id` from `sys_resource` where `code` = 'SYSTEM_MANAGE'));

COMMIT;