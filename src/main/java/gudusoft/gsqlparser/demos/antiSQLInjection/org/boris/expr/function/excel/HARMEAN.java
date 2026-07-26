package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.function.ForEachNumberFunction;
import org.boris.expr.util.Counter;

public class HARMEAN extends ForEachNumberFunction
{
    protected void value(Counter counter, double value) {
        if (value <= 0) {
            counter.doit = false;
            counter.result = ExprError.NUM;
            return;
        }
        counter.count++;
        counter.value += 1 / value;
    }

    protected Expr evaluate(Counter counter) throws ExprException {
        if (counter.result != null)
            return counter.result;
        return new ExprDouble(1. / (counter.value / counter.count));
    }
}
