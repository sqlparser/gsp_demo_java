package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.function.ForEachNumberFunction;
import org.boris.expr.util.Counter;

public class KURT extends ForEachNumberFunction
{
    public KURT() {
        setIterations(3);
    }

    protected void value(Counter counter, double value) {
        switch (counter.iteration) {
        case 1:
            average(counter, value);
            break;
        case 2:
            stdev(counter, value);
            break;
        case 3:
            kurt(counter, value);
        }
    }

    protected void iteration(Counter counter) {
        switch (counter.iteration) {
        case 2:
            if (counter.count < 4) {
                counter.doit = false;
                counter.result = ExprError.DIV0;
            }
            counter.value /= counter.count;
            break;
        case 3:
            counter.value2 /= (counter.count - 1);
            counter.value2 = Math.sqrt(counter.value2);
            if (counter.value2 == 0) {
                counter.doit = false;
                counter.result = ExprError.DIV0;
            }
        }
    }

    private void kurt(Counter counter, double value) {
        counter.value3 += Math.pow((value - counter.value) / counter.value2, 4);
    }

    private void stdev(Counter counter, double value) {
        counter.value2 += Math.pow(value - counter.value, 2);
    }

    private void average(Counter counter, double value) {
        counter.count++;
        counter.value += value;
    }

    protected Expr evaluate(Counter counter) throws ExprException {
        double n = counter.count;
        double kurt = (n * (n + 1) / ((n - 1) * (n - 2) * (n - 3))) *
                counter.value3 - (3 * Math.pow(n - 1, 2)) / ((n - 2) * (n - 3));
        return new ExprDouble(kurt);
    }
}
