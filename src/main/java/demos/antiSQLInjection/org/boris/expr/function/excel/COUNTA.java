package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class COUNTA extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertMinArgCount(args, 1);
        int count = 0;
        for (Expr a : args) {
            count += COUNT.count(context, a, true);
        }
        return new ExprInteger(count);
    }
}
