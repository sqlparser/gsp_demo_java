package org.boris.expr.function.excel;

import org.boris.expr.ExprException;
import org.boris.expr.function.DoubleInOutFunction;

public class RADIANS extends DoubleInOutFunction
{
    protected double evaluate(double value) throws ExprException {
        return value * Math.PI / 180;
    }
}
