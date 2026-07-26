package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprArray;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprEvaluatable;
import org.boris.expr.ExprException;
import org.boris.expr.ExprNumber;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class MAX extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertMinArgCount(args, 1);
        return max(context, args);
    }

    public static Expr max(IEvaluationContext context, Expr[] args)
            throws ExprException {
        double d = -Double.MAX_VALUE;
        for (Expr a : args) {
            Expr res = max(context, a);
            if (res instanceof ExprError) {
                return res;
            } else {
                double r = ((ExprDouble) res).doubleValue();
                if (r > d)
                    d = r;
            }
        }
        return new ExprDouble(d);
    }

    public static Expr max(IEvaluationContext context, Expr arg)
            throws ExprException {
        if (arg instanceof ExprEvaluatable) {
            arg = ((ExprEvaluatable) arg).evaluate(context);
        }

        if (arg instanceof ExprNumber) {
            return new ExprDouble(((ExprNumber) arg).doubleValue());
        }

        if (arg instanceof ExprArray) {
            return max(context, ((ExprArray) arg).getInternalArray());
        }

        if (arg instanceof ExprError) {
            return arg;
        }

        return ExprError.VALUE;
    }
}
