package gregtech.api.util.extensions;

public class Arrays {
    public static int[] of(int... objects) {
        return objects;
    }

    public static float[] of(float... objects) {
        return objects;
    }

    public static double[] of(double... objects) {
        return objects;
    }

    public static char[] of(char... objects) {
        return objects;
    }

    public static byte[] of(byte... objects) {
        return objects;
    }

    public static long[] of(long... objects) {
        return objects;
    }

    @SafeVarargs
    public static <T> T[] of(T... objects) {
        return objects;
    }
}
