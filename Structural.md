# Structural Coverage Report

## Classe: `ApiExceptionHandler.java`

| Linhas | Trecho                                 | Justificativa |
|--------|----------------------------------------|---------------|
| 19–26  | `handleNullPointerException(...)`      | Difícil simular uma NullPointerException real dentro do fluxo de testes unitários sem recorrer a mocks forçados. |
| 31–38  | `handleIllegalArgumentException(...)`  | Não ocorre no fluxo natural dos testes. Seria necessário provocar erro proposital. |
| 43–50  | `handleIllegalStateException(...)`     | Só ocorre com fluxos inválidos ou estados corrompidos, que não são foco nos testes. |
| 55–62  | `handleEntityNotFoundException(...)`   | O fluxo normal garante existência da entidade; difícil simular exceção no escopo atual. |
| 67–74  | `handleEntityAlreadyExistsException(...)` | Exceção nunca é lançada diretamente, apenas por falhas lógicas em casos raros de duplicidade. |

---

## Classe: `TransactionController.java`

| Linhas | Trecho                                 | Justificativa |
|--------|----------------------------------------|---------------|
| 21–22  | `hello()` endpoint                     | Este controller está fora do escopo funcional principal (tarefas), usado apenas como exemplo/documentação. |

---

## Classe: `UserController.java`

| Linhas | Trecho                                 | Justificativa |
|--------|----------------------------------------|---------------|
| 50–51  | `register(...)` endpoint               | Fluxo de autenticação não é contemplado nos testes funcionais principais de tarefa. |

---

## Classe: `JwtAuthenticationFilter.java`

| Linhas | Trecho                                 | Justificativa |
|--------|----------------------------------------|---------------|
| 36–38  | Validação de header `Authorization`    | Fluxo de autenticação real exigiria testes de integração com tokens reais. |
| 46     | `jwtService.extractUsername(...)`      | Só entra se token inválido ou ausente, o que não foi testado em casos negativos. |
| 49     | Validação `SecurityContextHolder`      | Depende do estado da autenticação, não simulado completamente em teste unitário. |
| 60–63  | `catch (JwtException)` + resposta JSON | Simulação de erro JWT só seria possível em teste de integração com token inválido. |

---

## Classe: `AuthenticationInfoService.java`

| Linhas | Trecho                                 | Justificativa |
|--------|----------------------------------------|---------------|
| 13–14  | Verificação de autenticação nula       | Cenário de segurança extrema não reproduzido no teste unitário por ausência de contexto autenticado falso. |

---

## Classe: `ResponseTaskDTO.java`

| Linhas | Trecho                                 | Justificativa |
|--------|----------------------------------------|---------------|
| 24–36  | Construtor que mapeia `Task`           | Apenas encapsula valores sem lógica de decisão. Testes diretos sobre `Task` já validam esses valores. |

---
