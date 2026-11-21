package br.unisinos.edu;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

final class Graficos {

    private static final int LARGURA = 1000;
    private static final int ALTURA = 400;

    // Largura útil aproximada da área de plotagem, em pixels. Serve só para
    // sugerir ao XChart o espaçamento dos rótulos.
    private static final double LARGURA_UTIL_PX = 880.0;

    // Fator < 1 faz o XChart escolher passo de 1 bit (e não 2 bits) nos rótulos.
    private static final double FATOR_PASSO = 0.9;

    private Graficos() {
    }

    /** Figura com todos os bits. */
    static void salvar(Path pasta, String nome, String titulo, double[] tempoMs,
                       double[] sinal, double tbMs, int nBits) throws IOException {
        salvar(pasta, nome, titulo, tempoMs, sinal, tbMs, nBits, nBits);
    }

    /** Figura exibindo apenas os primeiros nBitsExibidos bits (ex.: 4 para zoom). */
    static void salvar(Path pasta, String nome, String titulo, double[] tempoMs,
                       double[] sinal, double tbMs, int nBits, int nBitsExibidos) throws IOException {
        Files.createDirectories(pasta);

        double fimMs = nBitsExibidos * tbMs;

        // tempoMs é crescente, então basta pegar o prefixo até fimMs
        int n = 0;
        for (double t : tempoMs) {
            if (t <= fimMs + 1e-9) {
                n++;
            }
        }
        double[] tempoVisivel = Arrays.copyOf(tempoMs, n);
        double[] sinalVisivel = Arrays.copyOf(sinal, n);

        XYChart chart = new XYChartBuilder().width(LARGURA).height(ALTURA)
                .title(titulo).xAxisTitle("Tempo (ms)").yAxisTitle("Amplitude").build();
        chart.getStyler().setLegendVisible(false);

        // Eixo X fixo de 0 até o fim exibido, com rótulos no passo de 1 bit
        chart.getStyler().setXAxisMin(0.0);
        chart.getStyler().setXAxisMax(fimMs);
        chart.getStyler().setXAxisTickMarkSpacingHint(
                (int) Math.round(LARGURA_UTIL_PX * tbMs / fimMs * FATOR_PASSO));

        XYSeries s = chart.addSeries("sinal", tempoVisivel, sinalVisivel);
        s.setMarker(SeriesMarkers.NONE);
        s.setLineColor(Color.BLUE);

        double min = Arrays.stream(sinalVisivel).min().orElse(-1.0);
        double max = Arrays.stream(sinalVisivel).max().orElse(1.0);
        for (int k = 0; k <= nBitsExibidos; k++) {
            double t = k * tbMs;
            XYSeries limite = chart.addSeries("limite " + k,
                    new double[] {t, t}, new double[] {min, max});
            limite.setMarker(SeriesMarkers.NONE);
            limite.setLineColor(Color.GRAY);
            limite.setLineStyle(SeriesLines.DOT_DOT);
        }

        BitmapEncoder.saveBitmap(chart, pasta.resolve(nome).toString(), BitmapEncoder.BitmapFormat.PNG);
    }
}