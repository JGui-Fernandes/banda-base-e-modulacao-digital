package br.unisinos.edu;

public record ParametrosModulacao(
        double taxaBits,
        double fs,
        double fPortadora,
        double f0,
        double f1,
        double amplitude
) {

    public static ParametrosModulacao padrao() {
        return new ParametrosModulacao(1000, 100_000, 4000, 3000, 5000, 1.0);
    }

    public int amostrasPorBit() {
        return (int) Math.round(fs / taxaBits);
    }
}