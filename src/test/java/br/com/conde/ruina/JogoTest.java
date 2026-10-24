package br.com.conde.ruina;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

/**
 * As formulas fechadas sao curtas e faceis de errar num expoente. Por isso elas
 * sao conferidas contra o sistema de equacoes, que nao sabe nada de formula.
 */
@DisplayName("A conta fechada contra o sistema")
class JogoTest {

    @Test
    @DisplayName("a chance de vencer bate com o sistema, em todo capital e toda vantagem")
    void aChanceBate() {
        for (double chance : new double[]{0.3, 0.45, 0.49, 0.5, 0.51, 0.55, 0.7}) {
            Tabuleiro tabuleiro = new Tabuleiro(40, chance);

            for (int capital = 0; capital <= 40; capital++) {
                assertThat(Jogo.de(capital, 40, chance).chanceDeVencer())
                        .describedAs("p=%.2f, capital %d", chance, capital)
                        .isCloseTo(tabuleiro.vitoria(capital), within(1e-9));
            }
        }
    }

    @Test
    @DisplayName("a duracao bate com o sistema, em todo capital e toda vantagem")
    void aDuracaoBate() {
        for (double chance : new double[]{0.35, 0.45, 0.5, 0.55, 0.65}) {
            Tabuleiro tabuleiro = new Tabuleiro(30, chance);

            for (int capital = 0; capital <= 30; capital++) {
                assertThat(Jogo.de(capital, 30, chance).duracaoEsperada())
                        .describedAs("p=%.2f, capital %d", chance, capital)
                        .isCloseTo(tabuleiro.duracao(capital), within(1e-6));
            }
        }
    }

    @Test
    @DisplayName("no jogo justo a chance e a fracao do capital, e nada mais")
    void noJogoJustoEhAFracao() {
        for (int capital = 0; capital <= 100; capital += 5) {
            assertThat(Jogo.de(capital, 100, 0.5).chanceDeVencer())
                    .isCloseTo(capital / 100.0, within(1e-12));
        }
    }

    @Test
    @DisplayName("no jogo justo a duracao e o produto dos dois capitais")
    void noJogoJustoADuracaoEhOProduto() {
        for (int capital = 1; capital < 100; capital++) {
            assertThat(Jogo.de(capital, 100, 0.5).duracaoEsperada())
                    .describedAs("capital %d", capital)
                    .isCloseTo((double) capital * (100 - capital), within(1e-9));
        }
    }

    @Test
    @DisplayName("sem capital a ruina e certa; na meta a vitoria ja aconteceu")
    void asBordas() {
        for (double chance : new double[]{0.3, 0.5, 0.7}) {
            assertThat(Jogo.de(0, 10, chance).chanceDeVencer()).isZero();
            assertThat(Jogo.de(10, 10, chance).chanceDeVencer()).isEqualTo(1.0);
            assertThat(Jogo.de(0, 10, chance).duracaoEsperada()).isZero();
        }
    }

    @Test
    @DisplayName("a chance de ruina e o complemento da de vencer")
    void ruinaEhOComplemento() {
        for (int capital = 0; capital <= 20; capital++) {
            Jogo jogo = Jogo.de(capital, 20, 0.47);

            assertThat(jogo.chanceDeRuina() + jogo.chanceDeVencer())
                    .isCloseTo(1.0, within(1e-12));
        }
    }

    @Test
    @DisplayName("meta menor que dois ou capital fora da faixa nao e jogo")
    void entradasInvalidas() {
        assertThatThrownBy(() -> Jogo.de(0, 1, 0.5)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Jogo.de(11, 10, 0.5)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Jogo.de(5, 10, 1.5)).isInstanceOf(IllegalArgumentException.class);
    }
}
