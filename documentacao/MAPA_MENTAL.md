# Mapa Mental: Sistema Fuzzy de Filmes

```
                            ┌─────────────────────────────────┐
                            │  SISTEMA FUZZY PARA FILMES      │
                            └────────────┬────────────────────┘
                                         │
                    ┌────────────────────┼────────────────────┐
                    │                    │                    │
              ┌─────▼─────┐      ┌──────▼──────┐      ┌──────▼──────┐
              │ENTRADA     │      │PROCESSAMENTO│      │SAÍDA        │
              │(CSV Data)  │      │(Cálculos)   │      │(Score)      │
              └─────┬─────┘      └──────┬──────┘      └──────┬──────┘
                    │                   │                    │
            ┌───────┴───────┐   ┌───────┴────────┐   ┌──────┴──────┐
            │               │   │                │   │             │
        Budget      Popularity Runtime   Vote_Avg    Score Final   
        (spl[1])    (spl[9])   (spl[14]) (spl[19])   (1-10)
            │               │   │                │   │
            │               │   │                │   └────┬────────┐
            │               │   │                │        │        │
            └───────┬───────┘   └────────┬───────┘    ┌───▼──┐ ┌──▼───┐
                    │                    │            │ NR:2 │ │ R:6.5│
              FUZZIFICAÇÃO               │            └──────┘ └──────┘
              (0-1 valores)              │                │
                    │                    │            ┌───▼──┐
            ┌───────┴────────────────────┴──────┐    │MR:9.0│
            │                                    │    └──────┘
        ┌───▼────┐ ┌────────┐ ┌───────┐ ┌──────▼──┐
        │Budget_ │ │Pop_    │ │Runtime│ │Voto_   │
        │Baixo   │ │Baixa   │ │Curto  │ │Baixo   │
        │(0.00)  │ │(0.00)  │ │(0.00) │ │(0.00)  │
        └────────┘ └────────┘ └───────┘ └────────┘
        
        ┌───▼────┐ ┌────────┐ ┌───────┐ ┌──────▼──┐
        │Budget_ │ │Pop_    │ │Runtime│ │Voto_   │
        │Médio   │ │Média   │ │Normal │ │Médio   │
        │(0.50)  │ │(0.30)  │ │(0.75) │ │(0.60)  │
        └────────┘ └────────┘ └───────┘ └────────┘
        
        ┌───▼────┐ ┌────────┐ ┌───────┐ ┌──────▼──┐
        │Budget_ │ │Pop_    │ │Runtime│ │Voto_   │
        │Alto    │ │Alta    │ │Longo  │ │Alto    │
        │(0.74)  │ │(0.80)  │ │(0.86) │ │(0.80)  │
        └────────┘ └────────┘ └───────┘ └────────┘
                    │
                REGRAS FUZZY
            (IF-THEN com AND/OR)
                    │
        ┌───────────┼───────────┐
        │           │           │
    ┌───▼──┐   ┌───▼──┐   ┌───▼──┐
    │Regra1│   │Regra2│   │Regra3│
    │...   │   │...   │   │...   │
    └───┬──┘   └───┬──┘   └───┬──┘
        │          │          │
        │    DEFUZZIFICAÇÃO   │
        └────────┬────────────┘
                 │
         ┌───────▼────────┐
         │  Média Ponderada
         │  Score = (NR×2 + R×6.5 + MR×9) 
         │          ─────────────────────
         │           (NR + R + MR)
         └───────┬────────┘
                 │
         ┌───────▼─────────┐
         │  Score Final    │
         │  (1 a 10)       │
         │  Ex: 8.9        │
         └─────────────────┘
```

---

## 🎯 Variáveis Fuzzy: Estrutura Completa

```
┌─────────────────────────────────────────────────────────────┐
│ BUDGET (milhões)                                            │
├─────────────────────────────────────────────────────────────┤
│ Budget_Baixo:      0────5    (0, 0, 1, 5)                  │
│ Budget_MedioBaixo: 1───40    (1, 5, 20, 40)                │
│ Budget_Medio:      20──100   (20, 40, 60, 100)             │
│ Budget_MedioAlto:  60──200   (60, 100, 150, 200)           │
│ Budget_Alto:       150─500   (150, 200, 300, 500)          │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ POPULARITY (0-200+)                                         │
├─────────────────────────────────────────────────────────────┤
│ Pop_Baixa:      0──15    (0, 0, 5, 15)                     │
│ Pop_Media:      5──50    (5, 15, 30, 50)                   │
│ Pop_Alta:       30─150   (30, 50, 100, 150)                │
│ Pop_MuitoAlta:  100─500  (100, 150, 500, 500)              │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ RUNTIME (minutos)                                           │
├─────────────────────────────────────────────────────────────┤
│ Runtime_Curto:   0──90    (0, 0, 60, 90)                   │
│ Runtime_Normal:  80─140   (80, 100, 120, 140)              │
│ Runtime_Longo:   130─250  (130, 150, 200, 250)             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ VOTE_AVERAGE (0-10)                                         │
├─────────────────────────────────────────────────────────────┤
│ Voto_Baixo:      0──5    (0, 0, 3, 5)                      │
│ Voto_Medio:      4──7.5  (4, 5, 6.5, 7.5)                  │
│ Voto_Alto:       6──9    (6, 7, 8, 9)                      │
│ Voto_MuitoAlto:  7.5─10  (7.5, 8.5, 10, 10)                │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 Ciclo de Fuzzificação: Exemplo Prático

```
Entrada: Vote = 7.8

Passo 1: Verificar cada variável fuzzy do grupo Voto

┌─ Voto_Baixo (0, 0, 3, 5)
│  7.8 > 5? SIM → Não pertence → 0.0
│
├─ Voto_Medio (4, 5, 6.5, 7.5)
│  7.8 > 7.5? SIM → Não pertence (passou do pico) → 0.0
│
├─ Voto_Alto (6, 7, 8, 9)
│  6 ≤ 7.8 ≤ 9? SIM
│  7.8 está em [7, 8]? SIM → No pico → 1.0 ✓
│
├─ Voto_MuitoAlto (7.5, 8.5, 10, 10)
│  7.5 ≤ 7.8 ≤ 10? SIM
│  7.8 está em [7.5, 8.5]? SIM → No pico → 1.0 ✓
│
└─ Saída:
   HashMap: {
     "Voto_Baixo": 0.0,
     "Voto_Medio": 0.0,
     "Voto_Alto": 1.0,
     "Voto_MuitoAlto": 1.0
   }
```

---

## 🎮 Ciclo de Regras: Aplicação

```
Entrada (após fuzzificação):
{
  "Voto_Alto": 0.80,
  "Voto_MuitoAlto": 0.60,
  "Budget_Alto": 0.63,
  "Budget_Medio": 0.00,
  "Pop_MuitoAlta": 0.80,
  ...
}

Passo 2: Aplicar regras

┌─ Regra 1: Voto_Alto AND Budget_Alto → MuitoRecomendado
│  min(0.80, 0.63) = 0.63
│  MuitoRecomendado = 0.63 ✓
│
├─ Regra 2: Voto_MuitoAlto AND Pop_MuitoAlta → MuitoRecomendado
│  min(0.60, 0.80) = 0.60
│  MuitoRecomendado = max(0.63, 0.60) = 0.63 (já tem 0.63)
│
├─ Regra 3: Voto_Bajo AND ... → NaoRecomendado
│  Voto_Bajo não existe (0.0) → Não aplica
│
└─ Saída após todas as regras:
   {
     "NaoRecomendado": 0.0,
     "Recomendado": 0.0,
     "MuitoRecomendado": 0.63
   }
```

---

## 📊 Ciclo de Defuzzificação: Cálculo

```
Entrada (saída das regras):
{
  "NaoRecomendado": 0.0,
  "Recomendado": 0.0,
  "MuitoRecomendado": 0.63
}

Passo 3: Calcular score final

Formula: Score = (NR × 2.0 + R × 6.5 + MR × 9.0) 
                 ─────────────────────────────────
                    (NR + R + MR)

Substituir:
Numerador   = (0.0 × 2.0) + (0.0 × 6.5) + (0.63 × 9.0)
            = 0.0 + 0.0 + 5.67
            = 5.67

Denominador = 0.0 + 0.0 + 0.63
            = 0.63

Score = 5.67 / 0.63 = 9.0 ✓ EXCELENTE!
```

---

## 🔀 Diferença: Operadores AND vs OR

```
┌─────────────────────────────────────────────────┐
│ AND (min) - Ambas as condições devem ser altas  │
├─────────────────────────────────────────────────┤
│ A = 0.8    B = 0.6    A AND B = min(0.8, 0.6)  │
│                       = 0.6 (mais restritivo)   │
│                                                 │
│ Interpretação: "Quanto à combinação de A e B?"  │
│ Resposta: 60% (limitado pelo menor)             │
└─────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────┐
│ OR (max) - Pelo menos uma condição deve ser alta│
├─────────────────────────────────────────────────┤
│ A = 0.8    B = 0.6    A OR B = max(0.8, 0.6)   │
│                       = 0.8 (menos restritivo)  │
│                                                 │
│ Interpretação: "A ou B (ou ambas)?"             │
│ Resposta: 80% (limitado pelo maior)             │
└─────────────────────────────────────────────────┘

EXEMPLO NO CÓDIGO:
  rodaRegraE(vars, "Voto_Alto", "Budget_Alto", "MuitoRecomendado");
  // Usa: min(0.8, 0.63) = 0.63

  rodaRegraOU(vars, "Voto_Alto", "Pop_Alta", "MuitoRecomendado");
  // Usa: max(0.8, 0.5) = 0.8
```

---

## 📈 Gráfico: Como a Função Trapezóide Funciona

```
CASO 1: Valor = 2.5 (Preço Barato = 10, 20, 30, 60)
                                          
        1.0 |      ___                    
            |     /   \                   
        0.5 |    /     \                  
            |   /       \                 
        0.0 |__/___●_____\__              
            10 20  2.5  60 (valor)        
            
Valor 2.5 está na rampa esquerda (10-20)
Pertença = (2.5 - 10) / (20 - 10) = 0.0

Interpretação: "Muito Barato" = 0% (não é muito barato)


CASO 2: Valor = 25 (Preço Barato = 10, 20, 30, 60)

        1.0 |      ___                    
            |     /   \                   
        0.5 |    /  ●  \                  
            |   /       \                 
        0.0 |__/________\__               
            10 20 25 30  60 (valor)       
            
Valor 25 está no PICO (20-30)
Pertença = 1.0

Interpretação: "Barato" = 100% (completamente barato)


CASO 3: Valor = 45 (Preço Barato = 10, 20, 30, 60)

        1.0 |      ___                    
            |     /   \                   
        0.5 |    /     \  ●               
            |   /       \                 
        0.0 |__/________\__               
            10 20  30  45 60 (valor)      
            
Valor 45 está na rampa direita (30-60)
Pertença = 1.0 - ((45-30)/(60-30)) = 1.0 - 0.5 = 0.5

Interpretação: "Barato" = 50% (razoavelmente barato)
```

---

## 🎯 Mapa de Decisão: Como Escolher Pesos

```
┌─ Favor "Muito Recomendado" (filmes blockbuster)
│  └─ Pesos: (NR×1, R×6, MR×10)
│     └─ Score tende a ser mais alto
│
├─ Padrão (recomendado)
│  └─ Pesos: (NR×2, R×6.5, MR×9)
│     └─ Distribuição equilibrada
│
└─ Rigoroso (filtro duro)
   └─ Pesos: (NR×4, R×6, MR×8)
      └─ Score tende a ser mais baixo
```

---

## 🔧 Checklist: Validação do Sistema

```
┌─────────────────────────────────────────────┐
│ TEST CASE: Avatar (Budget Alto, Vote Alto)  │
├─────────────────────────────────────────────┤
│ Expected: Score > 8                         │
│ Actual: [?]                                 │
│ Status: ☐ PASS  ☐ FAIL                    │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│ TEST CASE: Filme ruim (Vote Baixo)          │
├─────────────────────────────────────────────┤
│ Expected: Score < 4                         │
│ Actual: [?]                                 │
│ Status: ☐ PASS  ☐ FAIL                    │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│ TEST CASE: Filme médio (Vote Médio)         │
├─────────────────────────────────────────────┤
│ Expected: 5 < Score < 7                     │
│ Actual: [?]                                 │
│ Status: ☐ PASS  ☐ FAIL                    │
└─────────────────────────────────────────────┘

┌─────────────────────────────────────────────┐
│ TEST CASE: Distribuição suave               │
├─────────────────────────────────────────────┤
│ Expected: Sem picos anormais                │
│ Actual: [?]                                 │
│ Status: ☐ PASS  ☐ FAIL                    │
└─────────────────────────────────────────────┘
```

---

## 🎓 Resumo Visual: 3 Colunas

```
┌────────────────────┬────────────────────┬────────────────────┐
│   ENTRADA (CSV)    │  PROCESSAMENTO     │   SAÍDA (Score)    │
├────────────────────┼────────────────────┼────────────────────┤
│ Budget: 237M       │ 1. Fuzzificar      │ NaoRec: 0.0        │
│ Pop: 150.5         │    (0-1 scale)     │ Rec: 0.0           │
│ Runtime: 162 min   │                    │ MuitoRec: 0.63     │
│ Vote: 7.8          │ 2. Aplicar Regras  │                    │
│                    │    (IF-THEN)       │ Score: 9.0 ✓       │
│                    │                    │                    │
│                    │ 3. Defuzzificar    │ Conclusão:         │
│                    │    (Média Pond.)   │ "Muito              │
│                    │                    │  Recomendado"      │
└────────────────────┴────────────────────┴────────────────────┘
```

---

## 📚 Documentação: Qual Arquivo Ler?

```
Iniciante?
└─ QUICK_START.md (5 min)
   └─ Depois: DOCUMENTACAO_SISTEMA_FUZZY.md (30 min)

Pronto para codificar?
└─ GUIA_PRATICO_IMPLEMENTACAO.md (copiar template)

Tem dúvida?
└─ CHECKLIST_E_FAQ.md (procurar sua pergunta)

Quer visualizar?
└─ RESUMO_VISUAL_EXEMPLOS.md (gráficos + exemplos)
```

---

## 🚀 Passo-a-Passo: Do CSV ao Score

```
Arquivo CSV (movie_dataset.csv)
         │
         ▼
┌─────────────────────────┐
│ Ler linha por linha     │
│ Extrair Budget, Pop,    │
│ Runtime, Vote           │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│ Fuzzificar cada valor   │
│ Obter graus (0-1)       │
│ Armazenar em HashMap    │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│ Aplicar 5-7 Regras IF   │
│ Calcular saídas fuzzy   │
│ (NaoRec, Rec, MuitoRec) │
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│ Defuzzificar com média  │
│ ponderada               │
│ Resultado = Score (1-10)│
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│ Output                  │
│ Título; Score; ...      │
└─────────────────────────┘
```

---

**AGORA VIRE A PAGE E COMECE!** 

Comece por: **QUICK_START.md**
