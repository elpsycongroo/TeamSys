INSERT INTO `sys_user` ( `delete_status`, `password`, `true_name`, `username`, `role_id`) VALUES (0, 'b5c3857d0e93cf9de0f54a34438a341d', '香草_诺蕾姬（测试账号）', 'chiba', (select `id` from `sys_role` where `name` = '测试管理员')) ON DUPLICATE KEY UPDATE `delete_status` = 0;

COMMIT;