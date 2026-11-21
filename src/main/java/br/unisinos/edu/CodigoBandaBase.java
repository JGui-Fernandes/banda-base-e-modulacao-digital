package br.unisinos.edu;

public enum CodigoBandaBase {
    NRZ_UNIPOLAR("NRZ unipolar"),
    NRZ_POLAR("NRZ polar"),
    MANCHESTER("Manchester");

    private final String rotulo;

    CodigoBandaBase(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    public double[] codificar(int[] bits, int amostrasPorBit) {
        double[] sinal = new double[bits.length * amostrasPorBit];
        for (int i = 0; i < bits.length; i++) {
            for (int k = 0; k < amostrasPorBit; k++) {
                sinal[i * amostrasPorBit + k] = nivel(bits[i], k, amostrasPorBit);
            }
        }
        return sinal;
    }

    public int[] decodificar(double[] amostras, int amostrasPorBit) {
        int n = amostras.length / amostrasPorBit;
        int[] bits = new int[n];
        for (int i = 0; i < n; i++) {
            int ini = i * amostrasPorBit;
            int fim = ini + amostrasPorBit;
            int meio = ini + amostrasPorBit / 2;
            bits[i] = switch (this) {
                case NRZ_UNIPOLAR -> media(amostras, ini, fim) > 0.5 ? 1 : 0;
                case NRZ_POLAR -> media(amostras, ini, fim) > 0.0 ? 1 : 0;
                case MANCHESTER -> media(amostras, ini, meio) > media(amostras, meio, fim) ? 1 : 0;
            };
        }
        return bits;
    }

    private double nivel(int bit, int k, int amostrasPorBit) {
        return switch (this) {
            case NRZ_UNIPOLAR -> bit == 1 ? 1.0 : 0.0;
            case NRZ_POLAR -> bit == 1 ? 1.0 : -1.0;
            case MANCHESTER -> {
                boolean primeiraMetade = k < amostrasPorBit / 2;
                yield (bit == 1) == primeiraMetade ? 1.0 : -1.0;
            }
        };
    }

    private static double media(double[] a, int ini, int fim) {
        double soma = 0.0;
        for (int i = ini; i < fim; i++) {
            soma += a[i];
        }
        return soma / (fim - ini);
    }
}
