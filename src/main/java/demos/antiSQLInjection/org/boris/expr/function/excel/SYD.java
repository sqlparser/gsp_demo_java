package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class SYD extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 4);
        double cost = asDouble(context, args[0], true);
        double salvage = asDouble(context, args[1], true);
        double life = asDouble(context, args[2], true);
        double per = asDouble(context, args[3], true);
        double syd = ((cost - salvage) * (life - per + 1) * 2) /
                (life * (life + 1));

        return new ExprDouble(syd);
    }
}
