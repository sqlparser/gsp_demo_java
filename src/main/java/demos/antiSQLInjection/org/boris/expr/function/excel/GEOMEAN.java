package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprException;
import org.boris.expr.function.ForEachNumberFunction;
import org.boris.expr.util.Counter;

public class GEOMEAN extends ForEachNumberFunction
{
    protected void initialize(Counter counter) throws ExprException {
        counter.value = 1;
    }

    protected void value(Counter counter, double value) {
        counter.count++;
        counter.value *= value;
    }

    protected Expr evaluate(Counter counter) throws ExprException {
        return new ExprDouble(Math.pow(counter.value, 1. / counter.count));
    }
}
