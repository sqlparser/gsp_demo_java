package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprArray;
import org.boris.expr.ExprBoolean;
import org.boris.expr.ExprEvaluatable;
import org.boris.expr.ExprException;
import org.boris.expr.ExprMissing;
import org.boris.expr.ExprNumber;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class OR extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        if (args.length == 0)
            throw new ExprException(
                    "OR function requires at least one argument");

        for (Expr a : args) {
            if (eval(context, a, true))
                return ExprBoolean.TRUE;
        }
        return ExprBoolean.FALSE;
    }

    protected boolean eval(IEvaluationContext context, Expr a, boolean strict)
            throws ExprException {
        if (a == null)
            return false;

        if (a instanceof ExprEvaluatable) {
            a = ((ExprEvaluatable) a).evaluate(context);
        }

        if (a instanceof ExprNumber) {
            return ((ExprNumber) a).booleanValue();
        }

        if (a instanceof ExprMissing)
            return false;

        if (a instanceof ExprArray) {
            ExprArray arr = (ExprArray) a;
            int rows = arr.rows();
            int cols = arr.columns();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (eval(context, arr.get(i, j), false))
                        return true;
                }
            }
        }

        if (strict)
            throw new ExprException("Unexpected argument to " +
                    getClass().getSimpleName() + ": " + a);

        return false;
    }

}
