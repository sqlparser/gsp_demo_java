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

public class Financials
{
    public static double db(double cost, double salvage, int life, int period,
            int month) {
        double rate = 1 - Math.pow(salvage / cost, 1. / life);
        rate = Maths.round(rate, 3);
        double dep = 0;
        double total = 0;
        for (int i = 0; i < period; i++) {
            if (i == 0) {
                total = dep = cost * rate * month / 12;
            } else if (i == life) {
                dep = ((cost - total) * rate * (12 - month)) / 12;
            } else {
                dep = (cost - total) * rate;
                total += dep;
            }
        }
        return dep;
    }

    public static double ddb(double cost, double salvage, int life, int period,
            double factor) {
        double dep = 0;
        double total = 0;
        double rate = factor / life;
        for (int i = 0; i < period; i++) {
            dep = ((cost - salvage) - total) * rate;
            total += dep;
        }
        return dep;
    }

    public static double vdb(double cost, double salvage, int life,
            double start_period, double end_period, double factor,
            boolean no_switch) {
        return 0;
    }
}
