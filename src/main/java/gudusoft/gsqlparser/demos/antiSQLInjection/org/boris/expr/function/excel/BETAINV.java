package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.ExprNumber;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;
import org.boris.expr.util.Statistics;

public class BETAINV extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertMinArgCount(args, 3);
        assertMaxArgCount(args, 5);
        Expr eX = evalArg(context, args[0]);
        if (!isNumber(eX))
            return ExprError.VALUE;
        double x = ((ExprNumber) eX).doubleValue();
        Expr eAlpha = evalArg(context, args[1]);
        if (!isNumber(eAlpha))
            return ExprError.VALUE;
        double alpha = ((ExprNumber) eAlpha).doubleValue();
        if (alpha <= 0)
            return ExprError.NUM;
        Expr eBeta = evalArg(context, args[2]);
        if (!isNumber(eBeta))
            return ExprError.VALUE;
        double beta = ((ExprNumber) eBeta).doubleValue();
        if (beta <= 0)
            return ExprError.NUM;
        double a = 0, b = 1;
        if (args.length > 3) {
            Expr eA = evalArg(context, args[3]);
            if (!isNumber(eA))
                return ExprError.VALUE;
            a = ((ExprNumber) eA).doubleValue();
        }
        if (args.length > 4) {
            Expr eB = evalArg(context, args[4]);
            if (!isNumber(eB))
                return ExprError.VALUE;
            b = ((ExprNumber) eB).doubleValue();
        }
        if (x < a || x > b || a == b)
            return ExprError.NUM;
        return new ExprDouble(Statistics.betaInv(x, alpha, beta, a, b));
    }
}
