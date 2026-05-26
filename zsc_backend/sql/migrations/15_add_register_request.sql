-- 15_add_register_request.sql
-- 注册申请审核表

DROP TABLE IF EXISTS `biz_register_request`;
CREATE TABLE `biz_register_request` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `email` varchar(50) NOT NULL COMMENT '申请邮箱',
  `note` varchar(500) DEFAULT '' COMMENT '申请说明',
  `role_key` varchar(20) NOT NULL DEFAULT 'user' COMMENT '申请角色',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '0-待审核 1-已通过 2-已拒绝',
  `generated_username` varchar(30) DEFAULT NULL COMMENT '生成的用户名',
  `generated_password` varchar(100) DEFAULT NULL COMMENT '生成的密码(加密)',
  `review_by` varchar(64) DEFAULT NULL COMMENT '审批人',
  `review_time` datetime DEFAULT NULL COMMENT '审批时间',
  `review_comment` varchar(500) DEFAULT NULL COMMENT '审批意见',
  `create_time` datetime DEFAULT NULL COMMENT '申请时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='注册申请审核表';
