package br.com.conde.ruina;

import java.util.Locale;

/**
 * As tabelas do README. Uso:
 * {@code mvn exec:java -Dexec.args="ruina"} (ou {@code duracao}, {@code estrategias}).
 */
public final class Medidor {

    private Medidor() {
    }

    public static void main(String[] args) {
        String tabela = args.length > 0 ? args[0] : "tudo";
        boolean tudo = tabela.equals("tudo");
        if (tudo || tabela.equals("ruina")) {
            ruina();
        }
        if (tudo || tabela.equals("duracao")) {
            duracao();
        }
        if (tudo || tabela.equals("estrategias")) {
            estrategias();
        }
    }

    /** O que uma vantagem pequena da casa faz com a chance de sobreviver. */
    public static void ruina() {
        System.out.println();
        System.out.println("RUINA  (chance de dobrar o capital antes de zerar)");
        System.out.printf(Locale.ROOT, "%-10s", "capital");
        double[] chances = {0.50, 0.49, 0.48, 0.45, 0.40};
        for (double chance : chances) {
            System.out.printf(Locale.ROOT, " %10s", String.format(Locale.ROOT, "p=%.2f", chance));
        }
        System.out.println();
        for (int capital : new int[]{1, 5, 10, 20, 50, 100, 200}) {
            System.out.printf(Locale.ROOT, "%-10d", capital);
            for (double chance : chances) {
                Jogo jogo = Jogo.de(capital, 2 * capital, chance);
                System.out.printf(Locale.ROOT, " %9.2f%%", 100 * jogo.chanceDeVencer());
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("dobrar o capital num jogo justo e sempre 50%, qualquer que seja ele.");
        System.out.println("com 48% por aposta e capital de 100, a chance de dobrar cai para "
                + String.format(Locale.ROOT, "%.4f%%", 100 * Jogo.de(100, 200, 0.48).chanceDeVencer()));
        System.out.println();
    }

    /** Quanto tempo o jogo dura, e a surpresa do jogo justo. */
    public static void duracao() {
        System.out.println();
        System.out.println("DURACAO  (apostas ate alguem quebrar, no jogo justo)");
        System.out.printf(Locale.ROOT, "%-14s %12s %14s %14s%n",
                "capitais", "pela formula", "pelo sistema", "chance de vencer");
        int[][] casos = {{1, 99}, {5, 95}, {10, 90}, {25, 75}, {50, 50}, {90, 10}, {99, 1}};
        for (int[] caso : casos) {
            int total = caso[0] + caso[1];
            Jogo jogo = Jogo.de(caso[0], total, 0.5);
            Tabuleiro tabuleiro = new Tabuleiro(total, 0.5);
            System.out.printf(Locale.ROOT, "%-14s %12.1f %14.1f %13.1f%%%n",
                    caso[0] + " contra " + caso[1],
                    jogo.duracaoEsperada(), tabuleiro.duracao(caso[0]),
                    100 * jogo.chanceDeVencer());
        }
        System.out.println();
        System.out.println("no jogo desfavoravel a duracao desaba:");
        for (double chance : new double[]{0.50, 0.49, 0.45, 0.40}) {
            Jogo jogo = Jogo.de(50, 100, chance);
            System.out.printf(Locale.ROOT, "  p=%.2f: %8.1f apostas, %5.2f%% de chance%n",
                    chance, jogo.duracaoEsperada(), 100 * jogo.chanceDeVencer());
        }
        System.out.println();
    }

    /** Timida contra ousada: quando apostar tudo e melhor. */
    public static void estrategias() {
        System.out.println();
        System.out.println("ESTRATEGIAS  (capital 1 de meta 8: chance de chegar a meta)");
        System.out.printf(Locale.ROOT, "%-10s %14s %14s %12s%n",
                "p", "timida", "ousada", "diferenca");
        for (double chance : new double[]{0.60, 0.55, 0.50, 0.45, 0.40, 0.30}) {
            double timida = Jogo.de(1, 8, chance).chanceDeVencer();
            double ousada = new Ousada(8, chance).vitoria(1);
            System.out.printf(Locale.ROOT, "%-10.2f %13.4f%% %13.4f%% %11.4f%%%n",
                    chance, 100 * timida, 100 * ousada, 100 * (ousada - timida));
        }
        System.out.println();
        System.out.println("com capital 32 de meta 64, a mesma pergunta:");
        System.out.printf(Locale.ROOT, "%-10s %14s %14s %14s %14s%n",
                "p", "timida", "ousada", "apostas timida", "apostas ousada");
        for (double chance : new double[]{0.50, 0.49, 0.45, 0.40}) {
            double timida = Jogo.de(32, 64, chance).chanceDeVencer();
            Ousada ousada = new Ousada(64, chance);
            System.out.printf(Locale.ROOT, "%-10.2f %13.4f%% %13.4f%% %14.1f %14.1f%n",
                    chance, 100 * timida, 100 * ousada.vitoria(32),
                    Jogo.de(32, 64, chance).duracaoEsperada(), ousada.duracao(32));
        }
        System.out.println();
    }
}
