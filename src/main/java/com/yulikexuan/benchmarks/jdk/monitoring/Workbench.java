//: com.yulikexuan.benchmarks.jdk.monitoring.Workbench.java

package com.yulikexuan.benchmarks.jdk.monitoring;


import java.io.Console;


/*
 * https://www.baeldung.com/java-fibonacci
 * Φ = ( 1 + √5 )/2 = 1.6180339887...
 * Sn = Φⁿ–(– Φ⁻ⁿ)/√5
 */
public class Workbench {

    public static void main(String[] args) {

        final Console console = System.console();
        String numStr = null;
        int factor = 0;
        do {
            numStr = console.readLine(
                    ">>>>>>> Please enter a number to produce its Fibonacci: ");
            try {
                factor = Integer.parseInt(numStr);
                if (factor < 0) {
                    factor = 0;
                }
                console.printf(">>>>>>> The Fibonacci of number %d is : %d%n%n",
                        factor, calculateTheNthFibonacciTerm(factor));
            } catch (Exception e) {
                System.exit(1);
            }

        } while (factor >= 0);

    }

    private static long calculateTheNthFibonacciTerm(final int n) {

        double squareRootOf5 = Math.sqrt(5);
        double phi = (1 + squareRootOf5) / 2;

        long nthTerm = calculate(squareRootOf5, phi, n);

        return nthTerm;
    }

    private static long calculate(
            final double squareRootOf5, final double phi, final int n) {

        return (long)(((Math.pow(phi, n) - Math.pow(-phi, -n)) / squareRootOf5));
    }

}///:~