CREATE TABLE `treinamento` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `data_agendamento` date NOT NULL,
                               `data_concluido` date DEFAULT NULL,
                               `data_vencimento` date NOT NULL,
                               `instrutor` varchar(255) DEFAULT NULL,
                               `status` enum('AGENDADO','VALIDO','VENCIDO','VENCIMENTO_PROXIMO') NOT NULL,
                               `curso_id` bigint NOT NULL,
                               `funcionario_id` bigint NOT NULL,
                               PRIMARY KEY (`id`),
                               KEY `FKn71lk4elfkv3huiuqatafgtn5` (`curso_id`),
                               KEY `FK9cnqncpmq6ul1lv21okm7koj1` (`funcionario_id`),
                               CONSTRAINT `FK9cnqncpmq6ul1lv21okm7koj1` FOREIGN KEY (`funcionario_id`) REFERENCES `funcionario` (`id`),
                               CONSTRAINT `FKn71lk4elfkv3huiuqatafgtn5` FOREIGN KEY (`curso_id`) REFERENCES `curso` (`id`)
)
