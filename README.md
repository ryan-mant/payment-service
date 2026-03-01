# 🛡️ Resilient Payment Gateway

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-green)
![Build Status](https://github.com/ryan-mant/payment-service/actions/workflows/ci-cd.yml/badge.svg)
![Coverage](https://img.shields.io/badge/Coverage-83%25-brightgreen)
![License](https://img.shields.io/badge/License-MIT-blue)

> **Status:** ✅ Concluído (Fase 5)
> **Arquitetura:** Microsserviços Event-Driven
> **Foco:** Alta Concorrência, Resiliência, Observabilidade e Otimização de Recursos (AWS Free Tier).

---

## 🎯 Contexto do Projeto
Este é um sistema de Core Banking distribuído, desenvolvido para processar transações financeiras com alta disponibilidade e observabilidade, rodando sob restrições severas de recursos (AWS Free Tier - 1GB RAM).

O objetivo principal é manter o código limpo, garantir a cobertura de testes e manter a eficiência de recursos para que o deploy na nuvem permaneça estável.

---

## 🏗️ Arquitetura
O sistema segue uma arquitetura de **Microsserviços Event-Driven**:

1.  **Core Banking Service (Producer):** Responsável por gestão de carteiras (Wallets), usuários e execução da transação. Realiza chamadas síncronas para um "Autorizador Externo" (mock) e publica eventos de transação no RabbitMQ.
2.  **Notification Service (Consumer):** Consome eventos de transação concluída e simula o envio de notificações (e-mail/SMS) de forma assíncrona.

```mermaid
graph TD
    subgraph "External World (Drivers)"
        API[REST Controller]
        Grafana[Grafana Dashboards]
    end

    subgraph "Core Banking Service (Hexagon)"
        InputPort[Transfer UseCase]
        Domain[Wallet Domain Entity]
        Service[Transfer Service]
        Producer[RabbitMQ Producer]
        Listener[Transactional Event Listener]
    end

    subgraph "Notification Service (Hexagon)"
        Consumer[RabbitMQ Consumer]
        NotifService[Notification Service]
        NotifDB[(Notification DB)]
    end

    subgraph "Infrastructure (Driven)"
        DB[(PostgreSQL)]
        Auth[Authorizer API]
        Queue[RabbitMQ]
        Prometheus[Prometheus]
    end

    API --> InputPort
    InputPort --> Service
    Service --> Domain
    Service --> DB
    Service -- Circuit Breaker --> Auth
    Service -- Publish Event --> Listener
    Listener -- Async Event (After Commit) --> Producer
    Producer --> Queue
    Queue --> Consumer
    Consumer --> NotifService
    NotifService --> NotifDB
    
    Prometheus -- Scrape Metrics --> Service
    Prometheus -- Scrape Metrics --> NotifService
    Grafana -- Query --> Prometheus
```

---

## 🛠️ Tech Stack

*   **Language:** Java 21
*   **Framework:** Spring Boot 3 (Web, Data JPA, Validation, Actuator)
*   **Database:** PostgreSQL 15 (Instância única com databases segregados: `db_core_banking` e `db_notification`).
*   **Messaging:** RabbitMQ (Exchange: `transaction-exchange`, Queue: `notification-queue`).
*   **Resilience:** Resilience4j (Circuit Breaker implementado na comunicação com o Autorizador Externo).
*   **Observability:** Prometheus (coleta de métricas JVM e Micrometer) e Grafana (Dashboards).
*   **Testing:** JUnit 5, Mockito e **Testcontainers** (Integração real com Postgres e RabbitMQ).

---

## ☁️ Infraestrutura & DevOps (Critical Constraints)

*   **Environment:** AWS EC2 t2.micro (1 vCPU, 1GB RAM).
*   **CI/CD:** Pipeline automatizado via **GitHub Actions**.
    *   **CI:** Build e Testes de Integração com Testcontainers a cada push.
    *   **CD:** Build de imagens Docker e Push automático para o Docker Hub na branch `main`.
*   **Containerization:** Docker & Docker Compose.
*   **Optimization:** Devido à baixa memória, todos os serviços Java rodam com a flag `JAVA_TOOL_OPTIONS="-Xms128m -Xmx300m"` para evitar OOM Kills. As imagens Docker utilizam base Alpine para serem mais leves.
*   **Security:** Credenciais de banco e broker são injetadas via variáveis de ambiente (`.env`).

---

## 🔄 Fluxo de Transação (Caminho Feliz)

1.  API recebe `POST /transfer`.
2.  Valida saldo da carteira de origem.
3.  Chama Autorizador Externo (protegido por Circuit Breaker).
4.  Persiste a transação no Postgres (Atomicidade).
5.  **Transactional Event Listener:** Após o commit bem-sucedido no banco, publica o evento `TransactionSuccessEvent` no RabbitMQ.
6.  Notification Service consome o evento, processa e envia o **ACK manual** para o RabbitMQ.

---

## 💡 Decisões Técnicas Chave (Deep Dive)

### 1. Controle de Concorrência (Optimistic Locking)
Para evitar o problema de **Lost Update** (duas transações debitando a mesma carteira simultaneamente), utilizei a estratégia de **Optimistic Locking** com JPA (`@Version`).

### 2. Resiliência com Circuit Breaker (Fail Fast)
Antes de efetivar uma transferência, o sistema consulta um **Autorizador Externo**. Se a taxa de erros ultrapassar 50%, o circuito abre e o sistema falha imediatamente (Fail Fast), protegendo o Core.

### 3. Consistência Eventual e Transactional Outbox (Simulado)
Utilizei `@TransactionalEventListener(phase = AFTER_COMMIT)` para garantir que eventos só sejam enviados ao RabbitMQ se a transação no banco de dados for confirmada. Isso evita inconsistências onde uma mensagem é enviada mas a transação falha (rollback).

### 4. Confiabilidade no Consumo (Manual ACK)
O **Notification Service** utiliza `AcknowledgeMode.MANUAL`. A mensagem só é removida da fila após o processamento completo e bem-sucedido. Em caso de erro, a mensagem retorna para a fila (NACK com requeue) ou vai para uma Dead Letter Queue (DLQ), garantindo **At-Least-Once Delivery**.

### 5. Idempotência
O consumidor verifica se a notificação já foi processada para o ID da transação (Chave de Idempotência), garantindo que mensagens duplicadas (comuns em sistemas distribuídos) não gerem envios duplicados.

### 6. Observabilidade Centralizada
**Prometheus** coleta métricas expostas pelo Spring Boot Actuator e o **Grafana** exibe dashboards de performance (JVM, CPU, Latência HTTP) e métricas de negócio.

---

## 🧪 Estratégia de Testes

A qualidade é garantida através da Pirâmide de Testes, cobrindo **>83%** do código no Core Banking:

| Tipo | Ferramentas | O que testamos? |
| :--- | :--- | :--- |
| **Unitários** | JUnit 5, Mockito | Lógica de Domínio (`Wallet`), Casos de Uso e Services. |
| **Slice (Web)** | `@WebMvcTest` | Contrato da API, Serialização JSON e Tratamento de Exceções Global. |
| **Integração (DB)** | Testcontainers | Cenários de concorrência real no PostgreSQL (Double Spending). |
| **Integração (Messaging)** | Testcontainers | Publicação e consumo de mensagens no RabbitMQ com validação de ACK/NACK. |
| **Integração (HTTP)** | WireMock | Simulação de falhas e timeouts do serviço externo para validar o Circuit Breaker. |
| **BDD** | AssertJ | Testes descritivos e legíveis focados em comportamento. |

---

## 🚀 Como Rodar Localmente

1.  **Configurar Variáveis de Ambiente:**
    ```bash
    cp .env.example .env
    ```

2.  **Subir a Infraestrutura:**
    ```bash
    docker-compose up -d
    ```

3.  **Executar os Serviços:**
    ```bash
    # Terminal 1
    cd core-banking-service && ./mvnw spring-boot:run
    
    # Terminal 2
    cd notification-service && ./mvnw spring-boot:run
    ```

4.  **Acessar Dashboards:**
    *   **Grafana:** http://localhost:3000 
    *   **Prometheus:** http://localhost:9090

---

## ☁️ Como Rodar em Produção (Docker)

Para ambientes de produção (como EC2), utilize o arquivo `docker-compose-prod.yml`.

1.  **Configurar Variáveis de Ambiente no Servidor:**
    Crie o arquivo `.env` no servidor com as senhas seguras de produção.

2.  **Subir a Stack Completa:**
    ```bash
    docker-compose -f docker-compose-prod.yml up -d
    ```

---

## ✅ Roadmap Concluído

- [x] Implementar Core Banking (Débito/Crédito).
- [x] Implementar Optimistic Locking (Concorrência).
- [x] Integração com Autorizador Externo (Feign + Resilience4j).
- [x] Publicação de Eventos no RabbitMQ (Producer).
- [x] Implementar Worker de Notificação (Consumer Assíncrono).
- [x] Implementar Idempotência (Chave única por transação).
- [x] Implementar Transactional Event Listener (Consistência).
- [x] Implementar Manual ACK no Consumer (Confiabilidade).
- [x] Aumentar cobertura de testes para >80%.
- [x] Adicionar Observabilidade (Prometheus + Grafana).
- [x] Otimização para Cloud (Docker Alpine, JVM Tuning).
- [x] Deploy em Infraestrutura Cloud (AWS EC2).
- [x] Pipeline CI/CD (GitHub Actions + Testcontainers).

---

Developed by **Ryan Silva** 👨‍💻