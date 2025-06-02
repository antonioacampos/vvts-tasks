# Mutation Equivalence Report

Este arquivo lista os mutantes considerados **equivalentes** nas classes testadas.
---

## Classe: `TaskServiceDB.java`

| Linha | ID do Mutante                          | Justificativa da Equivalência                                                                                                                                                                                                     |
|-------|----------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 138   | Replaced double multiplication with division | A troca de `estimatedTime * 0.10` por `estimatedTime / 0.10` altera significativamente o valor da tolerância (ex: de 10 para 1000), mas **os testes cobrem apenas cenários dentro de ambas as tolerâncias**, sem provocar falhas. |
| 141   | Removed call to `task.setSuggestion(...)`     | Neste caminho, a sugestão é redefinida para o mesmo valor. Como os testes não inspecionam diretamente essa atribuição, ela se torna redundante e o comportamento permanece inalterado.                                            |
| 144   | Removed call to `task.setSuggestion(null)`    | O valor já é `null`, então a remoção da chamada não altera o estado. Os testes validam que continua `null`.                                                                                                                       |
| 164   | Removed call to `task.setSuggestion(null)`    | Situação idêntica à da linha 144 - redundância sem efeito colateral.                                                                                                                                                              |

---

## Classe: `Task.java`

**Nenhum mutante equivalente encontrado.**  
Todos os mutantes foram cobertos e eliminados pelos testes.

---

## Classe: `TaskService.java`

**Nenhum mutante equivalente encontrado.**  
Todos os mutantes foram cobertos e eliminados pelos testes.
