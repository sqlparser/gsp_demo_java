package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.ExprNumber;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;
import org.boris.expr.util.Statistics;

public class CRITBINOM extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 3);
        Expr et = evalArg(context, args[0]);
        if (!isNumber(et))
            return ExprError.VALUE;
        int trials = ((ExprNumber) et).intValue();
        if (trials < 0)
            return ExprError.NUM;
        Expr ep = evalArg(context, args[1]);
        if (!isNumber(ep))
            return ExprError.VALUE;
        double p = ((ExprNumber) ep).doubleValue();
        if (p < 0 || p > 1)
            return ExprError.NUM;
        Expr ea = evalArg(context, args[2]);
        if (!isNumber(ea))
            return ExprError.VALUE;
        double alpha = ((ExprNumber) ea).doubleValue();
        if (alpha < 0 || alpha > 1)
            return ExprError.NUM;

        return new ExprDouble(Statistics.critBinom(trials, p, alpha));
    }
}
