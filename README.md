# ruina

A ruína do jogador do zero em Java 21: começo com um capital, quero dobrá-lo
antes de zerar, e cada aposta tem uma probabilidade fixa de ganhar.

As fórmulas fechadas deste problema são curtas e fáceis de errar num expoente.
Por isso nenhuma delas é acreditada: o mesmo jogo é resolvido também pelo
**sistema de equações** (uma incógnita por capital, matriz tridiagonal), que não
sabe nada de fórmula, e os dois precisam dar o mesmo número.

```
mvn -B -ntp verify
mvn -B -ntp exec:java -Dexec.args="ruina"   # (ou duracao, estrategias)
```

## Um por cento de desvantagem

Chance de dobrar o capital antes de zerar, apostando uma unidade por vez:

| capital | p=0,50 | p=0,49 | p=0,48 | p=0,45 | p=0,40 |
|---:|---:|---:|---:|---:|---:|
| 1 | 50,00% | 49,00% | 48,00% | 45,00% | 40,00% |
| 5 | 50,00% | 45,02% | 40,13% | 26,83% | 11,64% |
| 10 | 50,00% | 40,13% | 30,99% | 11,85% | 1,70% |
| 20 | 50,00% | 31,00% | 16,79% | 1,78% | 0,03% |
| 50 | 50,00% | 11,92% | 1,79% | 0,00% | 0,00% |
| **100** | **50,00%** | **1,80%** | 0,03% | 0,00% | 0,00% |
| 200 | 50,00% | 0,03% | 0,00% | 0,00% | 0,00% |

Com o jogo justo, dobrar o capital é sempre 50%, qualquer que seja ele. Com
**1% de desvantagem por aposta** e capital de 100, a chance cai de 50% para
**1,80%** — e com 200, para 0,03%.

É a mesma desvantagem em cada aposta. O que muda é quantas apostas são
necessárias: quanto maior o capital, mais rodadas até decidir, e mais vezes a
vantagem minúscula da casa é cobrada. Jogar mais tempo com desvantagem não é
"mais chance de recuperar": é mais chance de perder.

## Quanto tempo o jogo dura

Jogo justo, 100 fichas na mesa:

| capitais | duração | pelo sistema | chance de vencer |
|---|---:|---:|---:|
| 1 contra 99 | 99,0 | 99,0 | 1,0% |
| 10 contra 90 | 900,0 | 900,0 | 10,0% |
| 25 contra 75 | 1.875,0 | 1.875,0 | 25,0% |
| **50 contra 50** | **2.500,0** | 2.500,0 | 50,0% |
| 90 contra 10 | 900,0 | 900,0 | 90,0% |

No jogo justo a duração é exatamente **o produto dos dois capitais**. Um jogo
equilibrado (50 contra 50) dura 25 vezes mais que um desequilibrado (1 contra
99), e as duas pontas da tabela são simétricas: 1 contra 99 e 99 contra 1 duram
as mesmas 99 apostas.

E com desvantagem a duração desaba junto com a chance:

| p | apostas | chance |
|---:|---:|---:|
| 0,50 | 2.500,0 | 50,00% |
| 0,49 | 1.904,1 | 11,92% |
| 0,45 | 500,0 | 0,00% |
| 0,40 | 250,0 | 0,00% |

## A estratégia só importa quando o jogo não é justo

Capital 1, meta 8. "Tímida" aposta uma unidade por vez; "ousada" aposta sempre o
máximo que faz sentido (o menor entre o que tem e o que falta):

| p | tímida | ousada | diferença |
|---:|---:|---:|---:|
| 0,60 | **34,69%** | 21,60% | −13,09 |
| 0,55 | **22,75%** | 16,64% | −6,11 |
| **0,50** | **12,50%** | **12,50%** | **0,00** |
| 0,45 | 5,58% | **9,11%** | +3,53 |
| 0,40 | 2,03% | **6,40%** | +4,37 |
| 0,30 | 0,15% | **2,70%** | +2,55 |

**No jogo justo as duas dão exatamente a mesma chance.** Não é aproximação: é
igualdade até a última casa, em todo capital e toda meta testados. Com jogo
justo, nenhuma sequência de apostas muda a chance de chegar à meta — só muda
quanto tempo leva.

Fora do justo, a estratégia decide, e para lados opostos:

| p | tímida | ousada | apostas (tímida) | apostas (ousada) |
|---:|---:|---:|---:|---:|
| 0,50 | 50,0000% | 50,0000% | 1.024,0 | 1,0 |
| 0,49 | 21,7521% | **49,0000%** | 903,9 | 1,0 |
| 0,45 | 0,1624% | **45,0000%** | 319,0 | 1,0 |
| 0,40 | 0,0002% | **40,0000%** | 160,0 | 1,0 |

Com 45% por aposta, partindo da metade do caminho, a tímida dá 0,16% e a ousada
dá 45%: **277 vezes melhor**. E o motivo é simples quando se olha: apostando
tudo de uma vez, o jogo acaba em **uma** aposta, e a desvantagem é cobrada uma
vez só. Apostando de um em um, ela é cobrada centenas de vezes.

Num jogo que te favorece, o raciocínio inverte: quanto mais apostas, mais vezes
a sua vantagem é cobrada, e a tímida ganha.

## O que as medidas me corrigiram

1. **"Com desvantagem pequena, capital grande protege."** É o contrário: com 1%
   de desvantagem, dobrar 10 fichas tem 40% de chance e dobrar 200 tem 0,03%.
   Capital maior significa mais apostas, e cada aposta cobra a desvantagem.
2. **"A estratégia sempre importa."** No jogo justo, tímida e ousada dão o mesmo
   número até a última casa decimal. Só a duração muda.
3. **"Apostar tudo de uma vez é sempre imprudente."** Num jogo desfavorável é a
   melhor estratégia possível, e por uma margem enorme.
4. **"Jogo equilibrado acaba rápido porque qualquer um pode ganhar."** Dura o
   produto dos capitais: 50 contra 50 leva 2.500 apostas, 25 vezes mais que 1
   contra 99.

## Como está organizado

- `Jogo`: as fórmulas fechadas da chance e da duração.
- `Tabuleiro`: o juiz. Resolve o sistema `v[i] = p·v[i+1] + q·v[i−1]` pelo
  algoritmo de Thomas, exatamente, sem iterar até convergir.
- `Ousada`: a estratégia que aposta o máximo. Com essa regra o capital dobra ou
  zera e nunca volta a um valor visitado, então não há sistema para resolver:
  é uma recursão que termina.
- `Medidor`: as três tabelas acima, rodando no CI nos três sistemas.

22 testes.
