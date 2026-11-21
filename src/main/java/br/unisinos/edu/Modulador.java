package br.unisinos.edu;

public final class Modulador {

    public enum Tecnica { ASK, FSK, BPSK }

    private Modulador() {
    }

    public static double[] modular(int[] bits, Tecnica tecnica, ParametrosModulacao p) {
        int sp = p.amostrasPorBit();
        double[] sinal = new double[bits.length * sp];
        for (int n = 0; n < sinal.length; n++) {
            int bit = bits[n / sp];
            double t = n / p.fs();
            sinal[n] = switch (tecnica) {
                case ASK -> bit * p.amplitude() * Math.cos(2 * Math.PI * p.fPortadora() * t);
                case BPSK -> (bit == 1 ? 1 : -1) * p.amplitude() * Math.cos(2 * Math.PI * p.fPortadora() * t);
                case FSK -> p.amplitude() * Math.cos(2 * Math.PI * (bit == 1 ? p.f1() : p.f0()) * t);
            };
        }
        return sinal;
    }

    public static int[] demodular(double[] r, Tecnica tecnica, ParametrosModulacao p) {
        int sp = p.amostrasPorBit();
        int[] bits = new int[r.length / sp];
        for (int i = 0; i < bits.length; i++) {
            double cPort = 0.0;
            double c0 = 0.0;
            double c1 = 0.0;
            for (int n = i * sp; n < (i + 1) * sp; n++) {
                double t = n / p.fs();
                cPort += r[n] * Math.cos(2 * Math.PI * p.fPortadora() * t);
                c0 += r[n] * Math.cos(2 * Math.PI * p.f0() * t);
                c1 += r[n] * Math.cos(2 * Math.PI * p.f1() * t);
            }
            bits[i] = switch (tecnica) {
                case ASK -> cPort > p.amplitude() * sp / 4.0 ? 1 : 0;
                case BPSK -> cPort > 0.0 ? 1 : 0;
                case FSK -> c1 > c0 ? 1 : 0;
            };
        }
        return bits;
    }
}
