package br.com.conde.ruina;

/**
 * A estrategia ousada: aposte sempre o maximo que faz sentido, que e o menor
 * entre o que voce tem e o que falta para a meta.
 *
 * <p>Com essa regra o capital nunca volta a um valor ja visitado — ele dobra ou
 * zera, ou vai direto para a meta —, entao nao ha sistema de equacoes para
 * resolver: e uma recursao que termina. A memoria guarda cada capital uma vez.
 *
 * <p>O resultado dela contra a estrategia timida e o assunto do repositorio.
 */
public final class Ousada {

    private final int meta;
    private final double chance;
    private final Double[] guardado;

    public Ousada(int meta, double chance) {
        if (meta < 2) {
            throw new IllegalArgumentException("meta precisa ser de pelo menos 2");
        }
        this.meta = meta;
        this.chance = chance;
        this.guardado = new Double[meta + 1];
    }

    /** Quanto se aposta com este capital. */
    public int aposta(int capital) {
        return Math.min(capital, meta - capital);
    }

    /** A chance de chegar a meta jogando ousado a partir deste capital. */
    public double vitoria(int capital) {
        if (capital <= 0) {
            return 0;
        }
        if (capital >= meta) {
            return 1;
        }
        if (guardado[capital] != null) {
            return guardado[capital];
        }
        int aposta = aposta(capital);
        guardado[capital] = 0.0;                 // nao ha ciclo: a aposta sempre sai daqui
        double valor = chance * vitoria(capital + aposta)
                + (1 - chance) * vitoria(capital - aposta);
        guardado[capital] = valor;
        return valor;
    }

    /** Quantas apostas o jogo ousado dura, em media. */
    public double duracao(int capital) {
        if (capital <= 0 || capital >= meta) {
            return 0;
        }
        int aposta = aposta(capital);
        return 1 + chance * duracao(capital + aposta)
                + (1 - chance) * duracao(capital - aposta);
    }
}
