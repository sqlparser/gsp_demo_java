package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprArray;
import org.boris.expr.ExprEvaluatable;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.ExprMissing;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class COUNTBLANK extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertMinArgCount(args, 1);
        int count = 0;
        for (Expr a : args) {
            count += count(context, a);
        }
        return new ExprInteger(count);
    }

    public static int count(IEvaluationContext context, Expr arg)
            throws ExprException {
        if (arg instanceof ExprEvaluatable) {
            arg = ((ExprEvaluatable) arg).evaluate(context);
        }

        if (arg instanceof ExprMissing) {
            return 1;
        } else if (arg instanceof ExprArray) {
            int count = 0;
            ExprArray arr = (ExprArray) arg;
            int rows = arr.rows();
            int cols = arr.columns();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    count += count(context, arr.get(i, j));
                }
            }
            return count;
        } else {
            return 0;
        }
    }
}
