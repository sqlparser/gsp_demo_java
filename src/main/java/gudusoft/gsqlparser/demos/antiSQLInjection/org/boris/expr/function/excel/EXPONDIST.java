package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;
import org.boris.expr.util.Statistics;

public class EXPONDIST extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 3);
        double x = asDouble(context, args[0], true);
        double l = asDouble(context, args[1], true);
        boolean c = asBoolean(context, args[2], true);

        return new ExprDouble(Statistics.exponDist(x, l, c));
    }
}
