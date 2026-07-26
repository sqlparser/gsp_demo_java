package org.boris.expr.function.excel;

import org.boris.expr.ExprException;
import org.boris.expr.function.DoubleInOutFunction;

public class ODD extends DoubleInOutFunction
{
    protected double evaluate(double value) throws ExprException {
        double res = (value + 1) % 2;
        if (res < 0) {
            return value - 2 - res;
        } else if (res == 0) {
            return value;
        } else {
            return value + 2 - res;
        }
    }
}
