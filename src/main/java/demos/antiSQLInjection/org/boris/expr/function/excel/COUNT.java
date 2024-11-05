package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprArray;
import org.boris.expr.ExprBoolean;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprEvaluatable;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class COUNT extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertMinArgCount(args, 1);
        int count = 0;
        for (Expr a : args) {
            count += count(context, a, false);
        }
        return new ExprInteger(count);
    }

    public static int count(IEvaluationContext context, Expr arg, boolean any)
            throws ExprException {
        if (arg instanceof ExprEvaluatable) {
            arg = ((ExprEvaluatable) arg).evaluate(context);
        }

        if (arg instanceof ExprDouble || arg instanceof ExprInteger) {
            return 1;
        } else if (arg instanceof ExprArray) {
            int count = 0;
            ExprArray arr = (ExprArray) arg;
            int rows = arr.rows();
            int cols = arr.columns();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    count += count(context, arr.get(i, j), any);
                }
            }
            return count;
        } else {
            if (any) {
                if (arg instanceof ExprString || arg instanceof ExprBoolean ||
                        arg instanceof ExprError)
                    return 1;
            }
            return 0;
        }
    }
}
