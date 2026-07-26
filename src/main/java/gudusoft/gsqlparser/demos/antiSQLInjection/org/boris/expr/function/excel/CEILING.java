package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class CEILING extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 2);
        double val = asDouble(context, args[0], true);
        double rnd = asDouble(context, args[1], true);
        if (rnd == 0)
            return ExprDouble.ZERO;
        if ((val < 0 && rnd > 0) || (val > 0 && rnd < 0))
            return ExprError.NUM;
        double m = val % rnd;
        if (rnd < 0)
            rnd = 0;
        return new ExprDouble(val - m + rnd);
    }

    public boolean equalish(double d1, double d2) {
        return Math.abs(d1 - d2) < 0.0000000001;
    }
}
