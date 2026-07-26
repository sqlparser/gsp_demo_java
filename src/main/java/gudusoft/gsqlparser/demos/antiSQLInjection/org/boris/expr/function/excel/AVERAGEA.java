package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprException;
import org.boris.expr.function.ForEachNumberAFunction;
import org.boris.expr.util.Counter;

public class AVERAGEA extends ForEachNumberAFunction
{
    protected void value(Counter counter, double value) {
        counter.count++;
        counter.value += value;
    }

    protected Expr evaluate(Counter counter) throws ExprException {
        return new ExprDouble(counter.value / counter.count);
    }
}
