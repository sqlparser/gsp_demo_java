package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;

public class STDEVP extends STDEV
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        return stdevp(context, args);
    }

    public static Expr stdevp(IEvaluationContext context, Expr[] args)
            throws ExprException {
        return stdev(context, args, true);
    }
}
