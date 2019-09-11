package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.ExprNumber;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;
import org.boris.expr.util.Statistics;

public class CHIDIST extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 2);
        Expr eX = evalArg(context, args[0]);
        if (!isNumber(eX))
            return ExprError.VALUE;
        double x = ((ExprNumber) eX).doubleValue();
        Expr eDF = evalArg(context, args[1]);
        if (!isNumber(eDF))
            return ExprError.VALUE;
        int df = ((ExprNumber) eDF).intValue();
        if (df < 0 || df > 10e10)
            return ExprError.NUM;
        return new ExprDouble(Statistics.chiDist(x, df));
    }
}
