package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.ExprNumber;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;
import org.boris.expr.util.Condition;

public class SUMIF extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertMinArgCount(args, 2);

        /*
        // First argument must be a reference to a range
        if (!(args[0] instanceof ExprVariable)) {
            throw new ExprException(
                    "First argument to SUMIF must be a reference");
        }

        // Sum range (if present) must be a reference to a range
        if (args.length > 2 && !(args[2] instanceof ExprVariable)) {
            throw new ExprException(
                    "Third argument to SUMIF must be a reference");
        }*/

        Expr range = evalArg(context, args[0]);
        int len = getLength(range);
        Condition cond = Condition.valueOf(evalArg(context, args[1]));
        Expr sumrange = args.length == 3 ? evalArg(context, args[2]) : range;

        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum += eval(get(range, i), cond, get(sumrange, i));
        }

        return new ExprDouble(sum);
    }

    protected double eval(Expr item, Condition c, Expr value)
            throws ExprException {
        if (c.eval(item)) {
            if (value instanceof ExprDouble || value instanceof ExprInteger) {
                return ((ExprNumber) value).doubleValue();
            }
        }
        return 0.;
    }
}
