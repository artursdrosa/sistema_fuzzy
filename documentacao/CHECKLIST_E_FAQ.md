# Checklist e FAQ: Implementação do Sistema Fuzzy para Filmes

## ✅ Checklist Passo a Passo

### **FASE 1: Análise e Planejamento**

- [ ] **1.1** Ler toda a documentação (DOCUMENTACAO_SISTEMA_FUZZY.md)
- [ ] **1.2** Entender o código original (FuzzyMain.java)
- [ ] **1.3** Validar estrutura do movie_dataset.csv (abrir e verificar colunas)
- [ ] **1.4** Decidir: Com ou sem Cast?
- [ ] **1.5** Decidir: 4 ou 5 variáveis fuzzy?

**Perguntas de verificação:**
- Consegue explicar o que é fuzzificação?
- Entende a função trapezóide?
- Consegue ler um CSV em Java?

---

### **FASE 2: Coleta de Dados Estatísticos**

- [ ] **2.1** Executar análise exploratória no CSV
  ```java
  // Copiar código para analisar ranges de valores
  ```
- [ ] **2.2** Registrar valores mín/máx para:
  - [ ] Budget
  - [ ] Popularity
  - [ ] Runtime
  - [ ] Vote Average
  - [ ] Vote Count

**Valores esperados (aproximado):**
```
Budget:      $0 até $300M+
Popularity:  0.0 até 200+
Runtime:     0 até 240 min
Vote Avg:    0 até 10.0
Vote Count:  0 até 400K+
```

---

### **FASE 3: Definir Variáveis Fuzzy**

- [ ] **3.1** Para BUDGET: Definir 4-5 categorias
  - [ ] Baixo (0-5M)
  - [ ] Médio Baixo (5-40M)
  - [ ] Médio (40-100M)
  - [ ] Médio Alto (100-200M)
  - [ ] Alto (200M+)

- [ ] **3.2** Para POPULARITY: Definir 3-4 categorias
  - [ ] Baixa (0-15)
  - [ ] Média (15-50)
  - [ ] Alta (50-150)
  - [ ] Muito Alta (150+)

- [ ] **3.3** Para RUNTIME: Definir 3 categorias
  - [ ] Curto (0-90 min)
  - [ ] Normal (90-140 min)
  - [ ] Longo (140+ min)

- [ ] **3.4** Para VOTE AVERAGE: Definir 3-4 categorias
  - [ ] Baixo (0-5)
  - [ ] Médio (5-7)
  - [ ] Alto (7-8.5)
  - [ ] Muito Alto (8.5-10)

---

### **FASE 4: Desenvolvimento**

- [ ] **4.1** Criar arquivo FuzzyMovies.java
- [ ] **4.2** Copiar estrutura base (do GUIA_PRATICO_IMPLEMENTACAO.md)
- [ ] **4.3** Implementar leitura do CSV
- [ ] **4.4** Implementar fuzzificação para cada variável
- [ ] **4.5** Definir pelo menos 5 regras fuzzy
- [ ] **4.6** Implementar defuzzificação
- [ ] **4.7** Gerar output em formato separado por ;

**Verificações de código:**
- [ ] Não há exceções de índice (NumberFormatException)
- [ ] Valores null são tratados
- [ ] Output está sendo gerado corretamente

---

### **FASE 5: Testes**

- [ ] **5.1** Executar com 10 filmes (teste pequeno)
- [ ] **5.2** Validar output com 100 filmes
- [ ] **5.3** Comparar resultados:
  - [ ] Avatar/Inception (devem ter score alto)
  - [ ] Filmes com baixo voto (devem ter score baixo)
- [ ] **5.4** Ajustar faixas fuzzy se necessário

**Checklist de validação:**
```
[ ] Avatar tem score > 8?
[ ] Filmes ruim têm score < 4?
[ ] Distribuição parece suave (sem picos)?
[ ] Output está formatado correto?
[ ] Não há valores NaN?
```

---

### **FASE 6: Otimização (Opcional)**

- [ ] **6.1** Adicionar suporte para Cast (se desejado)
- [ ] **6.2** Salvar output em arquivo CSV
- [ ] **6.3** Criar visualização gráfica dos scores
- [ ] **6.4** Comparar com sistema original de restaurantes

---

## ❓ FAQ (Perguntas Frequentes)

### **P1: Como saber se as faixas fuzzy estão corretas?**

**R:** Execute com dados conhecidos:
```
Avatar (Budget: 237M, Vote: 7.8) → Score deve ser ~8.5+
Titanic (Budget: 200M, Vote: 7.2) → Score deve ser ~7.5+
Plan 9 (Budget: 0.6M, Vote: 3.9) → Score deve ser ~2-3
```

Se esses filmes tiverem scores muito diferentes, ajuste as faixas.

---

### **P2: Qual é a melhor forma de lidar com outliers em Popularity?**

**R:** Três opções:

**Opção 1: Ignorar outliers**
```java
if (popularity > 500) popularity = 500; // Limitar ao máximo
```

**Opção 2: Usar logaritmo**
```java
popularity = Math.log(popularity + 1); // Normaliza distribuição
```

**Opção 3: Dividir em categorias (recomendado)**
```java
// Usar quartis do dataset como limites
// Ex: 25% = 10, 50% = 30, 75% = 80, 100% = 200
```

---

### **P3: Por que alguns filmes têm score NaN?**

**R:** Divisão por zero!

```java
float score = (naoRec * 2 + rec * 6.5 + muitoRec * 9) 
             / (naoRec + rec + muitoRec);
             // ↑ Se denominator = 0, resultado = NaN!
```

**Solução:**
```java
float denominator = (naoRec + rec + muitoRec);
if (denominator > 0) {
    score = (naoRec * 2 + rec * 6.5 + muitoRec * 9) / denominator;
} else {
    score = 5.0f; // Valor padrão
}
```

---

### **P4: Como adicionar mais regras fuzzy?**

**R:** Cada regra é uma chamada a `rodaRegraE()`:

```java
// Formato geral:
rodaRegraE(variaveis, "Entrada1", "Entrada2", "Saída");

// Exemplos:
rodaRegraE(vars, "Voto_Alto", "Budget_Alto", "MuitoRecomendado");
rodaRegraE(vars, "Pop_Alta", "Runtime_Normal", "Recomendado");
rodaRegraE(vars, "Voto_Baixo", "Pop_Baixa", "NaoRecomendado");
```

**Recomendação:** Começar com 5-7 regras. Depois adicionar mais se necessário.

---

### **P5: E se quiser usar O operador OR em vez de AND?**

**R:** Use a função `rodaRegraOU()`:

```java
// AND: min() - ambas as condições devem ser atendidas
rodaRegraE(vars, "Voto_Alto", "Budget_Alto", "MuitoRecomendado");

// OR: max() - pelo menos uma condição deve ser atendida  
rodaRegraOU(vars, "Voto_Alto", "Pop_Alta", "MuitoRecomendado");
```

---

### **P6: Como filtrar filmes com dados incompletos?**

**R:** Validar antes de fuzzificar:

```java
// Pular se dados críticos faltam
if (budget == 0 || voteAvg == 0) {
    continue;  // Próximo filme
}

// Ou usar valores padrão
if (runtime == 0) runtime = 120;  // Assumir 2 horas
if (popularity == 0) popularity = 10;  // Valor mínimo
```

---

### **P7: Qual é a diferença entre esses pesos em defuzzificação?**

**R:** Os pesos determinam o "valor final" de cada categoria:

```java
// Original (Restaurante):
score = (NA*1.5 + A*7.0 + MA*9.5) / (NA+A+MA)

// Proposto (Filme):
score = (NR*2.0 + R*6.5 + MR*9.0) / (NR+R+MR)
```

| Peso | Significado |
|------|-------------|
| 2.0 | "Não Recomendado" vale pouco |
| 6.5 | "Recomendado" vale bastante |
| 9.0 | "Muito Recomendado" vale muito |

**Para ajustar:**
```java
// Se quer favorecer "Recomendado":
score = (NR*1.0 + R*8.0 + MR*9.0) / (NR+R+MR);

// Se quer ser mais rigoroso:
score = (NR*3.0 + R*6.0 + MR*9.0) / (NR+R+MR);
```

---

### **P8: Como salvar output em arquivo?**

**R:** Substituir `System.out.println` por `FileWriter`:

```java
import java.io.FileWriter;
import java.io.BufferedWriter;

// No topo do main():
BufferedWriter writer = new BufferedWriter(new FileWriter("output_filmes.csv"));
writer.write("Título;Budget;Popularity;Runtime;Vote_Avg;NaoRecomendado;Recomendado;MuitoRecomendado;Score\n");

// Em vez de:
System.out.println(...);

// Use:
writer.write(titulo + ";" + ... + "\n");

// Ao final:
writer.close();
```

---

### **P9: Como comparar com o sistema original de restaurantes?**

**R:** Ambos usam a mesma estrutura:

| Aspecto | Restaurante | Filme |
|---------|-------------|-------|
| Variáveis | 4 | 4 |
| Fuzzificação | Trapezóide | Trapezóide |
| Operador | AND (min) | AND (min) |
| Defuzzificação | Média ponderada | Média ponderada |
| Output | 3 categorias | 3 categorias |

**Diferença:** Dados e regras adaptadas para filmes.

---

### **P10: Como saber se está funcionando corretamente?**

**R:** Execute este teste:

```
TESTE 1: Avatar
- Budget alto, voto alto → Deve ter score muito alto (8+)

TESTE 2: Filme desconhecido com voto baixo
- Qualquer budget, voto baixo → Deve ter score baixo (2-4)

TESTE 3: Filme médio com popularidade alta
- Budget médio, voto médio, pop alta → Score médio (5-7)

Se esses testes passam, o sistema está certo!
```

---

## 🔧 Troubleshooting Comum

### **Problema 1: Exception "Index out of bounds"**

**Causa:** Tentando acessar coluna que não existe
```java
String[] spl = line.split(",");
System.out.println(spl[25]); // Erro se CSV tem < 26 colunas
```

**Solução:**
```java
if (spl.length > 25) {
    // usar spl[25]
} else {
    continue; // Pular linha
}
```

---

### **Problema 2: Todos os filmes têm o mesmo score**

**Causa:** Todas as regras estão retornando 0, ou há um problema na lógica
```java
// Verificar se variáveis estão sendo fuzzificadas
System.out.println("Voto_Alto: " + vars.get("Voto_Alto"));
System.out.println("MuitoRecomendado: " + vars.get("MuitoRecomendado"));
```

---

### **Problema 3: Score varia muito (100% para 1%)**

**Causa:** Faixas fuzzy muito apertadas, ou pesos desproporcionais

**Solução:** Expandir as faixas:
```java
// De:
grupoVoto.add(new VariavelFuzzy("Voto_Alto", 7, 7.5f, 8, 9));

// Para:
grupoVoto.add(new VariavelFuzzy("Voto_Alto", 6.5f, 7, 8.5f, 9));
```

---

### **Problema 4: Arquivo não encontrado**

```
Exception: No such file or directory: movie_dataset.csv
```

**Solução:** Verificar caminho:
```java
// Caminho completo:
new FileReader("c:/Users/artur/Desktop/Materias/IA1/Fuzzy_Movie_Dataset/movie_dataset.csv")

// Ou usar caminho relativo (estando no diretório certo):
new FileReader("movie_dataset.csv")
```

---

## 📊 Métricas de Sucesso

Após implementar, verificar:

1. **Cobertura:** Quantos filmes foram processados?
   ```
   Total filmes: 5000
   Filmes com dados completos: 4850 (97%)
   ```

2. **Distribuição de Scores:**
   ```
   Não Recomendado (0-3):     500 filmes (10%)
   Recomendado (4-7):        2500 filmes (51%)
   Muito Recomendado (8-10): 1850 filmes (38%)
   ```

3. **Validação Manual:**
   - Avatar, Inception, Titanic têm score > 7? ✓
   - Filmes ruins têm score < 4? ✓

---

## 🎓 Próximos Passos Avançados

Depois que o sistema básico estiver funcionando:

1. **Adicionar Cast com pesos customizados**
2. **Implementar gêneros como variável**
3. **Normalizar Popularity com logaritmo**
4. **Criar visualização gráfica com JFreeChart**
5. **Comparar resultados com IMDB/Rotten Tomatoes**

---

## 📞 Resumo: O que Fazer Agora?

### **IMEDIATAMENTE:**
1. Ler DOCUMENTACAO_SISTEMA_FUZZY.md
2. Entender o código FuzzyMain.java original

### **PRÓXIMAS 2 HORAS:**
3. Executar análise exploratória no CSV
4. Definir faixas para cada variável fuzzy

### **PRÓXIMAS 4 HORAS:**
5. Implementar FuzzyMovies.java
6. Testar com 100 filmes

### **PRÓXIMAS 8 HORAS:**
7. Validar resultados
8. Ajustar faixas se necessário

### **OPCIONAL:**
9. Adicionar features avançadas (Cast, gêneros, etc)

---

**BOA SORTE! 🚀**

Se tiver dúvidas, revise:
- DOCUMENTACAO_SISTEMA_FUZZY.md (conceitos)
- GUIA_PRATICO_IMPLEMENTACAO.md (código)
- RESUMO_VISUAL_EXEMPLOS.md (exemplos)
