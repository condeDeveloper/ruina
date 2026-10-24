package br.com.conde.ruina;

/**
 * O juiz: resolve o jogo pelo sistema de equacoes, sem usar formula nenhuma.
 *
 * <p>A chance de vencer partindo de cada capital satisfaz
 * {@code v[i] = p*v[i+1] + q*v[i-1]}, com {@code v[0] = 0} e {@code v[N] = 1}.
 * Sao N-1 equacoes com N-1 incognitas, e a matriz e tridiagonal: da para
 * resolver em tempo linear pelo algoritmo de Thomas, exatamente, sem iterar ate
 * convergir.
 *
 * <p>Isso existe para conferir as formulas fechadas de {@link Jogo}. Elas sao
 * curtas e faceis de errar num expoente; o sistema nao sabe nada de formula.
 */
public final class Tabuleiro {

    private final int meta;
    private final double chance;
    private final double[] vitoria;
    private final double[] duracao;

    public Tabuleiro(int meta, double chance) {
        if (meta < 2) {
            throw new IllegalArgumentException("meta precisa ser de pelo menos 2");
        }
        this.meta = meta;
        this.chance = chance;
        this.vitoria = resolver(new double[meta + 1], true);
        this.duracao = resolver(new double[meta + 1], false);
    }

    /** A chance de chegar a meta partindo deste capital. */
    public double vitoria(int capital) {
        return vitoria[capital];
    }

    /** As apostas que o jogo dura, em media, partindo deste capital. */
    public double duracao(int capital) {
        return duracao[capital];
    }

    /**
     * Thomas: varre uma vez para frente eliminando a subdiagonal e uma vez para
     * tras substituindo. Para a vitoria o lado direito e zero e as bordas valem 0
     * e 1; para a duracao o lado direito e -1 e as bordas valem 0.
     */
    private double[] resolver(double[] saida, boolean paraVitoria) {
        int n = meta - 1;
        if (n == 0) {
            return saida;
        }
        double p = chance;
        double q = 1 - chance;
        double[] c = new double[n];
        double[] d = new double[n];

        for (int i = 0; i < n; i++) {
            double a = -q;
            double b = 1.0;
            double cc = -p;
            double dd = paraVitoria ? 0 : 1;
            if (i == n - 1 && paraVitoria) {
                dd += p;                       // v[N] = 1 entra no lado direito
            }
            if (i == 0) {
                c[i] = cc / b;
                d[i] = dd / b;
            } else {
                double denominador = b - a * c[i - 1];
                c[i] = cc / denominador;
                d[i] = (dd - a * d[i - 1]) / denominador;
            }
        }

        double[] x = new double[n];
        x[n - 1] = d[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            x[i] = d[i] - c[i] * x[i + 1];
        }

        System.arraycopy(x, 0, saida, 1, n);
        if (paraVitoria) {
            saida[meta] = 1;
        }
        return saida;
    }
}
