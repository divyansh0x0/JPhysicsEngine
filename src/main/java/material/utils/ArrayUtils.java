package material.utils;

import org.jetbrains.annotations.NotNull;

public class ArrayUtils {
    public static void reverseArray(double @NotNull [] arr){
        int size = arr.length;
        int half = size/2;
        for (int i = 0; i < half; i++) {
            int oppositeIndex = size - i - 1;
            double temp1 = arr[i];
            double temp2 = arr[oppositeIndex];
            arr[i] = temp2;
            arr[oppositeIndex] = temp1;
        }
    }
    public static void reverseArray(float @NotNull [] arr){
        int size = arr.length;
        int half = size/2;
        for (int i = 0; i < half; i++) {
            int oppositeIndex = size - i - 1;
            float temp1 = arr[i];
            float temp2 = arr[oppositeIndex];
            arr[i] = temp2;
            arr[oppositeIndex] = temp1;
        }
    }

    public static float sumArray(float[] magnitudes) {
        if(magnitudes == null || magnitudes.length == 0)
            return 0f;
        float sum = 0;
        for(float f : magnitudes){
            sum += f;
        }
        return sum;
    }
}
