package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprVariable;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class ISREF extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args) throws ExprException {
        assertArgCount(args, 1);
        return bool(args[0] instanceof ExprVariable);
    }
}
