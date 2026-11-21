package br.unisinos.edu;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {

    static final int[] BITS = {1, 0, 1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 0, 0, 1, 0};
    static final double[] SIGMAS = {0.5, 1.0, 2.0, 4.0};
    static final double SIGMA_GRAFICO = 2.0;
    static final long SEMENTE_RUIDO = 42L;
    static final int BITS_TESTE_BER = 10_000;
    static final int BITS_ZOOM = 4;
    static final ParametrosModulacao P = ParametrosModulacao.padrao();

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        Path pasta = Path.of(args.length > 0 ? args[0] : "resultados");
        int sp = P.amostrasPorBit();
        double tb = 1000.0 / P.taxaBits();   // ms
        double dt = tb / sp;                 // ms por amostra
        String seq = texto(BITS);

        System.out.println("Sequência de 16 bits: " + seq);
        System.out.println("Parâmetros: " + P);
        System.out.printf(Locale.ROOT, "Tb = %.3f ms, %d amostras/bit, dt = %.4f ms%n", tb, sp, dt);
        System.out.println("Convenção NRZ unipolar: 1 = +1, 0 = 0");
        System.out.println("Convenção NRZ polar: 1 = +1, 0 = -1");
        System.out.println("Convenção Manchester: 1 = (+1,-1), 0 = (-1,+1); transição no meio do bit");
        System.out.println("Modulação: ASK 1 = A·cos, 0 = 0; BPSK 1 = cos, 0 = -cos; FSK 1 = cos(f1), 0 = cos(f0)");

        System.out.println("\n== 1. Codificação de banda base ==");
        for (CodigoBandaBase c : CodigoBandaBase.values()) {
            String base = "banda_base_" + c.name().toLowerCase(Locale.ROOT);
            double[] tx = c.codificar(BITS, sp);
            double[] t = eixo(tx.length, dt);
            Graficos.salvar(pasta, base, c.rotulo() + " - bits " + seq, t, tx, tb, BITS.length);
            Graficos.salvar(pasta, base + "_ruido",
                    c.rotulo() + " com ruído (sigma=" + SIGMA_GRAFICO + ")",
                    t, ruido(tx, SIGMA_GRAFICO), tb, BITS.length);
            testarRuido(c.rotulo(), b -> c.codificar(b, sp), s -> c.decodificar(s, sp));
        }

        System.out.println("\n== 2. Modulação digital ==");
        for (Modulador.Tecnica tec : Modulador.Tecnica.values()) {
            String base = "modulacao_" + tec.name().toLowerCase(Locale.ROOT);
            double[] tx = Modulador.modular(BITS, tec, P);
            double[] t = eixo(tx.length, dt);
            Graficos.salvar(pasta, base, tec + " - bits " + seq, t, tx, tb, BITS.length);
            Graficos.salvar(pasta, base + "_zoom",
                    tec + " - bits " + seq.substring(0, BITS_ZOOM) + " (0 a " + BITS_ZOOM + " ms)",
                    t, tx, tb, BITS.length, BITS_ZOOM);
            Graficos.salvar(pasta, base + "_ruido",
                    tec + " com ruído (sigma=" + SIGMA_GRAFICO + ")",
                    t, ruido(tx, SIGMA_GRAFICO), tb, BITS.length);
            testarRuido(tec.name(), b -> Modulador.modular(b, tec, P),
                    s -> Modulador.demodular(s, tec, P));
        }

        System.out.println("\nFiguras geradas em " + pasta.toAbsolutePath());
    }

    /** Erros nos 16 bits e BER em 10.000 bits aleatórios, para cada sigma. */
    static void testarRuido(String rotulo, Function<int[], double[]> transmissor,
                            Function<double[], int[]> receptor) {
        int[] longos = DataGenerator.bitsAleatorios(BITS_TESTE_BER, SEMENTE_RUIDO);
        for (double sigma : SIGMAS) {
            int errosCurtos = Metrics.ber(BITS,
                    receptor.apply(ruido(transmissor.apply(BITS), sigma))).bitsErrados();
            double ber = Metrics.ber(longos,
                    receptor.apply(ruido(transmissor.apply(longos), sigma))).taxa();
            System.out.printf(Locale.ROOT,
                    "%-14s sigma=%.1f  erros em 16 bits=%2d  BER em %d bits=%.4f%n",
                    rotulo, sigma, errosCurtos, BITS_TESTE_BER, ber);
        }
    }

    /** Ruído AWGN apenas na parte real (sinal real), usando o canal do projeto. */
    static double[] ruido(double[] sinal, double sigma) {
        Complex[] rx = AwgnChannel.bandaBase()
                .apply(Complex.deReais(sinal), sigma, new Random(SEMENTE_RUIDO));
        return Complex.partesReais(rx);
    }

    static double[] eixo(int n, double dt) {
        double[] t = new double[n];
        for (int i = 0; i < n; i++) {
            t[i] = i * dt;
        }
        return t;
    }

    static String texto(int[] bits) {
        return Arrays.stream(bits).mapToObj(String::valueOf).collect(Collectors.joining());
    }
}