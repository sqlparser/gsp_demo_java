package org.boris.expr.function.excel;

import org.boris.expr.ExprException;
import org.boris.expr.function.DoubleInOutFunction;
import org.boris.expr.util.Maths;

public class ACOSH extends DoubleInOutFunction
{
    protected double evaluate(double value) throws ExprException {
        return Maths.acosh(value);
    }
}
