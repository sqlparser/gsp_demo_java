package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;
import org.boris.expr.util.Statistics;

public class CONFIDENCE extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 3);
        Expr ea = evalArg(context, args[0]);
        if (!isNumber(ea))
            return ExprError.VALUE;
        double alpha = asDouble(context, ea, true);
        if (alpha <= 0 || alpha >= 1)
            return ExprError.NUM;
        Expr es = evalArg(context, args[1]);
        if (!isNumber(es))
            return ExprError.VALUE;
        double stdev = asDouble(context, es, true);
        if (stdev <= 0)
            return ExprError.NUM;
        Expr esi = evalArg(context, args[2]);
        if (!isNumber(esi))
            return ExprError.VALUE;
        int size = asInteger(context, esi, true);
        if (size < 1)
            return ExprError.NUM;

        return new ExprDouble(Statistics.confidence(alpha, stdev, size));
    }
}
