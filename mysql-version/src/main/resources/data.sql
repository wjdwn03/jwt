create schema mysqljwt;

use mysqljwt;

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'auto_increment',
  `type` varchar(15) NOT NULL COMMENT 'NORMAL : 일반 회원, KAKAO : 카카오, GOOGLE : 구글, TWITTER : 트위터',
  `email` varchar(100) NOT NULL COMMENT '이메일',
  `password` varchar(100) DEFAULT NULL COMMENT '비밀번호',
  `refresh_token` varchar(200) DEFAULT NULL COMMENT 'refresh 토큰',
  `created_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입일시',
  `updated_date` datetime DEFAULT NULL COMMENT '수정일시',
  `status` char(1) NOT NULL DEFAULT 'A' COMMENT 'A : 활성화, D : 탈퇴',
  PRIMARY KEY (`id`),
  UNIQUE KEY `refresh_token` (`refresh_token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

Insert into user (type, email, password, created_date, status) values ('NORMAL', 'qwer@qwer.com', 'qwer', now(), 'A');