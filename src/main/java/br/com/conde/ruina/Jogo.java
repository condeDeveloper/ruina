package br.com.conde.ruina;

/**
 * Um jogo de apostas repetidas: comeco com {@code capital}, quero chegar a
 * {@code meta}, e cada aposta ganha com probabilidade {@code chance}.
 *
 * <p>Ganhar leva o capital para cima pelo valor apostado; perder leva para
 * baixo pelo mesmo valor. O jogo acaba quando o capital zera (ruina) ou atinge a
 * meta. Nao ha empate e nao ha credito.
 */
public record Jogo(int capital, int meta, double chance) {

    public Jogo {
        if (meta < 2) {
            throw new IllegalArgumentException("meta precisa ser de pelo menos 2");
        }
        if (capital < 0 || capital > meta) {
            throw new IllegalArgumentException("capital " + capital + " fora de 0.." + meta);
        }
        if (chance < 0 || chance > 1) {
            throw new IllegalArgumentException("chance fora de 0..1");
        }
    }

    public static Jogo de(int capital, int meta, double chance) {
        return new Jogo(capital, meta, chance);
    }

    /** O mesmo jogo visto de outro capital. */
    public Jogo com(int outroCapital) {
        return new Jogo(outroCapital, meta, chance);
    }

    /** Quanto falta para a meta, que e o capital do adversario. */
    public int doAdversario() {
        return meta - capital;
    }

    public boolean justo() {
        return Math.abs(chance - 0.5) < 1e-12;
    }

    /**
     * A chance de chegar a meta apostando uma unidade por vez, pela conta fechada.
     *
     * <p>No jogo justo e a fracao do capital sobre a meta, e so. Com vantagem para
     * um lado, entra a razao entre perder e ganhar elevada ao capital, que e o que
     * faz uma vantagem pequena virar uma diferenca enorme.
     */
    public double chanceDeVencer() {
        if (capital == 0) {
            return 0;
        }
        if (capital == meta) {
            return 1;
        }
        if (justo()) {
            return (double) capital / meta;
        }
        double razao = (1 - chance) / chance;
        return (1 - Math.pow(razao, capital)) / (1 - Math.pow(razao, meta));
    }

    public double chanceDeRuina() {
        return 1 - chanceDeVencer();
    }

    /**
     * Quantas apostas o jogo dura, em media, com aposta de uma unidade.
     *
     * <p>No jogo justo sai um produto exato: o capital de um vezes o do outro.
     */
    public double duracaoEsperada() {
        if (capital == 0 || capital == meta) {
            return 0;
        }
        if (justo()) {
            return (double) capital * doAdversario();
        }
        // com vantagem para um lado a duracao e (capital - meta * chance de vencer)
        // dividido pela diferenca entre perder e ganhar
        return (capital - meta * chanceDeVencer()) / (1 - 2 * chance);
    }
}
