# Barbearia Frontend

Frontend Angular leve para o projeto de barbearia.

## Como usar

```bash
cd '/home/robson/Workspaces/Projetos Pessoais/kavex/barbearia/frontend'
npm install
npm start
```

O app roda em `http://localhost:4200` e consome o backend em `http://localhost:8081`.

## Recursos

- Autenticação básica em memória com headers HTTP Authorization
- Rotas separadas para `CLIENTE`, `BARBEIRO` e `DONO`
- UI leve sem frameworks pesados
- Proteção de rotas com `CanActivate`
- Comunicação segura com `HttpClient`
- Layout responsivo e compatível com navegadores modernos
