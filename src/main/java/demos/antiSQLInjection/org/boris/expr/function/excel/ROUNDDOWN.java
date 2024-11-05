package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;
import org.boris.expr.util.Maths;

public class ROUNDDOWN extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 2);
        double num = asDouble(context, args[0], true);
        int dps = asInteger(context, args[1], true);
        return new ExprDouble(Maths.roundDown(num, dps));
    }
}
