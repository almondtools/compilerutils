package net.amygdalum.util.builders;

public final class PrimitiveArrays<T> {

    private PrimitiveArrays() {
    }

    public static void revert(byte[] array) {
        int end = array.length - 1;
        for (int i = 0; i < array.length / 2; i++) {
            int j = end - i;

            byte temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static void revert(short[] array) {
        int end = array.length - 1;
        for (int i = 0; i < array.length / 2; i++) {
            int j = end - i;

            short temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static void revert(int[] array) {
        int end = array.length - 1;
        for (int i = 0; i < array.length / 2; i++) {
            int j = end - i;

            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static void revert(long[] array) {
        int end = array.length - 1;
        for (int i = 0; i < array.length / 2; i++) {
            int j = end - i;

            long temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static void revert(float[] array) {
        int end = array.length - 1;
        for (int i = 0; i < array.length / 2; i++) {
            int j = end - i;

            float temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static void revert(double[] array) {
        int end = array.length - 1;
        for (int i = 0; i < array.length / 2; i++) {
            int j = end - i;

            double temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static void revert(char[] array) {
        int end = array.length - 1;
        for (int i = 0; i < array.length / 2; i++) {
            int j = end - i;

            char temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static void revert(boolean[] array) {
        int end = array.length - 1;
        for (int i = 0; i < array.length / 2; i++) {
            int j = end - i;

            boolean temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

}
