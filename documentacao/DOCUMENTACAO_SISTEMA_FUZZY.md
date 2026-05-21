# Documentação: Sistema de Avaliação Fuzzy para Filmes

## 📋 Índice
1. [Visão Geral](#visão-geral)
2. [Componentes do Sistema](#componentes-do-sistema)
3. [Como Funciona a Lógica Fuzzy](#como-funciona-a-lógica-fuzzy)
4. [Fluxo de Processamento](#fluxo-de-processamento)
5. [Análise das Variáveis Fuzzy](#análise-das-variáveis-fuzzy)
6. [Base de Regras Fuzzy](#base-de-regras-fuzzy)
7. [Cálculo Final do Score](#cálculo-final-do-score)
8. [Adaptação para Movie Dataset](#adaptação-para-movie-dataset)

---

## 🎯 Visão Geral

O sistema implementa **lógica fuzzy** para atribuir uma nota (score) a restaurantes com base em:
- **Preço** (custo para dois)
- **Rating** (avaliação do restaurante)
- **Votos** (número de avaliações)
- **Nota da Culinária** (qualidade dos tipos de comida oferecidos)

O sistema gera um score final entre **1.5 a 9.5** indicando a **atratividade** do restaurante (NA, A ou MA).

---

## 🔧 Componentes do Sistema

### 1. **VariavelFuzzy.java**
Define uma **variável fuzzy** com função de pertencimento trapezoidal.

**Parâmetros:**
- `nome`: identificador da variável (ex: "Barato", "Muito Barato")
- `b1`: limite inferior (begins)
- `t1`: início do pico (top 1)
- `t2`: fim do pico (top 2)
- `b2`: limite superior (begins)

**Função de Fuzzificação:**
```
     1.0 |      ___
         |     /   \
Grau     |    /     \
     0.5 |   /       \
         |  /         \
       0 |_/___________\_____
         b1  t1   t2   b2   valor
```

A função retorna um grau de pertencimento (0 a 1):
- Se `valor < b1` ou `valor > b2`: pertence **0%**
- Se `t1 <= valor <= t2`: pertence **100%** (pico)
- Entre `b1-t1`: rampa crescente
- Entre `t2-b2`: rampa descendente

---

## 📊 Como Funciona a Lógica Fuzzy

### O que é Fuzzificação?

**Transforma valores numéricos em graus de pertencimento** a conjuntos fuzzy.

**Exemplo com Preço:**

```
Preço = R$45

"Muito Barato" (0, 0, 10, 20):    0.0   (não é muito barato)
"Barato"       (10, 20, 30, 60):  0.75  (razoavelmente barato)
"Custo Médio"  (20, 40, 50, 70):  0.75  (razoavelmente médio)
"Caro"         (40, 60, 70, 120): 0.25  (um pouco caro)
"Muito Caro"   (70, 110, 500, 500): 0.0 (não é caro)
```

---

## 🔄 Fluxo de Processamento

### Passo 1: Leitura do CSV
```java
BufferedReader bfr = new BufferedReader(new FileReader(new File("restaurantes_filtrados.csv")));
```

### Passo 2: Para cada linha (restaurante):

#### A. **Fuzzificação do Preço**
```java
float custodinheiro = Float.parseFloat(spl[3]);  // Coluna 3 do CSV
grupoPreco.fuzzifica(custodinheiro, asVariaveis);
```
Calcula graus de pertencimento: "Muito Barato", "Barato", "Custo Médio", "Caro", "Muito Caro"

#### B. **Fuzzificação do Rating**
```java
float rating = Float.parseFloat(spl[5]);  // Coluna 5
grupoRating.fuzzifica(rating, asVariaveis);
```
Calcula: "MR" (Muito Ruim), "R" (Ruim), "B" (Bom), "MB" (Muito Bom)

#### C. **Fuzzificação dos Votos**
```java
float votos = Float.parseFloat(spl[8]);  // Coluna 8
grupoVotos.fuzzifica(votos, asVariaveis);
```
Calcula: "V_MPV" (Muito Poucos), "V_PV" (Poucos), "V_MEV" (Muitos), "V_BAV" (Bastante), "V_MUV" (Muito)

#### D. **Fuzzificação da Nota da Culinária**
```java
// Pega os tipos de comida (coluna 2)
String comidas[] = spl[2].split(",");
// Busca cada comida no HashMap de notas e calcula média
float notafinalcomida = notacomida/numeroNotas;
grupoNotaComida.fuzzifica(notafinalcomida, asVariaveis);
```
Calcula: "C_R" (Ruim), "C_M" (Médio), "C_B" (Bom)

### Passo 3: Aplicação de Regras Fuzzy (IF-THEN)
```java
rodaRegraE(asVariaveis, "Barato", "B", "A");
// SE (Barato AND Bom Rating) ENTÃO Atratividade = A
```

### Passo 4: Cálculo da Atratividade Final
```java
float NA = asVariaveis.get("NA");  // Não Atrativo
float A = asVariaveis.get("A");    // Atrativo
float MA = asVariaveis.get("MA");  // Muito Atrativo

float score = (NA*1.5f + A*7.0f + MA*9.5f) / (NA+A+MA);
```

---

## 📈 Análise das Variáveis Fuzzy

### **Variável: PREÇO**
```java
grupoPreco.add(new VariavelFuzzy("Muito Barato", 0, 0, 10, 20));
grupoPreco.add(new VariavelFuzzy("Barato", 10, 20, 30, 60));
grupoPreco.add(new VariavelFuzzy("Custo Medio", 20, 40, 50, 70));
grupoPreco.add(new VariavelFuzzy("Caro", 40, 60, 70, 120));
grupoPreco.add(new VariavelFuzzy("Muito Caro", 70, 110, 500, 500));
```

| Categoria | b1 | t1 | t2 | b2 | Significado |
|-----------|----|----|----|----|------------|
| Muito Barato | 0 | 0 | 10 | 20 | Extremamente barato |
| Barato | 10 | 20 | 30 | 60 | Preço acessível |
| Custo Médio | 20 | 40 | 50 | 70 | Preço médio |
| Caro | 40 | 60 | 70 | 120 | Preço elevado |
| Muito Caro | 70 | 110 | 500 | 500 | Preço muito elevado |

### **Variável: RATING**
```java
grupoRating.add(new VariavelFuzzy("MR", 0, 0, 10, 20));    // Muito Ruim
grupoRating.add(new VariavelFuzzy("R", 10, 20, 30, 40));   // Ruim
grupoRating.add(new VariavelFuzzy("B", 20, 40, 45, 50));   // Bom
grupoRating.add(new VariavelFuzzy("MB", 40, 48, 50, 50));  // Muito Bom
```

### **Variável: VOTOS**
```java
grupoVotos.add(new VariavelFuzzy("V_MPV", 0, 0, 10, 20));      // Muito Poucos
grupoVotos.add(new VariavelFuzzy("V_PV", 10, 20, 50, 60));     // Poucos
grupoVotos.add(new VariavelFuzzy("V_MEV", 40, 80, 200, 300));  // Muitos
grupoVotos.add(new VariavelFuzzy("V_BAV", 200, 300, 500, 1000)); // Bastante
grupoVotos.add(new VariavelFuzzy("V_MUV", 400, 500, 3200, 3200)); // Muito
```

### **Variável: NOTA DA CULINÁRIA**
```java
grupoNotaComida.add(new VariavelFuzzy("C_R", 0, 0, 4.5f, 6.0f));     // Ruim
grupoNotaComida.add(new VariavelFuzzy("C_M", 5.0f, 6.0f, 7.0f, 8.0f)); // Médio
grupoNotaComida.add(new VariavelFuzzy("C_B", 7.5f, 8.5f, 10, 10));   // Bom
```

---

## 🎮 Base de Regras Fuzzy

As regras implementadas seguem a lógica **AND** (operador mín):

```java
rodaRegraE(asVariaveis, var1, var2, resultado);
// SE (var1 AND var2) ENTÃO resultado
// Usa: min(var1, var2) para combinar
```

### Regras de Preço + Rating
| Preço | Rating | Resultado |
|-------|--------|-----------|
| Barato | Bom | Atrativo |
| Muito Barato | Bom | Atrativo |
| Muito Barato | Muito Bom | Muito Atrativo |
| Barato | Muito Bom | Muito Atrativo |
| Barato | Ruim | Não Atrativo |
| Muito Barato | Ruim | Atrativo |
| Muito Barato | Muito Ruim | Não Atrativo |

### Regras de Atratividade + Votos
| Atratividade | Votos | Resultado |
|--------------|-------|-----------|
| Muito Atrativo | Muito Poucos | Não Atrativo |
| Muito Atrativo | Poucos | Atrativo |
| Muito Atrativo | Muitos | Atrativo |

### Regras de Culinária + Preço
| Culinária | Preço | Resultado |
|-----------|-------|-----------|
| Boa | Barato | Muito Atrativo |
| Média | Barato | Atrativo |
| Ruim | Barato | Não Atrativo |

---

## 🧮 Cálculo Final do Score

O score é calculado usando **defuzzificação por média ponderada**:

```java
float score = (NA*1.5f + A*7.0f + MA*9.5f) / (NA+A+MA);
```

**Onde:**
- **NA** (Não Atrativo) = peso **1.5**
- **A** (Atrativo) = peso **7.0**
- **MA** (Muito Atrativo) = peso **9.5**

**Exemplo:**
```
NA = 0.2  →  0.2 × 1.5 = 0.3
A  = 0.6  →  0.6 × 7.0 = 4.2
MA = 0.2  →  0.2 × 9.5 = 1.9

Score = (0.3 + 4.2 + 1.9) / (0.2 + 0.6 + 0.2)
      = 6.4 / 1.0
      = 6.4
```

**Range Final:** 1.5 a 9.5 (ou além, dependendo da distribuição)

---

## 🎬 Adaptação para Movie Dataset

### 1. **Estrutura do CSV**
O `movie_dataset.csv` tem colunas:
```
budget, genres, homepage, id, keywords, original_language, 
original_title, overview, popularity, production_companies, 
production_countries, release_date, revenue, runtime, 
spoken_languages, status, tagline, title, vote_average, 
vote_count, cast, crew, director
```

### 2. **Variáveis Fuzzy Propostas**

#### **Opção A: 4 Variáveis (Recomendado)**
```
1. Budget (Orçamento)
2. Popularity (Popularidade)
3. Runtime (Duração)
4. Vote_Average (Nota do Filme)
```

#### **Opção B: 5 Variáveis (com Cast)**
```
1. Budget
2. Popularity
3. Runtime
4. Vote_Average
5. Cast Score (customizado manualmente)
```

### 3. **Mapeamento de Variáveis**

#### **A. BUDGET (Orçamento)**

| Categoria | b1 | t1 | t2 | b2 | Faixa de Orçamento (USD) |
|-----------|----|----|----|----|--------------------------|
| Baixo | 0 | 0 | 1M | 5M | Baixo orçamento |
| Médio Baixo | 1M | 5M | 20M | 40M | Orçamento moderado |
| Médio | 20M | 40M | 60M | 100M | Orçamento médio |
| Médio Alto | 60M | 100M | 150M | 200M | Orçamento elevado |
| Alto | 150M | 200M | 300M | 500M | Blockbuster |

```java
GrupoVariaveis grupoBudget = new GrupoVariaveis();
grupoBudget.add(new VariavelFuzzy("Budget_Baixo", 0, 0, 1000000, 5000000));
grupoBudget.add(new VariavelFuzzy("Budget_MedioBaixo", 1000000, 5000000, 20000000, 40000000));
grupoBudget.add(new VariavelFuzzy("Budget_Medio", 20000000, 40000000, 60000000, 100000000));
grupoBudget.add(new VariavelFuzzy("Budget_MedioAlto", 60000000, 100000000, 150000000, 200000000));
grupoBudget.add(new VariavelFuzzy("Budget_Alto", 150000000, 200000000, 300000000, 500000000));
```

#### **B. POPULARITY (Popularidade)**

```java
GrupoVariaveis grupoPopularidade = new GrupoVariaveis();
grupoPopularidade.add(new VariavelFuzzy("Pop_Baixa", 0, 0, 5, 15));
grupoPopularidade.add(new VariavelFuzzy("Pop_Média", 5, 15, 30, 50));
grupoPopularidade.add(new VariavelFuzzy("Pop_Alta", 30, 50, 100, 150));
grupoPopularidade.add(new VariavelFuzzy("Pop_MuitoAlta", 100, 150, 500, 500));
```

#### **C. RUNTIME (Duração em minutos)**

```java
GrupoVariaveis grupoRuntime = new GrupoVariaveis();
grupoRuntime.add(new VariavelFuzzy("Runtime_Curto", 0, 0, 60, 90));       // < 90 min
grupoRuntime.add(new VariavelFuzzy("Runtime_Normal", 80, 100, 120, 140)); // 90-140 min
grupoRuntime.add(new VariavelFuzzy("Runtime_Longo", 130, 150, 200, 250)); // > 150 min
```

#### **D. VOTE_AVERAGE (Nota: 0-10)**

```java
GrupoVariaveis grupoVoteAverage = new GrupoVariaveis();
grupoVoteAverage.add(new VariavelFuzzy("Voto_Baixo", 0, 0, 3, 5));      // Péssimo
grupoVoteAverage.add(new VariavelFuzzy("Voto_Médio", 4, 5, 6.5f, 7.5f)); // Médio
grupoVoteAverage.add(new VariavelFuzzy("Voto_Alto", 6, 7, 8, 10));       // Bom
grupoVoteAverage.add(new VariavelFuzzy("Voto_MuitoAlto", 7.5f, 8.5f, 10, 10)); // Excelente
```

#### **E. CAST (Variável Customizada - Opcional)**

```java
// Criar classe similar a ComidasNota
public class AtoresNota {
    public HashMap<String, Float> notasAtores = new HashMap<>();
    public AtoresNota() {
        notasAtores.put("Leonardo DiCaprio", 10.0f);
        notasAtores.put("Meryl Streep", 9.9f);
        notasAtores.put("Tom Hanks", 9.8f);
        // ... mais atores
    }
}
```

### 4. **Tratamento de Dados Específicos**

#### **Problema: Budget com valores vazios**
```java
// Antes de converter para float:
if (spl[0] == null || spl[0].isEmpty()) {
    float budget = 0;  // ou valor padrão
} else {
    float budget = Float.parseFloat(spl[0]);
}
```

#### **Problema: Genres (múltiplos gêneros)**
Como restaurantes têm múltiplas cozinhas, você pode:

**Opção 1: Ignorar gêneros (usar apenas 4 variáveis)**
- Simplifica o sistema

**Opção 2: Nomear gênero principal**
```java
String genres = spl[1];  // "Action|Adventure|Sci-Fi"
String generoPrincipal = genres.split("\\|")[0];  // "Action"
```

**Opção 3: Mesclar gêneros semelhantes**
```java
// Agrupar gêneros por tema
"Action|Adventure" → "Action-Adventure"
"Comedy|Romance" → "Comedy-Romance"
"Drama|Crime" → "Drama-Crime"
```

#### **Problema: Cast (string de atores)**
```java
String castString = spl[20];  // JSON ou string com nomes
// Fazer parsing e validar contra lista de atores importantes

// Exemplo simples:
float castScore = 0;
if (castString.contains("Leonardo DiCaprio")) castScore += 10.0f;
if (castString.contains("Meryl Streep")) castScore += 9.9f;
// ... etc
```

### 5. **Estrutura de Saída**

**Output esperado:**
```
Título do Filme; Gênero; Budget; Popularity; Runtime; Vote_Avg; NA; A; MA; Score
Avatar; Sci-Fi|Adventure; 237000000; 150.5; 162; 7.8; 0.1; 0.4; 0.5; 8.2
```

### 6. **Regras Fuzzy Adaptadas (Exemplo)**

```java
// SE (Budget_Alto AND Voto_Alto) ENTÃO Muito_Recomendado
rodaRegraE(asVariaveis, "Budget_Alto", "Voto_Alto", "MR");

// SE (Pop_Alta AND Voto_MuitoAlto) ENTÃO Muito_Recomendado
rodaRegraE(asVariaveis, "Pop_Alta", "Voto_MuitoAlto", "MR");

// SE (Runtime_Normal AND Voto_Alto) ENTÃO Recomendado
rodaRegraE(asVariaveis, "Runtime_Normal", "Voto_Alto", "R");

// SE (Budget_Baixo AND Voto_Alto) ENTÃO Muito_Recomendado
rodaRegraE(asVariaveis, "Budget_Baixo", "Voto_Alto", "MR");
```

---

## 📋 Checklist: Passos para Implementação

- [ ] 1. Criar `VariaveisFilmes.java` (similar a `ComidasNota.java`)
- [ ] 2. Definir faixas de budget baseadas em análise do dataset
- [ ] 3. Definir faixas de popularity (cuidado com outliers)
- [ ] 4. Definir faixas de runtime
- [ ] 5. Definir faixas de vote_average
- [ ] 6. (Opcional) Criar `AtoresNota.java` para customização de atores
- [ ] 7. Criar `FuzzyMovies.java` (similar a `FuzzyMain.java`)
- [ ] 8. Ajustar caminho do CSV para `movie_dataset.csv`
- [ ] 9. Ajustar índices das colunas de acordo com posição no CSV
- [ ] 10. Definir regras fuzzy apropriadas para filmes
- [ ] 11. Testar e validar os resultados

---

## 🔍 Análise Preliminar: Movie Dataset

Para ajustar melhor as faixas fuzzy, você precisa analisar:

```sql
-- Budget
SELECT MIN(budget), MAX(budget), AVG(budget) FROM movies WHERE budget > 0;

-- Popularity
SELECT MIN(popularity), MAX(popularity), AVG(popularity) FROM movies;

-- Runtime
SELECT MIN(runtime), MAX(runtime), AVG(runtime) FROM movies WHERE runtime > 0;

-- Vote Average
SELECT MIN(vote_average), MAX(vote_average), AVG(vote_average) FROM movies;

-- Vote Count
SELECT MIN(vote_count), MAX(vote_count), AVG(vote_count) FROM movies;
```

---

## 📚 Referências

- **Lógica Fuzzy**: Conjunto fuzzy com função de pertencimento trapezoidal
- **Fuzzificação**: Conversão de valores numéricos para graus de pertencimento
- **Defuzzificação**: Conversão de resultados fuzzy em valor numérico (média ponderada)
- **Operador AND**: Utiliza mínimo (min)
- **Operador OR**: Utiliza máximo (max)
