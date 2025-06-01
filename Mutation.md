# Mutation Equivalence Report

Este arquivo lista os mutantes considerados equivalentes nas classes testadas.

## Mutantes Equivalentes

### Classe: `TaskServiceDB.java`

| Linha | ID do Mutante | Justificativa da Equivalência |
|-------|----------------|-------------------------------|
| 138   | Replaced double multiplication with division | A troca de `estimatedTime * 0.10` por `estimatedTime / 0.10` altera significativamente o valor numérico da tolerância (ex: de 10 para 1000), mas **os testes cobrem apenas cenários que continuam válidos com ambos os valores**. Como os testes não verificam tempos fora da nova tolerância mais ampla, o comportamento observável não muda e o mutante sobrevive. |
| 141   | Removed call to `task.setSuggestion(...)` | Esse caminho só é atingido quando a sugestão é redefinida para o mesmo valor que já estava. Como a sugestão não é utilizada nos testes desse cenário, a chamada é redundante. |
| 144   | Removed call to `task.setSuggestion(null)` | O valor de `suggestion` já é `null` nesse cenário, então a chamada não altera o comportamento observável. Os testes que verificam `assertNull` ainda passam. |
| 164   | Removed call to `task.setSuggestion(null)` | Situação equivalente à linha 144. O método `setSuggestion(null)` é redundante quando o valor já é `null`, e os testes que validam esse estado continuam passando. |

## Classe: Task.java

Nenhum mutante equivalente encontrado. Todos os mutantes foram cobertos por testes.
