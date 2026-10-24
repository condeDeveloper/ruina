package br.com.conde.ruina;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * O achado central: no jogo justo a estrategia nao muda nada, e nos outros dois
 * ela muda tudo, para lados opostos.
 */
@DisplayName("Timida contra ousada")
class EstrategiaTest {

    private double timida(int capital, int meta, double chance) {
        return Jogo.de(capital, meta, chance).chanceDeVencer();
    }

    private double ousada(int capital, int meta, double chance) {
        return new Ousada(meta, chance).vitoria(capital);
    }

    @Test
    @DisplayName("no jogo justo as duas dao exatamente a mesma chance")
    void noJogoJustoDaNoMesmo() {
        for (int meta : new int[]{4, 8, 16, 32, 64}) {
            for (int capital = 1; capital < meta; capital++) {
                assertThat(ousada(capital, meta, 0.5))
                        .describedAs("meta %d, capital %d", meta, capital)
                        .isCloseTo(timida(capital, meta, 0.5), within(1e-12));
            }
        }
    }

    @Test
    @DisplayName("no jogo desfavoravel a ousada ganha, e por muito")
    void noDesfavoravelAOusadaGanha() {
        for (double chance : new double[]{0.49, 0.45, 0.40, 0.30}) {
            assertThat(ousada(1, 8, chance))
                    .describedAs("p=%.2f", chance)
                    .isGreaterThan(timida(1, 8, chance));
        }

        assertThat(ousada(32, 64, 0.45) / timida(32, 64, 0.45)).isGreaterThan(200);
    }

    @Test
    @DisplayName("no jogo favoravel e o contrario: apostar pouco e melhor")
    void noFavoravelATimidaGanha() {
        for (double chance : new double[]{0.55, 0.60, 0.70}) {
            assertThat(timida(1, 8, chance))
                    .describedAs("p=%.2f", chance)
                    .isGreaterThan(ousada(1, 8, chance));
        }
    }

    @Test
    @DisplayName("apostando tudo na metade do caminho, a chance e a da propria aposta")
    void metadeDoCaminhoEhUmaApostaSo() {
        for (double chance : new double[]{0.3, 0.45, 0.5, 0.6}) {
            assertThat(ousada(32, 64, chance)).isCloseTo(chance, within(1e-12));
            assertThat(new Ousada(64, chance).duracao(32)).isCloseTo(1.0, within(1e-12));
        }
    }

    @Test
    @DisplayName("a ousada aposta o menor entre o que tem e o que falta")
    void quantoElaAposta() {
        Ousada ousada = new Ousada(10, 0.5);

        assertThat(ousada.aposta(1)).isEqualTo(1);
        assertThat(ousada.aposta(4)).isEqualTo(4);
        assertThat(ousada.aposta(5)).isEqualTo(5);
        assertThat(ousada.aposta(6)).isEqualTo(4);
        assertThat(ousada.aposta(9)).isEqualTo(1);
    }

    @Test
    @DisplayName("a ousada termina muito mais rapido, e isso nao e detalhe")
    void aOusadaEhCurta() {
        for (double chance : new double[]{0.5, 0.45}) {
            double apostasTimida = Jogo.de(32, 64, chance).duracaoEsperada();
            double apostasOusada = new Ousada(64, chance).duracao(32);

            assertThat(apostasOusada).isLessThan(apostasTimida / 100);
        }
    }

    @Test
    @DisplayName("quanto menor a vantagem da casa, menor a diferenca entre as estrategias")
    void aDiferencaEncolhePertoDoJusto() {
        double longe = ousada(1, 8, 0.40) - timida(1, 8, 0.40);
        double perto = ousada(1, 8, 0.49) - timida(1, 8, 0.49);

        assertThat(perto).isPositive().isLessThan(longe);
    }
}
