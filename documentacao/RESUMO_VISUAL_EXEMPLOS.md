# Resumo Visual: Sistema Fuzzy de Classificação

## 🎬 Exemplo Completo: Avatar vs Inception

### **FILME 1: Avatar**
```
Entrada (do CSV):
- Budget: $237,000,000
- Popularity: 150.5
- Runtime: 162 minutos  
- Vote Average: 7.8
- Vote Count: 26,514

STEP 1: FUZZIFICAÇÃO

Budget = 237M (dividido por 1M = 237)
┌─────────────────────────────────────────────────────────────┐
│ Budget_Baixo        (0, 0, 1, 5)           → 0.00           │
│ Budget_MedioBaixo   (1, 5, 20, 40)         → 0.00           │
│ Budget_Medio        (20, 40, 60, 100)      → 0.00           │
│ Budget_MedioAlto    (60, 100, 150, 200)    → 0.00           │
│ Budget_Alto         (150, 200, 300, 500)   → 0.74 ✓         │
└─────────────────────────────────────────────────────────────┘

Popularity = 150.5
┌─────────────────────────────────────────────────────────────┐
│ Pop_Baixa           (0, 0, 5, 15)          → 0.00           │
│ Pop_Media           (5, 15, 30, 50)        → 0.00           │
│ Pop_Alta            (30, 50, 100, 150)     → 0.01           │
│ Pop_MuitoAlta       (100, 150, 500, 500)   → 1.00 ✓         │
└─────────────────────────────────────────────────────────────┘

Runtime = 162 minutos
┌─────────────────────────────────────────────────────────────┐
│ Runtime_Curto       (0, 0, 60, 90)         → 0.00           │
│ Runtime_Normal      (80, 100, 120, 140)    → 0.00           │
│ Runtime_Longo       (130, 150, 200, 250)   → 0.86 ✓         │
└─────────────────────────────────────────────────────────────┘

Vote Average = 7.8
┌─────────────────────────────────────────────────────────────┐
│ Voto_Baixo          (0, 0, 3, 5)           → 0.00           │
│ Voto_Medio          (4, 5, 6.5, 7.5)       → 0.20           │
│ Voto_Alto           (6, 7, 8, 9)           → 0.80 ✓         │
│ Voto_MuitoAlto      (7.5, 8.5, 10, 10)     → 0.40           │
└─────────────────────────────────────────────────────────────┘

STEP 2: APLICAR REGRAS FUZZY

Regra 1: Voto_Alto AND Budget_Medio → MuitoRecomendado
         min(0.80, 0.00) = 0.00

Regra 2: Voto_Alto AND Budget_Alto → MuitoRecomendado
         min(0.80, 0.74) = 0.74 ✓
         MuitoRecomendado = max(0.00, 0.74) = 0.74

Regra 3: Voto_Medio AND Pop_Alta → Recomendado
         min(0.20, 0.01) = 0.01

Resultado das Regras:
┌─────────────────────────────────────────────────────────────┐
│ NaoRecomendado = 0.00                                       │
│ Recomendado = 0.01                                          │
│ MuitoRecomendado = 0.74                                     │
└─────────────────────────────────────────────────────────────┘

STEP 3: DEFUZZIFICAÇÃO (Média Ponderada)

Score = (0.00×2 + 0.01×6.5 + 0.74×9) / (0.00 + 0.01 + 0.74)
      = (0 + 0.065 + 6.66) / 0.75
      = 6.725 / 0.75
      = 8.97 ✓✓✓ MUITO RECOMENDADO!
```

---

### **FILME 2: Inception**
```
Entrada (do CSV):
- Budget: $160,000,000
- Popularity: 140.2
- Runtime: 148 minutos
- Vote Average: 8.8
- Vote Count: 26,514

STEP 1: FUZZIFICAÇÃO

Budget = 160M
├─ Budget_Alto (150, 200, 300, 500) → 0.50 ✓
└─ Outros → 0.00

Popularity = 140.2
├─ Pop_MuitoAlta (100, 150, 500, 500) → 0.80 ✓
└─ Outros → 0.00

Runtime = 148
├─ Runtime_Longo (130, 150, 200, 250) → 0.90 ✓
└─ Outros → 0.00

Vote Average = 8.8
├─ Voto_Alto (6, 7, 8, 9) → 0.80
├─ Voto_MuitoAlto (7.5, 8.5, 10, 10) → 0.60 ✓
└─ Outros → 0.00

STEP 2: APLICAR REGRAS FUZZY

Regra: Voto_MuitoAlto AND Budget_Alto → MuitoRecomendado
       min(0.60, 0.50) = 0.50 ✓

MuitoRecomendado = 0.50

STEP 3: DEFUZZIFICAÇÃO

Score = (0.00×2 + 0.00×6.5 + 0.50×9) / 0.50
      = 4.5 / 0.50
      = 9.0 ✓✓✓ MUITO RECOMENDADO!
```

---

## 📈 Visualização: Funções de Pertencimento

### **Trapezóide Fuzzy**
```
           
    1.0    |        ___
           |       /   \
Grau de    |      /     \
Pertença   | 0.5 /       \
           |    /         \
    0.0    |___/___________\___
           b1  t1    t2   b2

Exemplo: "Barato" (10, 20, 30, 60)

           |        ___
       1.0 |       /   \
           |      /     \
           | 0.5 /       \
           |    /         \
       0.0 |___/___________\___
             10  20    30   60  (Preço em reais)
```

### **Vote Average (0-10)**
```
Voto_Baixo: (0, 0, 3, 5)
    1.0 |___\
        |    \___
    0.5 |        \___
        |            \__
    0.0 |________________
        0   1   2   3   4   5  (nota)

Voto_Medio: (4, 5, 6.5, 7.5)
    1.0 |    ___
        |   /   \
    0.5 |  /     \
        | /       \
    0.0 |/_________\__
        4   5  6.5  7.5

Voto_Alto: (6, 7, 8, 9)
    1.0 |      ___
        |     /   \
    0.5 |    /     \
        |   /       \
    0.0 |__/________\__
        6   7    8    9

Voto_MuitoAlto: (7.5, 8.5, 10, 10)
    1.0 |         ___
        |        /   \____
    0.5 |       /         \
        |      /           \___
    0.0 |_____/______________
        7.5  8.5    10
```

---

## 🧮 Operadores Lógicos Fuzzy

### **Operador AND (Intersecção)**
```java
float resultado = Math.min(valor1, valor2);
```

**Exemplo:**
```
Voto_Alto = 0.80
Budget_Alto = 0.74
Resultado = min(0.80, 0.74) = 0.74
```

**Interpretação:** "E quanto à combinação de Voto Alto E Budget Alto?"
Resposta: 74% de pertença a essa combinação.

### **Operador OR (União)**
```java
float resultado = Math.max(valor1, valor2);
```

**Exemplo:**
```
NaoRecomendado = 0.10
Recomendado = 0.50
Resultado = max(0.10, 0.50) = 0.50
```

### **Operador NOT (Negação)**
```java
float resultado = 1.0f - valor;
```

---

## 📋 Tabela de Regras: Restaurante (Original)

```
PREÇO + RATING → ATRATIVIDADE
┌─────────────────┬──────────┬──────────────────┐
│ Preço           │ Rating   │ Atratividade     │
├─────────────────┼──────────┼──────────────────┤
│ Barato          │ Bom      │ Atrativo         │
│ Muito Barato    │ Bom      │ Atrativo         │
│ Muito Barato    │ Muito Bom│ Muito Atrativo   │
│ Barato          │ Muito Bom│ Muito Atrativo   │
│ Barato          │ Ruim     │ Não Atrativo     │
│ Muito Barato    │ Ruim     │ Atrativo         │
│ Muito Barato    │ Muito Ruim│ Não Atrativo    │
│ Muito Caro      │ Ruim     │ Não Atrativo     │
│ Muito Caro      │ Bom      │ Não Atrativo     │
│ Muito Caro      │ Muito Bom│ Atrativo         │
└─────────────────┴──────────┴──────────────────┘
```

## 📋 Tabela de Regras: Filmes (Proposto)

```
VOTO + BUDGET → RECOMENDAÇÃO
┌──────────────┬──────────────┬──────────────────┐
│ Voto         │ Budget       │ Recomendação     │
├──────────────┼──────────────┼──────────────────┤
│ Alto         │ Medio        │ MuitoRecomendado │
│ MuitoAlto    │ Cualquier    │ MuitoRecomendado │
│ Alto         │ Alto         │ MuitoRecomendado │
│ Medio        │ Pop Alta     │ Recomendado      │
│ Alto         │ Pop Media    │ Recomendado      │
│ Bajo         │ Cualquier    │ NoRecomendado    │
└──────────────┴──────────────┴──────────────────┘
```

---

## 🔢 Pesos de Defuzzificação

### **Restaurante (Original)**
```
Score = (NA × 1.5 + A × 7.0 + MA × 9.5) / (NA + A + MA)

Peso NA (Não Atrativo):  1.5  (nota baixa)
Peso A  (Atrativo):      7.0  (nota média)
Peso MA (Muito Atrativo):9.5  (nota alta)
```

### **Filme (Proposto)**
```
Score = (NR × 2.0 + R × 6.5 + MR × 9.0) / (NR + R + MR)

Peso NR (Não Recomendado):  2.0  (evitar)
Peso R  (Recomendado):      6.5  (assistir)
Peso MR (Muito Recomendado):9.0  (imprescindível!)
```

---

## 🎯 Exemplo Iterativo: Calculando Avatar Passo a Passo

```
Avatar
Budget = 237M, Popularity = 150.5, Runtime = 162, Vote = 7.8

1. Fuzzificar Budget = 237M
   - Procurar qual faixa se encaixa melhor
   - Budget_Alto (150, 200, 300, 500)
   - Como 237 está entre 200 e 300:
     Pertença = 1.0 - ((237-200)/(300-200)) = 1.0 - 0.37 = 0.63
   - Mas usando a função da VariavelFuzzy:
     (237 está em [200, 300], então ramp descendo)
     Pertença = 1.0 - ((237-200)/(300-200)) = 0.63

2. Fuzzificar Vote = 7.8
   - Voto_Alto (6, 7, 8, 9): 7.8 está em [7, 8] (pico completo)
   - Pertença = 1.0
   - Voto_MuitoAlto (7.5, 8.5, 10, 10): 7.8 está em [7.5, 8.5]
   - Pertença = 1.0

3. Aplicar Regra: Voto_Alto AND Budget_Alto
   - min(1.0, 0.63) = 0.63
   - MuitoRecomendado recebe 0.63

4. Defuzzificar
   - Score = (0×2 + 0×6.5 + 0.63×9) / 0.63
   - Score = 5.67 / 0.63 = 9.0 ✓ MUITO RECOMENDADO!
```

---

## 💡 Dicas de Ajuste

### **Se o score está muito alto (todos acima de 7):**
```java
// Aumentar threshold de fuzzificação
// Ex: Voto_Alto (6, 7, 8, 9) → (6.5, 7.5, 8.5, 9)
// Fazer com que menos filmes entrem em "MuitoRecomendado"
```

### **Se o score está muito baixo (todos abaixo de 4):**
```java
// Diminuir threshold
// Ex: Voto_Alto (6, 7, 8, 9) → (5, 6, 7, 8)
// Fazer com que mais filmes entrem em "Recomendado"
```

### **Se há muita dispersão:**
```java
// Aumentar os pesos na defuzzificação
// Ex: R × 6.5 → R × 7.5
// Isso dá mais peso aos "Recomendados"
```

---

## 🔍 Validação: Como Saber se Está Certo?

1. **Avatar, Inception, Titanic** → Scores altos (8+)
2. **Filmes ruins (baixo voto)** → Scores baixos (3-)
3. **Filmes medianos** → Scores médios (5-7)
4. **Distribuição suave** → Sem picos estranhos

Se esses critérios forem atendidos, o sistema está funcionando! 🎉

