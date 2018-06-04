INSERT INTO `sys_user` ( `delete_status`, `password`, `true_name`, `username`) VALUES (0, '10f9a602f299811a90515d88db9aede6', '超级管理员', 'admin') ON DUPLICATE KEY UPDATE `delete_status` = 0;

COMMIT;