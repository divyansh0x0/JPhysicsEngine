package material.utils;

public class BitUtility {
    public static int invertBits(int num) {
        // calculating number of
        // bits in the number
        int x = (int) (Math.log(num) /
                Math.log(2)) + 1;

        // Inverting the
        // bits one by one
        for (int i = 0; i < x; i++)
            num = (num ^ (1 << i));

        return num;
    }
}
