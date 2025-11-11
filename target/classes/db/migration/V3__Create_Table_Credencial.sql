
CREATE TABLE `credencial` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `data_emissao` date NOT NULL,
                              `data_vencimento` date NOT NULL,
                              `status` enum('VALIDA','VENCIDA','VENCIMENTO_PROXIMO') NOT NULL,
                              `tipo` enum('PERMANENTE','TEMPORARIA') NOT NULL,
                              `funcionario_id` bigint NOT NULL,
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `UK2n1o0ul0gabuln6nei16p08an` (`funcionario_id`),
                              CONSTRAINT `FK40pqjkajvbsm945sjdli9d8lp` FOREIGN KEY (`funcionario_id`) REFERENCES `funcionario` (`id`)
)
