# Quick Start: Resumo Executivo

## 📌 TL;DR (Resumo Executivo)

### **O que é o Sistema?**
Um algoritmo de **Lógica Fuzzy** que avalia restaurantes (ou filmes) em uma **escala numérica contínua** (1-10) baseado em múltiplas variáveis usando **regras de inferência fuzzy**.

---

## 🎬 Como Funciona em 3 Etapas

### **1️⃣ FUZZIFICAÇÃO** 
Converte números reais em "graus de pertença" (0-1) a categorias fuzzy.

```
Entrada: Preço = R$45
         ↓
Saída: "Barato" = 0.75 (75% de pertença)
       "Médio" = 0.25 (25% de pertença)
       "Caro" = 0.0 (0% de pertença)
```

### **2️⃣ APLICAÇÃO DE REGRAS**
Combina variáveis fuzzy usando lógica IF-THEN.

```
SE (Voto Alto AND Budget Alto)
ENTÃO Muito Recomendado

min(0.80, 0.74) = 0.74 → Contribui 0.74 para "Muito Recomendado"

Na lógica IF-THEN, ao utilizar AND é considerado a menor variável. Ao utilizar OR é utilizado a maior variável. Nesse exemplo, por se tratar de um AND, foi utilizado o 0.74
```

### **3️⃣ DEFUZZIFICAÇÃO**
Converte resultado fuzzy de volta para número (1-10).

```
Entrada: NaoRec=0, Rec=0.1, MuitoRec=0.74
         ↓
Saída: Score = (0×2 + 0.1×6.5 + 0.74×9) / (0+0.1+0.74) = 8.9
```

---

## 🎯 Adaptação para Filmes: 4 Decisões

| Decisão | Opções | Recomendação |
|---------|--------|--------------|
| **Variáveis** | 4 ou 5? | 4 (Budget, Popularity, Runtime, Vote_Avg) |
| **Incluir Cast?** | Sim/Não | Não (começar sem) |
| **Faixas Fuzzy** | Definir ranges | Usar estatísticas do dataset |
| **Regras** | Quantas? | 5-7 regras básicas |

---

## 🔧 Estrutura de Código Necessária

```
├── GrupoVariaveis.java      ✓ Já existe
├── VariavelFuzzy.java       ✓ Já existe
├── FuzzyMovies.java         ✗ CRIAR (novo)
├── movie_dataset.csv        ✓ Usar como entrada
└── output_filmes.csv        → Output (opcional)
```

---

## 📐 As 4 Variáveis Fuzzy Para Filmes

### **1. BUDGET** (em milhões)
```
Baixo (0-5M) | MedioBaixo (5-40M) | Medio (40-100M) | MedioAlto (100-200M) | Alto (200M+)
```

### **2. POPULARITY** (escala 0-200)
```
Baixa (0-15) | Media (15-50) | Alta (50-150) | MuitoAlta (150+)
```

### **3. RUNTIME** (minutos)
```
Curto (0-90) | Normal (90-140) | Longo (140+)
```

### **4. VOTE_AVERAGE** (0-10)
```
Baixo (0-5) | Medio (5-7) | Alto (7-8.5) | MuitoAlto (8.5-10)
```

---

## 📋 Regras Fuzzy Mínimas (5-7)

```java
// Regra 1: Voto Alto + Budget Alto → Muito Recomendado
rodaRegraE(vars, "Voto_Alto", "Budget_Alto", "MuitoRecomendado");

// Regra 2: Voto Muito Alto → Sempre Muito Recomendado  
rodaRegraE(vars, "Voto_MuitoAlto", "Pop_Media", "MuitoRecomendado");

// Regra 3: Voto Médio + Pop Alta → Recomendado
rodaRegraE(vars, "Voto_Medio", "Pop_Alta", "Recomendado");

// Regra 4: Voto Alto + Pop Normal → Recomendado
rodaRegraE(vars, "Voto_Alto", "Runtime_Normal", "Recomendado");

// Regra 5: Voto Baixo → Não Recomendado
rodaRegraE(vars, "Voto_Baixo", "Pop_Cualquier", "NaoRecomendado");
```

---

## 💻 Código Básico: Estrutura Principal

```java
public class FuzzyMovies {
    public static void main(String[] args) {
        // 1. DEFINIR VARIÁVEIS FUZZY
        GrupoVariaveis grupoBudget = new GrupoVariaveis();
        grupoBudget.add(new VariavelFuzzy("Budget_Bajo", 0, 0, 1, 5));
        // ... mais 4 categorias
        
        // Similar para Popularity, Runtime, Vote_Average
        
        // 2. LER CSV
        BufferedReader bfr = new BufferedReader(
            new FileReader("movie_dataset.csv"));
        
        String line;
        while ((line = bfr.readLine()) != null) {
            String[] spl = line.split(",");
            
            // 3. EXTRAIR DADOS (com tratamento de erros)
            float budget = Float.parseFloat(spl[1]) / 1_000_000;
            float popularity = Float.parseFloat(spl[9]);
            float runtime = Float.parseFloat(spl[14]);
            float voteAvg = Float.parseFloat(spl[19]);
            
            // 4. FUZZIFICAR
            HashMap<String,Float> vars = new HashMap<>();
            grupoBudget.fuzzifica(budget, vars);
            grupoPopularidade.fuzzifica(popularity, vars);
            grupoRuntime.fuzzifica(runtime, vars);
            grupoVoto.fuzzifica(voteAvg, vars);
            
            // 5. APLICAR REGRAS
            rodaRegraE(vars, "Voto_Alto", "Budget_Alto", "MuitoRecomendado");
            // ... mais regras
            
            // 6. DEFUZZIFICAR (calcular score final)
            float score = (naoRec*2 + rec*6.5 + muitoRec*9) 
                         / (naoRec + rec + muitoRec);
            
            // 7. OUTPUT
            System.out.println(titulo + ";" + score);
        }
    }
}
```

---

## 🚀 Passos Práticos (Fazer Agora!)

### **Hoje:**
1. ✅ Ler DOCUMENTACAO_SISTEMA_FUZZY.md
2. ✅ Criar FuzzyMovies.java (copiar estrutura de FuzzyMain.java)
3. ✅ Definir as 4 variáveis fuzzy

### **Amanhã:**
4. ✅ Implementar leitura do CSV com tratamento de erros
5. ✅ Testar com 10 filmes
6. ✅ Ajustar faixas fuzzy (se necessário)

### **Quando estiver confortável:**
7. ✅ Executar com dataset completo
8. ✅ Analisar distribuição de scores
9. ✅ Refinar regras fuzzy

---

## 🎬 Exemplo Rápido: Avatar

```
INPUT:
Budget = 237M, Popularity = 150.5, Runtime = 162, Vote = 7.8

FUZZIFICAÇÃO:
- Budget_Alto: 0.63
- Pop_MuitoAlta: 1.0
- Runtime_Longo: 0.86
- Voto_Alto: 0.80

REGRA: Voto_Alto AND Budget_Alto
- min(0.80, 0.63) = 0.63 → MuitoRecomendado

DEFUZZIFICAÇÃO:
Score = (0×2 + 0×6.5 + 0.63×9) / 0.63
      = 5.67 / 0.63
      = 9.0 ✓ EXCELENTE!
```

---

## ⚠️ 5 Cuidados Importantes

1. **Budget pode ser 0 → Pular filme**
   ```java
   if (budget == 0 || voteAvg == 0) continue;
   ```

2. **Index errado → Exception**
   ```java
   if (spl.length < 20) continue;
   ```

3. **Divisão por zero → NaN**
   ```java
   if ((naoRec + rec + muitoRec) > 0) score = ...;
   ```

4. **Outliers em Popularity → Distorção**
   ```java
   if (popularity > 500) popularity = 500;
   ```

5. **Formato CSV com aspas → Split errado**
   ```java
   line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)")
   ```

---

## 📊 Validação: Teste Isso

Após implementar, verificar:

```
Avatar:        Score > 8? ✓
Inception:     Score > 8? ✓
Titanic:       Score > 7? ✓
Filme ruim:    Score < 4? ✓
Filme médio:   Score 5-7? ✓
```

Se tudo OK → Sistema está funcionando!

---

## 🗂️ Documentação Completa (Referência)

| Arquivo | Conteúdo |
|---------|----------|
| [DOCUMENTACAO_SISTEMA_FUZZY.md](DOCUMENTACAO_SISTEMA_FUZZY.md) | Explicação detalhada do sistema |
| [GUIA_PRATICO_IMPLEMENTACAO.md](GUIA_PRATICO_IMPLEMENTACAO.md) | Template de código + dicas |
| [RESUMO_VISUAL_EXEMPLOS.md](RESUMO_VISUAL_EXEMPLOS.md) | Gráficos + exemplos passo-a-passo |
| [CHECKLIST_E_FAQ.md](CHECKLIST_E_FAQ.md) | Checklist + FAQ + troubleshooting |

---

## 💡 Próximas 2 Horas: Roteiro

### **0:00 - 0:30:** Leitura
- [ ] Ler DOCUMENTACAO_SISTEMA_FUZZY.md (seções 1-3)
- [ ] Entender funções trapezóides

### **0:30 - 1:00:** Análise
- [ ] Abrir movie_dataset.csv
- [ ] Verificar colunas (budget, genres, popularity, runtime, vote_average, cast)
- [ ] Registrar min/max de cada coluna

### **1:00 - 1:45:** Codificação
- [ ] Criar FuzzyMovies.java
- [ ] Copiar estrutura de FuzzyMain.java
- [ ] Implementar leitura do CSV

### **1:45 - 2:00:** Teste
- [ ] Executar com 5-10 filmes
- [ ] Validar output
- [ ] Corrigir erros

**Resultado esperado:** FuzzyMovies.java funcionando com output básico! 🎉

---

## 🎓 Conceitos-Chave em 1 Frase Cada

- **Fuzzificação:** Converter números em "quanto" eles pertencem a uma categoria
- **Trapezóide:** Forma de função que define as 5 regiões de pertença
- **AND Fuzzy:** Usar o valor mínimo quando combinar duas condições
- **Defuzzificação:** Converter resultado fuzzy (graus) de volta em número final
- **Regra Fuzzy:** IF-THEN que mapeia entrada para saída usando lógica fuzzy

---

## 📞 Resumo Final

```
┌──────────────────────────────────────────────────┐
│ O QUE FAZER:                                     │
│ 1. Entender o sistema (ler documentação)         │
│ 2. Adaptar para filmes (4 variáveis)             │
│ 3. Implementar FuzzyMovies.java                  │
│ 4. Testar e validar                              │
│ 5. Refinar se necessário                         │
└──────────────────────────────────────────────────┘
```

**Tempo estimado:** 3-4 horas para implementação básica

**Documentação completa:** 4 arquivos (este + 3 detalhados)

**Suporte:** Use CHECKLIST_E_FAQ.md para dúvidas

---

**BORA COMEÇAR!** 🚀🎬

Comece pelo DOCUMENTACAO_SISTEMA_FUZZY.md, depois use GUIA_PRATICO_IMPLEMENTACAO.md como template.
