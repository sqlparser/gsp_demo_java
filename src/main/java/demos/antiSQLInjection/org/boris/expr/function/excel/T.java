package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class T extends AbstractFunction
{
    private static final ExprString EMPTY = new ExprString("");

    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 1);
        Expr a = evalArg(context, args[0]);

        if (a instanceof ExprString) {
            return a;
        } else {
            return EMPTY;
        }
    }
}
