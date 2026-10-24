package br.com.conde.ruina;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/** Os numeros publicados no README, presos em teste. */
@DisplayName("Os numeros do README")
class MedidasTest {

    private double pct(int capital, double chance) {
        return Math.round(10000 * Jogo.de(capital, 2 * capital, chance).chanceDeVencer()) / 100.0;
    }

    @Test
    @DisplayName("tabela da ruina: dobrar o capital no jogo justo e sempre 50%")
    void noJustoEhSempreMetade() {
        for (int capital : new int[]{1, 5, 10, 20, 50, 100, 200}) {
            assertThat(pct(capital, 0.5)).describedAs("capital %d", capital).isEqualTo(50.0);
        }
    }

    @Test
    @DisplayName("tabela da ruina: a coluna de 49% por aposta")
    void aColunaDeQuarentaENove() {
        assertThat(pct(1, 0.49)).isEqualTo(49.0);
        assertThat(pct(10, 0.49)).isEqualTo(40.13);
        assertThat(pct(50, 0.49)).isEqualTo(11.92);
        assertThat(pct(100, 0.49)).isEqualTo(1.80);
        assertThat(pct(200, 0.49)).isEqualTo(0.03);
    }

    @Test
    @DisplayName("um por cento de desvantagem derruba a chance de 50% para 1,8%")
    void oPrecoDeUmPorCento() {
        assertThat(pct(100, 0.50)).isEqualTo(50.0);
        assertThat(pct(100, 0.49)).isEqualTo(1.80);
        assertThat(pct(100, 0.48)).isEqualTo(0.03);
    }

    @Test
    @DisplayName("tabela da duracao: o produto dos capitais, no jogo justo")
    void tabelaDaDuracao() {
        assertThat(Jogo.de(1, 100, 0.5).duracaoEsperada()).isCloseTo(99.0, within(1e-9));
        assertThat(Jogo.de(10, 100, 0.5).duracaoEsperada()).isCloseTo(900.0, within(1e-9));
        assertThat(Jogo.de(50, 100, 0.5).duracaoEsperada()).isCloseTo(2500.0, within(1e-9));
        assertThat(Jogo.de(99, 100, 0.5).duracaoEsperada()).isCloseTo(99.0, within(1e-9));
    }

    @Test
    @DisplayName("o jogo equilibrado dura muito mais que o desequilibrado")
    void oEquilibrioAlonga() {
        assertThat(Jogo.de(50, 100, 0.5).duracaoEsperada())
                .isGreaterThan(25 * Jogo.de(1, 100, 0.5).duracaoEsperada());
    }

    @Test
    @DisplayName("tabela das estrategias: capital 1 de meta 8")
    void tabelaDasEstrategias() {
        assertThat(100 * Jogo.de(1, 8, 0.5).chanceDeVencer()).isCloseTo(12.5, within(0.001));
        assertThat(100 * new Ousada(8, 0.5).vitoria(1)).isCloseTo(12.5, within(0.001));
        assertThat(100 * Jogo.de(1, 8, 0.40).chanceDeVencer()).isCloseTo(2.0301, within(0.001));
        assertThat(100 * new Ousada(8, 0.40).vitoria(1)).isCloseTo(6.4, within(0.001));
        assertThat(100 * Jogo.de(1, 8, 0.60).chanceDeVencer()).isCloseTo(34.6868, within(0.001));
        assertThat(100 * new Ousada(8, 0.60).vitoria(1)).isCloseTo(21.6, within(0.001));
    }

    @Test
    @DisplayName("com 45% por aposta, a ousada e 277 vezes melhor que a timida")
    void aDiferencaNoDesfavoravel() {
        double timida = Jogo.de(32, 64, 0.45).chanceDeVencer();
        double ousada = new Ousada(64, 0.45).vitoria(32);

        assertThat(100 * timida).isCloseTo(0.1624, within(0.001));
        assertThat(100 * ousada).isCloseTo(45.0, within(0.001));
        assertThat(ousada / timida).isCloseTo(277.0, within(1.0));
    }

    @Test
    @DisplayName("o medidor roda inteiro sem lancar")
    void medidorRoda() {
        Medidor.ruina();
        Medidor.duracao();
        Medidor.estrategias();
    }
}
