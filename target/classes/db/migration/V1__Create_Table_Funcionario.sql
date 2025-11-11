CREATE TABLE `funcionario` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `cargo` varchar(100) NOT NULL,
                               `cpf` varchar(14) NOT NULL,
                               `data_admissao` date NOT NULL,
                               `data_nascimento` date NOT NULL,
                               `departamento` varchar(100) NOT NULL,
                               `email` varchar(80) NOT NULL,
                               `matricula` varchar(20) NOT NULL,
                               `nome` varchar(80) NOT NULL,
                               `rg` varchar(20) NOT NULL,
                               `situacao` enum('AFASTADO','ATIVO','DESLIGADO','LICENCA') NOT NULL,
                               `telefone` varchar(15) NOT NULL,
                               `tipo_contrato` enum('CLT','ESTAGIO','PJ','TEMPORARIO','TERCEIRIZADO') NOT NULL,
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `UKrxosr8731eb3gbnlbt2mqfan8` (`cpf`),
                               UNIQUE KEY `UK3uda6owswwy94ktwvq5uhifx1` (`matricula`),
                               UNIQUE KEY `UK3f4s61hy9ypa5e4mmjehkujka` (`rg`)
)