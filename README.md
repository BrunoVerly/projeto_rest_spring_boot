# API de Gestão de Treinamentos e Credenciais 🎓

![Java](https://img.shields.io/badge/Java-21-blue?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/Spring_Boot-3.4-darkgreen?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-red?style=for-the-badge&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-blue?style=for-the-badge&logo=docker&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Authentication-black?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

---

## 📖 Sobre o Projeto

Este projeto é uma **API RESTful** desenvolvida para o trabalho acadêmico da **UC – Programação de Soluções Computacionais (Faseh)**.

O objetivo é aplicar na prática os conceitos de desenvolvimento de software, criando uma solução real para informatizar processos de pequenas e médias empresas, conforme proposto pela disciplina. [cite: 4]

### Contexto da Solução

O módulo de negócio escolhido foi o de **Recursos Humanos**, com foco específico na **gestão de treinamentos e controle de credenciais**. [cite: 4] [cite_start]A API centraliza e automatiza o controle de cursos corporativos, capacitações obrigatórias e suas respectivas datas de validade, garantindo conformidade e reduzindo falhas manuais. [cite: 4]

A aplicação foi construída seguindo as melhores práticas de mercado, arquitetura limpa, cobertura de testes, documentação automática e suporte a múltiplos formatos de consumo de dados. [cite: 1]

---

## 🛠️ Tecnologias e Ferramentas

O projeto utiliza um *stack* de tecnologias robusto e moderno, focado no ecossistema Spring:

| Categoria | Ferramenta/Tecnologia |
| :--- | :--- |
| **Backend & Core** | Java 21, Spring Boot 3.4, Maven |
| **Banco de Dados** | MySQL (Produção), H2 (Testes), Spring Data JPA (Hibernate) |
| **Migrations** | Flyway (criação e carga automática do BD) |
| **API & Arquitetura**| RESTful, HATEOAS, Content Negotiation (JSON, XML, YAML) |
| **Segurança** | Spring Security, Autenticação JWT (com Refresh Token) |
| **Documentação** | Springdoc OpenAPI (Swagger UI) |
| **Testes** | JUnit 5, Mockito, Testcontainers, REST Assured |
| **Arquivos (I/O)** | Apache POI (Excel), Apache Commons (CSV), JasperReports (PDF) |
| **DevOps & Infra** | Docker, Docker Compose, GitHub (CI/CD) |
| **Utilitários** | Dozer Mapper, Spring Mail, Qodana (Análise Estática) |

---

## ✨ Funcionalidades Implementadas

### ✔️ Funcionários
* **CRUD** completo.
* **Busca** por: nome, matrícula, situação (ativo, inativo, etc.) e intervalo de datas de admissão.
* **Importação em massa** via `.csv` e `.xlsx`.
* **Exportação** em PDF, CSV e Excel.

### ✔️ Cursos
* **CRUD** completo.
* **Busca** por nome.
* **Importação em massa** via `.csv` e `.xlsx`.
* **Exportação** em PDF, CSV e Excel.

### ✔️ Treinamentos
* **CRUD** completo (associação entre funcionário e curso).
* **Filtros** por: instrutor, status (VÁLIDO, VENCIDO, etc.) e datas (conclusão ou vencimento).
* **Job automatizado** (`@Scheduled`) para atualização diária do status dos treinamentos.
* **Exportação** em PDF (relatórios detalhados via JasperReports), CSV e Excel.

### ✔️ Credenciais
* **CRUD** completo.
* **Busca** por: status e intervalo de datas (emissão ou vencimento).
* **Job automatizado** (`@Scheduled`) para atualização diária do status.
* **Exportação** em PDF, CSV e Excel.

### ✔️ API e Segurança
* **Autenticação JWT** em todos os endpoints de dados.
* **Endpoints** para login (`/auth/autenticar`) e renovação de token (`/auth/atualizar`).
* **Envio de E-mail** (simples e com anexos).
* **CORS** habilitado e configurado para permitir acesso de todas as origens (para fins de teste).
* **Versionamento** de API (`/api/<modulo>/v1`).

---

## 📂 Estrutura do Projeto

A estrutura segue o padrão de separação de responsabilidades (SoC) recomendado para aplicações Spring Boot:
```shell
src/main/java/com.example.projetoRestSpringBoot
├── config
├── controller
│    ├── docs
│    ├── AuthController
│    ├── FuncionarioController
│    ├── CursoController
│    ├── CredencialController
│    └── TreinamentoController
├── dto
├── enums
├── exception
│    ├── handler
├── file
│    ├── exporter
│    └── importer
├── mail
├── mapper
├── model
├── repository
├── security.jwt
├── serializationConverter
├── service
│    ├── linkhateoas
└── Startup

Testes
src/test/java/com.example.projetoRestSpringBoot
├── config
├── integrationtests
│    ├── swagger
│    └── testcontainers
├── unittests
│    ├── mocks
│    └── services

---
```
---

## 🚀 Como Executar o Projeto

Existem duas formas principais de executar a aplicação:

### Opção 1: Via IDE (Recomendado)

1.  **Pré-requisitos:** Garanta que você tenha o **JDK 21** e o **Maven** configurados.
2.  **Clonar o Repositório:**
    ```bash
    git clone [https://github.com/seu-usuario/projeto_rest_spring_boot.git](https://github.com/seu-usuario/projeto_rest_spring_boot.git)
    cd projeto_rest_spring_boot
    ```
3.  **Executar:** Importe o projeto como um "Existing Maven Project" na sua IDE (ex: IntelliJ, VSCode) e execute a classe `Startup.java`.
4.  **Acesso:** A aplicação iniciará em `http://localhost:8080`.

> **Nota sobre o Banco:** Ao iniciar, o **Flyway** criará automaticamente todas as tabelas e populará o banco com dados de teste (incluindo 2 usuários para autenticação).

### Opção 2: Via Docker (Alternativa)

1.  **Pré-requisitos:** Docker e Docker Compose instalados.
2.  **Pull da Imagem:**
    ```bash
    docker pull brunoverly/projeto_rest_spring_boot:latest
    ```
3.  **Rodar o Container:**
    ```bash
    docker run -p 8080:8080 brunoverly/projeto_rest_spring_boot:latest
    ```
4.  **Acesso:** A aplicação estará disponível em `http://localhost:8080`.

* **Repositório Docker Hub:** `https://hub.docker.com/repositories/brunoverly`

---

## 🧪 Testando a API

### 1. Demo Online (Azure)
Atualmente, a aplicação está disponível para testes em uma VM do Azure:

* **Swagger UI:** **`http://20.220.171.218/swagger-ui.html`**

### 2. Documentação Swagger (Local)
Ao executar localmente (via IDE ou Docker), acesse a documentação interativa para ver e testar todos os endpoints:

* **URL:** **`http://localhost:8080/swagger-ui.html`**

### 3. Coleção Postman
Uma coleção Postman completa, com todos os endpoints e *bodies* de exemplo, está incluída no projeto:

* **Localização:** 📁 `src/main/resources/postman/`

Basta importá-la. Ela já está configurada para obter o token JWT (verifique as **variáveis** de ambiente **configuradas** no Postman antes de realizar os testes).

### 4. Usuário de Teste Padrão
Para se autenticar (via Postman, Swagger ou Demo Online), utilize:

* **Username:** `leandro`
* **Password:** `admin123`
