# Guia Prático: Implementação para Movie Dataset

## 🚀 Começando

### Passo 1: Análise do Dataset

Primeiro, abra o `movie_dataset.csv` e execute uma análise rápida:

```java
// Script análise - executar uma vez para entender os dados
BufferedReader bfr = new BufferedReader(new FileReader("movie_dataset.csv"));
String header = bfr.readLine();
System.out.println("Header: " + header);

int count = 0;
while (bfr.readLine() != null && count < 50) {
    count++;
}
System.out.println("Total de filmes (primeiros 50): " + count);
```

### Passo 2: Identificar os Índices das Colunas

Contar as colunas no CSV (separadas por vírgula):

```
0: index
1: budget
2: genres
3: homepage
4: id
5: keywords
6: original_language
7: original_title
8: overview
9: popularity
10: production_companies
11: production_countries
12: release_date
13: revenue
14: runtime
15: spoken_languages
16: status
17: tagline
18: title
19: vote_average
20: vote_count
21: cast
22: crew
23: director
```

**Colunas importantes para o sistema fuzzy:**
- `spl[1]` → **Budget**
- `spl[2]` → **Genres** (opcional, complexo)
- `spl[9]` → **Popularity**
- `spl[14]` → **Runtime**
- `spl[19]` → **Vote_Average**
- `spl[20]` → **Vote_Count**
- `spl[18]` → **Title**
- `spl[21]` → **Cast** (opcional, customização)

---

## 📐 Escolher as 4 Variáveis Fuzzy

### **Decisão 1: Incluir Cast?**

| Opção | Vantagem | Desvantagem |
|-------|----------|-------------|
| **Sem Cast** | Mais simples, automático | Menos personalização |
| **Com Cast** | Customizável por ator favorito | Requer parsing JSON/string |

**Recomendação:** Começar SEM cast (4 variáveis), depois adicionar opcionalmente.

---

## 🛠️ Template: FuzzyMovies.java

```java
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class FuzzyMovies {
    public static void main(String[] args) {
        // ========== DEFINIR VARIÁVEIS FUZZY ==========
        
        // 1. BUDGET (em milhões de dólares, dividido por 1.000.000 na leitura)
        GrupoVariaveis grupoBudget = new GrupoVariaveis();
        grupoBudget.add(new VariavelFuzzy("Budget_Baixo", 0, 0, 1, 5));
        grupoBudget.add(new VariavelFuzzy("Budget_MedioBaixo", 1, 5, 20, 40));
        grupoBudget.add(new VariavelFuzzy("Budget_Medio", 20, 40, 60, 100));
        grupoBudget.add(new VariavelFuzzy("Budget_MedioAlto", 60, 100, 150, 200));
        grupoBudget.add(new VariavelFuzzy("Budget_Alto", 150, 200, 300, 500));
        
        // 2. POPULARITY (escala 0-200 aproximadamente)
        GrupoVariaveis grupoPopularidade = new GrupoVariaveis();
        grupoPopularidade.add(new VariavelFuzzy("Pop_Baixa", 0, 0, 5, 15));
        grupoPopularidade.add(new VariavelFuzzy("Pop_Media", 5, 15, 30, 50));
        grupoPopularidade.add(new VariavelFuzzy("Pop_Alta", 30, 50, 100, 150));
        grupoPopularidade.add(new VariavelFuzzy("Pop_MuitoAlta", 100, 150, 500, 500));
        
        // 3. RUNTIME (em minutos)
        GrupoVariaveis grupoRuntime = new GrupoVariaveis();
        grupoRuntime.add(new VariavelFuzzy("Runtime_Curto", 0, 0, 60, 90));
        grupoRuntime.add(new VariavelFuzzy("Runtime_Normal", 80, 100, 120, 140));
        grupoRuntime.add(new VariavelFuzzy("Runtime_Longo", 130, 150, 200, 250));
        
        // 4. VOTE_AVERAGE (escala 0-10)
        GrupoVariaveis grupoVoto = new GrupoVariaveis();
        grupoVoto.add(new VariavelFuzzy("Voto_Baixo", 0, 0, 3, 5));
        grupoVoto.add(new VariavelFuzzy("Voto_Medio", 4, 5, 6.5f, 7.5f));
        grupoVoto.add(new VariavelFuzzy("Voto_Alto", 6, 7, 8, 9));
        grupoVoto.add(new VariavelFuzzy("Voto_MuitoAlto", 7.5f, 8.5f, 10, 10));
        
        // 5. SAÍDA FUZZY (Recomendação)
        GrupoVariaveis grupoRecomendacao = new GrupoVariaveis();
        // Será preenchido pelas regras
        
        // ========== LER CSV ==========
        try {
            BufferedReader bfr = new BufferedReader(
                new FileReader(new File("movie_dataset.csv"))
            );
            
            String header = bfr.readLine();
            System.out.println("Processando filmes...\n");
            // Imprimir header para validação (opcional)
            // System.out.println(header);
            
            String line = "";
            int linhaNum = 0;
            
            while ((line = bfr.readLine()) != null && linhaNum < 100) {
                linhaNum++;
                
                try {
                    String[] spl = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // CSV com quotes
                    
                    // Validar se tem dados mínimos
                    if (spl.length < 21) continue;
                    
                    String titulo = spl[18].trim();  // Title
                    
                    // ========== TRATAMENTO DE DADOS ==========
                    
                    // Budget (índice 1)
                    float budget = 0;
                    try {
                        String budgetStr = spl[1].trim();
                        if (!budgetStr.isEmpty() && !budgetStr.equals("null")) {
                            budget = Float.parseFloat(budgetStr) / 1_000_000; // Converter para milhões
                        }
                    } catch (NumberFormatException e) {
                        budget = 0;
                    }
                    
                    // Popularity (índice 9)
                    float popularity = 0;
                    try {
                        popularity = Float.parseFloat(spl[9].trim());
                    } catch (NumberFormatException e) {
                        popularity = 0;
                    }
                    
                    // Runtime (índice 14)
                    float runtime = 0;
                    try {
                        String runtimeStr = spl[14].trim();
                        if (!runtimeStr.isEmpty() && !runtimeStr.equals("null")) {
                            runtime = Float.parseFloat(runtimeStr);
                        }
                    } catch (NumberFormatException e) {
                        runtime = 0;
                    }
                    
                    // Vote Average (índice 19)
                    float voteAvg = 0;
                    try {
                        String voteStr = spl[19].trim();
                        if (!voteStr.isEmpty() && !voteStr.equals("null")) {
                            voteAvg = Float.parseFloat(voteStr);
                        }
                    } catch (NumberFormatException e) {
                        voteAvg = 0;
                    }
                    
                    // Vote Count (índice 20)
                    int voteCount = 0;
                    try {
                        String voteCountStr = spl[20].trim();
                        if (!voteCountStr.isEmpty() && !voteCountStr.equals("null")) {
                            voteCount = Integer.parseInt(voteCountStr);
                        }
                    } catch (NumberFormatException e) {
                        voteCount = 0;
                    }
                    
                    // Pular filmes com dados insuficientes
                    if (budget == 0 || voteAvg == 0) continue;
                    
                    // ========== FUZZIFICAÇÃO ==========
                    HashMap<String, Float> variaveis = new HashMap<>();
                    
                    grupoBudget.fuzzifica(budget, variaveis);
                    grupoPopularidade.fuzzifica(popularity, variaveis);
                    grupoRuntime.fuzzifica(runtime, variaveis);
                    grupoVoto.fuzzifica(voteAvg, variaveis);
                    
                    // ========== APLICAR REGRAS FUZZY ==========
                    
                    // Inicializar saídas
                    if (!variaveis.containsKey("NaoRecomendado")) {
                        variaveis.put("NaoRecomendado", 0f);
                    }
                    if (!variaveis.containsKey("Recomendado")) {
                        variaveis.put("Recomendado", 0f);
                    }
                    if (!variaveis.containsKey("MuitoRecomendado")) {
                        variaveis.put("MuitoRecomendado", 0f);
                    }
                    
                    // Regras: Voto Alto + Budget Alto → Muito Recomendado
                    rodaRegraE(variaveis, "Voto_Alto", "Budget_Medio", "MuitoRecomendado");
                    rodaRegraE(variaveis, "Voto_MuitoAlto", "Budget_Cualquer", "MuitoRecomendado");
                    
                    // Regras: Voto Médio + Pop Alta → Recomendado
                    rodaRegraE(variaveis, "Voto_Medio", "Pop_Alta", "Recomendado");
                    rodaRegraE(variaveis, "Voto_Alto", "Pop_Media", "Recomendado");
                    
                    // Regras: Voto Baixo → Não Recomendado
                    rodaRegraE(variaveis, "Voto_Baixo", "Pop_Cualquier", "NaoRecomendado");
                    
                    // ========== DEFUZZIFICAÇÃO ==========
                    
                    float naoRec = variaveis.get("NaoRecomendado");
                    float rec = variaveis.get("Recomendado");
                    float muitoRec = variaveis.get("MuitoRecomendado");
                    
                    float score = 0;
                    if ((naoRec + rec + muitoRec) > 0) {
                        score = (naoRec * 2f + rec * 6.5f + muitoRec * 9f) 
                               / (naoRec + rec + muitoRec);
                    }
                    
                    // ========== OUTPUT ==========
                    System.out.println(
                        titulo + ";" + 
                        (""+budget).replace(".", ",") + ";" +
                        (""+popularity).replace(".", ",") + ";" +
                        runtime + ";" +
                        (""+voteAvg).replace(".", ",") + ";" +
                        (""+naoRec).replace(".", ",") + ";" +
                        (""+rec).replace(".", ",") + ";" +
                        (""+muitoRec).replace(".", ",") + ";" +
                        (""+score).replace(".", ",")
                    );
                    
                } catch (Exception e) {
                    // Pular linhas com erro
                    continue;
                }
            }
            
            bfr.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Operador AND (mínimo)
    private static void rodaRegraE(HashMap<String, Float> vars, 
                                    String var1, String var2, String resultado) {
        if (!vars.containsKey(var1) || !vars.containsKey(var2)) {
            return;
        }
        
        float v = Math.min(vars.get(var1), vars.get(var2));
        float vAtual = vars.getOrDefault(resultado, 0f);
        vars.put(resultado, Math.max(vAtual, v));
    }
    
    // Operador OR (máximo) - se precisar
    private static void rodaRegraOU(HashMap<String, Float> vars, 
                                     String var1, String var2, String resultado) {
        if (!vars.containsKey(var1) || !vars.containsKey(var2)) {
            return;
        }
        
        float v = Math.max(vars.get(var1), vars.get(var2));
        float vAtual = vars.getOrDefault(resultado, 0f);
        vars.put(resultado, Math.max(vAtual, v));
    }
}
```

---

## 🎯 Próximos Passos (Recomendação)

### 1. **Testar com dados reais**
```
Após criar FuzzyMovies.java, analisar os outputs e ajustar as faixas fuzzy.
Exemplo: Se todos os filmes têm score > 8, aumentar o threshold.
```

### 2. **Coletar estatísticas do dataset**
```java
// Adicionar ao código temporariamente:
float minBudget = Float.MAX_VALUE, maxBudget = 0;
float minPop = Float.MAX_VALUE, maxPop = 0;
// ... etc

// Ao final, imprimir:
System.out.println("Budget: " + minBudget + " - " + maxBudget);
System.out.println("Popularity: " + minPop + " - " + maxPop);
```

### 3. **Ajustar regras fuzzy**
Depois de ver os resultados, refinar as regras para separar melhor os filmes.

### 4. **(Opcional) Adicionar Cast**
Se quiser personalizar por ator favorito:

```java
public class AtoresNota {
    public HashMap<String, Float> notasAtores = new HashMap<>();
    
    public AtoresNota() {
        // Seus atores favoritos
        notasAtores.put("Leonardo DiCaprio", 10.0f);
        notasAtores.put("Tom Hanks", 9.8f);
        // ... adicionar quantos quiser
    }
}
```

---

## ⚠️ Cuidados Importantes

### 1. **Valores NULL/Vazios**
```java
// O CSV pode ter dados faltantes
if (spl[1] == null || spl[1].trim().isEmpty() || spl[1].equals("null")) {
    continue; // Pular filme
}
```

### 2. **OutLiers em Popularity**
Se alguns filmes têm popularity >> 500:
```java
// Limitar ao máximo da faixa fuzzy
if (popularity > 500) popularity = 500;
```

### 3. **Runtime Zero**
```java
// Alguns filmes podem ter runtime = 0
if (runtime == 0) continue; // Pular
```

### 4. **Índices do CSV**
Verificar se os índices correspondem! O CSV pode ter formatação diferente.

---

## 📊 Exemplo de Output Esperado

```
Avatar; 237,000000; 150,5; 162; 7,8; 0,1; 0,4; 0,5; 8,2
Inception; 160,000000; 140,2; 148; 8,8; 0,05; 0,3; 0,65; 8,9
Titanic; 200,000000; 120,5; 194; 7,2; 0,2; 0,45; 0,35; 7,5
```

---

## 🔧 Troubleshooting

| Problema | Causa | Solução |
|----------|-------|--------|
| Todos os filmes têm score baixo | Faixas fuzzy muito restritivas | Aumentar os intervalos |
| Nenhum filme entra em "MuitoRecomendado" | Regras muito difíceis | Relaxar as condições |
| Exception ao ler CSV | Índice errado ou quebra de linha | Verificar os índices |
| Score sempre é 6.5 | Distribuição de votos é uniforme | Normal, não é um problema |

