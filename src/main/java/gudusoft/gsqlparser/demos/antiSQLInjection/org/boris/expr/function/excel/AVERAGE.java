package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprArray;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprEvaluatable;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.ExprMissing;
import org.boris.expr.ExprNumber;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class AVERAGE extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertMinArgCount(args, 1);

        return average(context, args);
    }

    public static Expr average(IEvaluationContext context, Expr... args)
            throws ExprException {
        double[] values = { 0, 0 };
        for (Expr a : args)
            eval(context, a, values, true);

        if (values[1] == 0) {
            return ExprError.DIV0;
        }

        return new ExprDouble(values[0] / values[1]);
    }

    public static void eval(IEvaluationContext context, Expr a,
            double[] values, boolean strict) throws ExprException {
        if (a instanceof ExprEvaluatable)
            a = ((ExprEvaluatable) a).evaluate(context);

        if (a == null)
            return;

        if (a instanceof ExprMissing)
            return;

        if (a instanceof ExprString) {
            if (strict)
                throw new ExprException("Unexpected argument for AVERAGE: " + a);
            else
                return;
        }

        if (a instanceof ExprDouble || a instanceof ExprInteger) {
            double d = ((ExprNumber) a).doubleValue();
            values[0] += d;
            values[1] += 1;
            return;
        }

        if (a instanceof ExprArray) {
            ExprArray arr = (ExprArray) a;
            int rows = arr.rows();
            int cols = arr.columns();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    eval(context, arr.get(i, j), values, false);
                }
            }

            return;
        }

        throw new ExprException("Unexpected argument for AVERAGE: " + a);
    }
}
