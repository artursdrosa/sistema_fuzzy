import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FuzzyMovies {
    public static void main(String[] args) {
        // ========== DEFINIR VARIÁVEIS FUZZY ==========

        // 1. BUDGET (em milhões de dólares, dividido por 1.000.000 na leitura)
        GrupoVariaveis grupoBudget = new GrupoVariaveis();
        grupoBudget.add(new VariavelFuzzy("Budget_Baixo", 0, 0, 1, 5));
        grupoBudget.add(new VariavelFuzzy("Budget_MedioBaixo", 1, 5, 20, 40));
        grupoBudget.add(new VariavelFuzzy("Budget_Medio", 20, 40, 60, 100));
        grupoBudget.add(new VariavelFuzzy("Budget_MedioAlto", 60, 100, 150, 200));
        grupoBudget.add(new VariavelFuzzy("Budget_Alto", 150, 200, 300, 380));

        // 2. POPULARITY (escala 0-200 aproximadamente)
        GrupoVariaveis grupoPopularidade = new GrupoVariaveis();
        grupoPopularidade.add(new VariavelFuzzy("Pop_Baixa", 0, 0, 5, 15));
        grupoPopularidade.add(new VariavelFuzzy("Pop_Media", 8, 15, 30, 50));
        grupoPopularidade.add(new VariavelFuzzy("Pop_Alta", 35, 50, 75, 100));
        grupoPopularidade.add(new VariavelFuzzy("Pop_MuitoAlta", 80, 100, 870, 870));

        // 3. RUNTIME (em minutos)
        GrupoVariaveis grupoRuntime = new GrupoVariaveis();
        grupoRuntime.add(new VariavelFuzzy("Runtime_Curto", 0, 0, 60, 90));
        grupoRuntime.add(new VariavelFuzzy("Runtime_Normal", 80, 100, 120, 140));
        grupoRuntime.add(new VariavelFuzzy("Runtime_Longo", 130, 150, 200, 350));

        // 4. VOTE_AVERAGE (escala 0-10)
        GrupoVariaveis grupoVoto = new GrupoVariaveis();
        grupoVoto.add(new VariavelFuzzy("Voto_Baixo", 0, 0, 3, 5));
        grupoVoto.add(new VariavelFuzzy("Voto_Medio", 4, 5, 6.5f, 7.5f));
        grupoVoto.add(new VariavelFuzzy("Voto_Alto", 6, 7, 8, 9));
        grupoVoto.add(new VariavelFuzzy("Voto_MuitoAlto", 7.5f, 8.5f, 10, 10));

        // 5. GENRE (NOTAS ATRIBUÍDAS MANUALMENTE)
        GrupoVariaveis grupoNotasGenero = new GrupoVariaveis();
        grupoNotasGenero.add(new VariavelFuzzy("Genero_Ruim", 0, 0, 2, 5));
        grupoNotasGenero.add(new VariavelFuzzy("Genero_Bom", 4.5f, 5.5f, 6, 7.5f));
        grupoNotasGenero.add(new VariavelFuzzy("Genero_Muito_Bom", 7, 8.5f, 10, 10));

        // 6. SAÍDA FUZZY (Recomendação)
        GrupoVariaveis grupoRecomendacao = new GrupoVariaveis();
        grupoRecomendacao.add(new VariavelFuzzy("NaoRecomendado", 0, 0, 2, 5));
        grupoRecomendacao.add(new VariavelFuzzy("Recomendado", 4.5f, 5.5f, 6, 7.5f));
        grupoRecomendacao.add(new VariavelFuzzy("MuitoRecomendado", 7, 8.5f, 10, 10));

        // ========== LER CSV ==========
        try {
            BufferedReader bfr = new BufferedReader(
                    new FileReader(new File("movie_dataset_generos_tratados.csv"))
            );

            String header = bfr.readLine();
            System.out.println("Processando filmes...\n");
            System.out.println(header);

            String line = "";
            int linhaNum = 0;

            GenerosNota generosnotas = new GenerosNota();
            List<FilmeResultado> ranking = new ArrayList<>();


            while ((line = bfr.readLine()) != null /* && linhaNum < 1000*/) {

                try {
                    String[] spl = line.split(";"); // CSV com quotes

                    // Validar se tem dados mínimos
                    if (spl.length < 21) continue;

                    String titulo = spl[18].trim();  // Title

                    // ========== TRATAMENTO DE DADOS ==========

                    // Budget (índice 1)
                    float budget = 0;
                    try {
                        String budgetStr = spl[1].trim();
                        if (!budgetStr.isEmpty() && !budgetStr.equals("null")) {
                            budget = Float.parseFloat(budgetStr) / 1000000; // Converter para milhões
                        }
                    } catch (NumberFormatException e) {
                        budget = 0;
                    }

                    // Popularity (índice 9)
                    float popularity = 0;
                    try {
                        String popularityStr = spl[9].trim();
                        if (!popularityStr.isEmpty() && !popularityStr.equals("null")) {
                            popularity = (Float.parseFloat(popularityStr.replace(".", ""))) / 1000000; // Converter para milhões
                        }
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

                    float notaGenero = 0;
                    String generoStr = "";
                    try {
                        generoStr = spl[2].trim();
                        if (!generoStr.isEmpty() && !generoStr.equals("null")) {
                            notaGenero = generosnotas.notasGenero.get(generoStr.toLowerCase().trim());
                        }
                    } catch (Exception e) {
                        notaGenero = 0;
                    }

                    // Pular filmes com dados insuficientes
                    if (budget <= 1 || voteAvg <= 0 || popularity > 900 || popularity < 1)
                        continue;

                    // ========== FUZZIFICAÇÃO ==========
                    HashMap<String, Float> variaveis = new HashMap<>();

                    grupoBudget.fuzzifica(budget, variaveis);
                    grupoPopularidade.fuzzifica(popularity, variaveis);
                    grupoRuntime.fuzzifica(runtime, variaveis);
                    grupoVoto.fuzzifica(voteAvg, variaveis);
                    grupoNotasGenero.fuzzifica(notaGenero, variaveis);

                    // ========== APLICAR REGRAS FUZZY ==========

                    // ══════════════════════════════════════════════
                    // MUITO RECOMENDADO
                    // ══════════════════════════════════════════════

                    // Voto muito alto recomenda com qualquer budget
                    rodaRegraE(variaveis, "Voto_MuitoAlto", "Budget_Baixo", "MuitoRecomendado");
                    rodaRegraE(variaveis, "Voto_MuitoAlto", "Budget_MedioBaixo", "MuitoRecomendado");
                    rodaRegraE(variaveis, "Voto_MuitoAlto", "Budget_Medio", "MuitoRecomendado");
                    rodaRegraE(variaveis, "Voto_MuitoAlto", "Budget_MedioAlto", "MuitoRecomendado");
                    rodaRegraE(variaveis, "Voto_MuitoAlto", "Budget_Alto", "MuitoRecomendado");

                    // Voto alto com budget médio pra cima
                    rodaRegraE(variaveis, "Voto_Alto", "Budget_Medio", "MuitoRecomendado");
                    rodaRegraE(variaveis, "Voto_Alto", "Budget_MedioAlto", "MuitoRecomendado");
                    rodaRegraE(variaveis, "Voto_Alto", "Budget_Alto", "MuitoRecomendado");

                    // Voto muito alto com popularidade alta
                    rodaRegraE(variaveis, "Voto_MuitoAlto", "Pop_Alta", "MuitoRecomendado");
                    rodaRegraE(variaveis, "Voto_MuitoAlto", "Pop_MuitoAlta", "MuitoRecomendado");

                    // Gênero muito bom eleva filmes com voto alto ou muito alto
                    rodaRegraE(variaveis, "Voto_MuitoAlto", "Genero_Muito_Bom", "MuitoRecomendado");
                    rodaRegraE(variaveis, "Voto_Alto", "Genero_Muito_Bom", "MuitoRecomendado");

                    // Gênero muito bom com popularidade alta também eleva
                    rodaRegraE(variaveis, "Genero_Muito_Bom", "Pop_Alta", "MuitoRecomendado");
                    rodaRegraE(variaveis, "Genero_Muito_Bom", "Pop_MuitoAlta", "MuitoRecomendado");

                    // ══════════════════════════════════════════════
                    // RECOMENDADO
                    // ══════════════════════════════════════════════

                    // Voto alto com budget baixo (sem compensação de gênero)
                    rodaRegraE(variaveis, "Voto_Alto", "Budget_Baixo", "Recomendado");
                    rodaRegraE(variaveis, "Voto_Alto", "Budget_MedioBaixo", "Recomendado");

                    // Voto médio resgatado por popularidade alta
                    rodaRegraE(variaveis, "Voto_Medio", "Pop_Alta", "Recomendado");
                    rodaRegraE(variaveis, "Voto_Medio", "Pop_MuitoAlta", "Recomendado");

                    // Voto médio com budget equilibrado
                    rodaRegraE(variaveis, "Voto_Medio", "Budget_MedioBaixo", "Recomendado");
                    rodaRegraE(variaveis, "Voto_Medio", "Budget_Medio", "Recomendado");

                    // Runtime normal ajuda voto médio
                    rodaRegraE(variaveis, "Voto_Medio", "Runtime_Normal", "Recomendado");

                    // Gênero bom com voto médio ou alto salva o filme
                    rodaRegraE(variaveis, "Voto_Medio", "Genero_Muito_Bom", "Recomendado");
                    rodaRegraE(variaveis, "Voto_Alto", "Genero_Bom", "Recomendado");

                    // Gênero bom com popularidade média já é suficiente para recomendar
                    rodaRegraE(variaveis, "Genero_Muito_Bom", "Pop_Media", "Recomendado");
                    rodaRegraE(variaveis, "Genero_Bom", "Pop_Alta", "Recomendado");

                    // ══════════════════════════════════════════════
                    // NÃO RECOMENDADO
                    // ══════════════════════════════════════════════

                    // Voto baixo com qualquer budget
                    rodaRegraE(variaveis, "Voto_Baixo", "Budget_Baixo", "NaoRecomendado");
                    rodaRegraE(variaveis, "Voto_Baixo", "Budget_MedioBaixo", "NaoRecomendado");
                    rodaRegraE(variaveis, "Voto_Baixo", "Budget_Medio", "NaoRecomendado");
                    rodaRegraE(variaveis, "Voto_Baixo", "Budget_MedioAlto", "NaoRecomendado");
                    rodaRegraE(variaveis, "Voto_Baixo", "Budget_Alto", "NaoRecomendado");

                    // Voto baixo com popularidade baixa/média
                    rodaRegraE(variaveis, "Voto_Baixo", "Pop_Baixa", "NaoRecomendado");
                    rodaRegraE(variaveis, "Voto_Baixo", "Pop_Media", "NaoRecomendado");

                    // Voto médio com popularidade e budget ruins
                    rodaRegraE(variaveis, "Voto_Medio", "Pop_Baixa", "NaoRecomendado");
                    rodaRegraE(variaveis, "Voto_Medio", "Budget_Baixo", "NaoRecomendado");

                    // Voto médio com runtime inadequado
                    rodaRegraE(variaveis, "Voto_Medio", "Runtime_Curto", "NaoRecomendado");
                    rodaRegraE(variaveis, "Voto_Medio", "Runtime_Longo", "NaoRecomendado");

                    // Gênero ruim derruba filmes independente do voto
                    rodaRegraE(variaveis, "Voto_Baixo", "Genero_Ruim", "NaoRecomendado");
                    rodaRegraE(variaveis, "Voto_Medio", "Genero_Ruim", "NaoRecomendado");

                    // Gênero ruim com popularidade baixa não tem salvação
                    rodaRegraE(variaveis, "Genero_Ruim", "Pop_Baixa", "NaoRecomendado");
                    rodaRegraE(variaveis, "Genero_Ruim", "Pop_Media", "NaoRecomendado");
                    // ========== DEFUZZIFICAÇÃO ==========

                    float naoRec = variaveis.get("NaoRecomendado");
                    float rec = variaveis.get("Recomendado");
                    float muitoRec = variaveis.get("MuitoRecomendado");

                    float score = 0;
                    if ((naoRec + rec + muitoRec) > 0) {
                        score = (naoRec + rec * 5.75f + muitoRec * 9.5f)
                                / (naoRec + rec + muitoRec);
                    }

                    linhaNum++;

                    ranking.add(new FilmeResultado(
                            titulo, budget, popularity, (int) runtime, voteAvg, generoStr,
                            naoRec, rec, muitoRec, score
                    ));

                    // ========== OUTPUT ==========
                    System.out.println(
                            "\n" + linhaNum + " - Título: " + titulo +
                                    "\nBudget: " + ("" + budget).replace(".", ",") + " Milhões" +
                                    "\nPopularidade: " + ("" + popularity).replace(".", ",") +
                                    "\nDuração do filme: " + (int) runtime + " minutos" +
                                    "\nNota média: " + ("" + voteAvg).replace(".", ",") +
                                    "\nGênero: " + generoStr +
                                    "\nPertencimento a Não Recomendado: " + ("" + naoRec).replace(".", ",") +
                                    "\nPertencimento a Recomendado: " + ("" + rec).replace(".", ",") +
                                    "\nPertencimento a Muito Recomendado: " + ("" + muitoRec).replace(".", ",") +
                                    "\nScore Final: " + ("" + score).replace(".", ",")
                    );
                } catch (Exception e) {
                    continue;
                }
            }
            ranking.sort((a, b) -> Float.compare(b.score, a.score));

            System.out.println("\n========================== TOP 20 FILMES ==========================");
            for (int i = 0; i < 20; i++) {
                FilmeResultado f = ranking.get(i);
                System.out.println(
                        "\n" + (i + 1) + " - Título: " + f.titulo +
                                "\nBudget: " + ("" + f.budget).replace(".", ",") + " Milhões" +
                                "\nPopularidade: " + ("" + f.popularity).replace(".", ",") +
                                "\nDuração: " + f.runtime + " minutos" +
                                "\nNota média: " + ("" + f.voteAvg).replace(".", ",") +
                                "\nGênero: " + f.genero +
                                "\nNão Recomendado: " + ("" + f.naoRec).replace(".", ",") +
                                "\nRecomendado: " + ("" + f.rec).replace(".", ",") +
                                "\nMuito Recomendado: " + ("" + f.muitoRec).replace(".", ",") +
                                "\nScore Final: " + ("" + f.score).replace(".", ",")
                );

                bfr.close();
            }
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
}

//    // Operador OR (máximo) - se precisar
//    private static void rodaRegraOU(HashMap<String, Float> vars,
//                                    String var1, String var2, String resultado) {
//        if (!vars.containsKey(var1) || !vars.containsKey(var2)) {
//            return;
//        }
//
//        float v = Math.max(vars.get(var1), vars.get(var2));
//        float vAtual = vars.getOrDefault(resultado, 0f);
//        vars.put(resultado, Math.max(vAtual, v));
//    }
//}