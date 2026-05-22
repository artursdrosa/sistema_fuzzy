public class FilmeResultado {
    String titulo;
    float budget;
    float popularity;
    int runtime;
    float voteAvg;
    String genero;
    float naoRec;
    float rec;
    float muitoRec;
    float score;

    public FilmeResultado(String titulo, float budget, float popularity,
                          int runtime, float voteAvg, String genero,
                          float naoRec, float rec, float muitoRec, float score) {
        this.titulo = titulo;
        this.budget = budget;
        this.popularity = popularity;
        this.runtime = runtime;
        this.voteAvg = voteAvg;
        this.genero = genero;
        this.naoRec = naoRec;
        this.rec = rec;
        this.muitoRec = muitoRec;
        this.score = score;
    }
}