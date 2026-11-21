package br.unisinos.edu;

public record Complex(
        double re,
        double im
) {

    public static Complex[] deReais(double[] valores) {
        Complex[] zs = new Complex[valores.length];
        for (int i = 0; i < valores.length; i++) {
            zs[i] = new Complex(valores[i], 0.0);
        }
        return zs;
    }


    public static double[] partesReais(Complex[] complex) {
        double[] re = new double[complex.length];
        for (int i = 0; i < complex.length; i++) {
            re[i] = complex[i].re();
        }
        return re;
    }
}
