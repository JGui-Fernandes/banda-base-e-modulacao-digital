package br.unisinos.edu;

import java.util.Random;

public final class DataGenerator {

    private DataGenerator() {
    }

    //    Mesma semente = mesma sequencia
    public static int[] bitsAleatorios(int n, long semente) {
        if (n < 0) {
            throw new IllegalArgumentException("n não pode ser negativo: " + n);
        }
        Random rnd = new Random(semente);
        int[] bits = new int[n];
        for (int i = 0; i < n; i++) {
            bits[i] = rnd.nextInt(2);
        }
        return bits;
    }
}

