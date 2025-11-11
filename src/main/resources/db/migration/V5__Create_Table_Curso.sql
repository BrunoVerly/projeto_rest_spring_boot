CREATE TABLE `curso` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `carga_horaria` int NOT NULL,
                         `descricao` text,
                         `nome` varchar(80) NOT NULL,
                         `origem_curso` enum('EXTERNO','INTERNO') NOT NULL,
                         `tipo_obrigatoriedade` enum('ADICIONAL','OBRIGATORIO') NOT NULL,
                         `validade_meses` int NOT NULL,
                         PRIMARY KEY (`id`)
)
