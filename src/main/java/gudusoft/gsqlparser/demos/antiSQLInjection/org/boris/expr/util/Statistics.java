/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Peter Smith
 *******************************************************************************/
package org.boris.expr.util;

import java.math.BigInteger;

public class Statistics
{
    public static BigInteger factorial(int value) {
        BigInteger n = BigInteger.ONE;
        for (int i = 2; i <= value; i++) {
            n = n.multiply(BigInteger.valueOf(i));
        }
        return n;
    }

    public static double combin(int num, int cho) {
        return factorial(num).divide(
                factorial(cho).multiply(factorial(num - cho))).doubleValue();
    }

    public static double permut(int num, int cho) {
        return factorial(num).divide(factorial(num - cho)).doubleValue();
    }

    public static double binomDist(int x, int n, double p, boolean cumulative) {
        if (!cumulative) {
            return combin(n, x) * Math.pow(p, x) * Math.pow(1 - p, n - x);
        } else {
            double c = 0;
            for (int y = 0; y < x; y++) {
                c += binomDist(y, n, p, false);
            }
            return c;
        }
    }

    public static double critBinom(int n, double p, double alpha) {
        double c = 0;
        for (int y = 0; y < n; y++) {
            c += binomDist(y, n, p, false);
            if (c >= alpha)
                return y;
        }
        return 0;
    }

    public static double exponDist(double x, double lambda, boolean cumulative) {
        if (cumulative) {
            return (1 - Math.pow(Math.E, -lambda * x));
        } else {
            return (lambda * Math.pow(Math.E, -lambda * x));
        }
    }

    public static double confidence(double alpha, double stdev, int size) {
        return 1.96 * stdev / Math.sqrt(size);
    }

    public static double betaDist(double x, double alpha, double beta,
            double a, double b) {
        return 0;
    }

    public static double betaInv(double x, double alpha, double beta, double a,
            double b) {
        return 0;
    }

    public static double chiDist(double x, int df) {
        return 0;
    }

    public static double chiInv(double p, int df) {
        return 0;
    }

    public static double normDensity(double z) {
        return (1 / Math.sqrt(2 * Math.PI)) * Math.pow(Math.E, -z * z / 2);
    }

    public static double normsDist(double z) {
        return N(z);
    }

    static double N(double x) {
        double b1 = 0.319381530;
        double b2 = -0.356563782;
        double b3 = 1.781477937;
        double b4 = -1.821255978;
        double b5 = 1.330274429;
        double p = 0.2316419;
        double c = 0.39894228;

        if (x >= 0.0) {
            double t = 1.0 / (1.0 + p * x);
            return (1.0 - c * Math.exp(-x * x / 2.0) * t *
                    (t * (t * (t * (t * b5 + b4) + b3) + b2) + b1));
        } else {
            double t = 1.0 / (1.0 - p * x);
            return (c * Math.exp(-x * x / 2.0) * t * (t *
                    (t * (t * (t * b5 + b4) + b3) + b2) + b1));
        }
    }
}
