# Barbearia

Projeto Java Spring Boot para gestão de barbearia com arquitetura DDD, segurança, logs, métricas e testes.

## Visão geral

- Clientes podem agendar horários, ver serviços, produtos e preços.
- Barbeiros acompanham agenda, ganhos do dia, histórico e projeções de receita.
- Dono do negócio visualiza clientes, valor bruto, lucro, custos, dias de maior movimento e desempenho estratégico.

## Características

- Arquitetura DDD com camadas de domínio, aplicação e infraestrutura.
- Autenticação e autorização com papéis: `CLIENTE`, `BARBEIRO`, `DONO`.
- Persistência com Spring Data JPA e banco em memória H2.
- Métricas via Micrometer / Prometheus.
- Logs estruturados usando SLF4J.
- Testes unitários e de serviço.

## Como executar

```bash
cd barbearia
mvn spring-boot:run
```

## API

- `GET /api/clients/services`
- `POST /api/clients/appointments`
- `GET /api/barbers/schedule`
- `GET /api/barbers/earnings`
- `GET /api/owners/dashboard`

## Segurança

Usuários definidos no código apenas para exemplo. Em produção, troque por um provedor de identidade.
---