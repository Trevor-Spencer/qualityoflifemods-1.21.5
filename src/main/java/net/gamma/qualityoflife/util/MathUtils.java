package net.gamma.qualityoflife.util;

import java.util.Map;

public class MathUtils {
    private static final Map<Character, Integer> ROMANTOINT = Map.of(
            'I', 1,
            'V', 5,
            'X', 10,
            'L', 50,
            'C', 100,
            'D', 500,
            'M', 1000
    );

    public static int findNumeric(String romanNumeral)
    {
        int sum = 0;
        int size = romanNumeral.length();
        for(int i = 0; i < size; i++)
        {
            int currentVal = ROMANTOINT.get(romanNumeral.charAt(i));
            if(i + 1 < size && currentVal < ROMANTOINT.get(romanNumeral.charAt(i+1)))
            {
                sum -= currentVal;
            }
            else
            {
                sum += currentVal;
            }
        }
        return sum;
    }
}
