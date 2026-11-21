# Atividade 13: Codificação de Banda Base e Modulação Digital

Projeto em Java da Atividade 13 da disciplina de Redes de Computadores (UNISINOS, prof. Cristiano Bonato Both). Ele simula a transmissão de uma sequência binária com codificação de banda base e modulação digital, aplica ruído gaussiano e gera gráficos e uma tabela de BER.

## O que o projeto faz

**1. Codificação de banda base** (`CodigoBandaBase`)
- NRZ unipolar: bit 1 → +1, bit 0 → 0.
- NRZ polar: bit 1 → +1, bit 0 → −1.
- Manchester: bit 1 → (+1, −1), bit 0 → (−1, +1), com transição no meio do bit.

**2. Modulação digital** (`Modulador`)
- ASK: bit 1 → A·cos(2π·fp·t), bit 0 → 0.
- BPSK: bit 1 → +A·cos(2π·fp·t), bit 0 → −A·cos(2π·fp·t).
- FSK: bit 1 → A·cos(2π·f1·t), bit 0 → A·cos(2π·f0·t).

**3. Ruído e BER**
- Ruído AWGN aplicado à parte real do sinal (`AwgnChannel`), com σ ∈ {0,5; 1,0; 2,0; 4,0}.
- Decisão por integração sobre cada bit.
- BER medida em 10.000 bits aleatórios, além dos erros na sequência de 16 bits.

**4. Gráficos** (`Graficos`)
- Para cada codificação e modulação: sinal limpo e sinal com ruído. Para as modulações, também um zoom de 0 a 4 ms.
- As linhas pontilhadas marcam os limites de bit (1 ms).

### Parâmetros padrão

| Parâmetro | Valor |
|---|---|
| Taxa de bits | 1000 bps (Tb = 1 ms) |
| Frequência de amostragem | 100 kHz (100 amostras/bit) |
| Portadora (ASK e BPSK) | 4 kHz |
| FSK | f0 = 3 kHz (bit 0), f1 = 5 kHz (bit 1) |
| Amplitude | 1,0 |
| Sequência de 16 bits | 1011001011100010 |

## Estrutura

```
src/main/java/br/unisinos/edu/
├── AwgnChannel.java          canal com ruído gaussiano
├── CodigoBandaBase.java      NRZ unipolar, NRZ polar e Manchester
├── Complex.java              número complexo (record)
├── DataGenerator.java        geração de bits aleatórios
├── Graficos.java             geração das figuras (XChart)
├── Main.java                 execução dos testes e geração das figuras
├── Metrics.java              cálculo de erros e BER
├── Modulador.java            ASK, FSK e BPSK (modulação e demodulação)
└── ParametrosModulacao.java  parâmetros da simulação
```

## Requisitos

- JDK 23 ou superior
- Maven

A biblioteca de gráficos (XChart 4.0.4) é baixada pelo Maven.

## Como executar

1. Compile, empacote e copie as dependências para `target/lib`:

   ```
   mvn -q -DskipTests clean package
   ```

2. Execute, informando a pasta de saída das figuras:

   Windows (PowerShell ou cmd):
   ```
   java -cp "target\classes;target\lib\*" br.unisinos.edu.Main resultados
   ```

   Linux ou macOS:
   ```
   java -cp "target/classes:target/lib/*" br.unisinos.edu.Main resultados
   ```

Se nenhuma pasta for informada, as figuras são gravadas em `resultados/`, dentro da pasta do projeto.

O JAR gerado pelo `package` (`target/Atividade-13-1.0-SNAPSHOT.jar`) não inclui o XChart e não é executável sozinho. Por isso o programa é executado com `target/classes` e `target/lib/*` no classpath.

A execução imprime no console a sequência, os parâmetros, as convenções de codificação e a tabela de erros e BER para cada técnica e cada σ. Ao final, informa o caminho completo da pasta com as figuras.

## Saída

A pasta de saída recebe 15 arquivos PNG:
- `banda_base_nrz_unipolar`, `banda_base_nrz_polar`, `banda_base_manchester` e as versões `_ruido`;
- `modulacao_ask`, `modulacao_fsk`, `modulacao_bpsk`, com as versões `_zoom` (0 a 4 ms) e `_ruido`.

A pasta `resultados/` está no `.gitignore`. A semente do ruído é fixa (42), então a execução reproduz os mesmos resultados.

## Resultados resumidos

BER com σ = 4,0 em 10.000 bits:

| Técnica | BER |
|---|---|
| NRZ unipolar | 0,104 |
| NRZ polar | 0,005 |
| Manchester | 0,006 |
| ASK | 0,197 |
| FSK | 0,103 |
| BPSK | 0,041 |