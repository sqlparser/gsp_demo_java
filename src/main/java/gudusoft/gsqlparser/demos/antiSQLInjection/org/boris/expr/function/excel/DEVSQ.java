package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprException;
import org.boris.expr.function.ForEachNumberFunction;
import org.boris.expr.util.Counter;

public class DEVSQ extends ForEachNumberFunction
{
    public DEVSQ() {
        setIterations(2);
    }

    protected void value(Counter counter, double value) {
        switch (counter.iteration) {
        case 1:
            avg(counter, value);
            break;
        case 2:
            if (!counter.flag) {
                counter.value = counter.value / counter.count;
                counter.flag = true;
            }
            devsq(counter, value);
            break;
        }
    }

    private void devsq(Counter counter, double value) {
        counter.value2 += Math.pow(value - counter.value, 2);
    }

    private void avg(Counter counter, double value) {
        counter.value += value;
        counter.count++;
    }

    protected Expr evaluate(Counter counter) throws ExprException {
        return new ExprDouble(counter.value2);
    }
}
