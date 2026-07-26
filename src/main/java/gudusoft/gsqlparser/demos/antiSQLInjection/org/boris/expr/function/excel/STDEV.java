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

public class STDEV extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertMinArgCount(args, 1);
        return stdev(context, args);
    }

    public static Expr stdev(IEvaluationContext context, Expr[] args)
            throws ExprException {
        return stdev(context, args, false);
    }

    protected static Expr variance(IEvaluationContext context, Expr[] args,
            boolean allPopulation) throws ExprException {

        double[] values = { 0, 0 };

        for (Expr a : args)
            AVERAGE.eval(context, a, values, true);

        if (values[1] == 0) {
            return ExprError.NUM;
        }

        double average = values[0] / values[1];

        values[0] = values[1] = 0;

        for (Expr a : args)
            eval(context, a, average, values, true);

        return new ExprDouble(values[0] / (values[1] - (allPopulation ? 0 : 1)));
    }

    public static Expr stdev(IEvaluationContext context, Expr[] args,
            boolean allPopulation) throws ExprException {
        Expr res = variance(context, args, allPopulation);
        if (res instanceof ExprDouble) {
            res = new ExprDouble(Math.sqrt(((ExprDouble) res).doubleValue()));
        }
        return res;
    }

    protected static void eval(IEvaluationContext context, Expr a,
            double average, double[] values, boolean strict)
            throws ExprException {
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
            values[0] += Math.pow(average - d, 2);
            values[1] += 1;
            return;
        }

        if (a instanceof ExprArray) {
            ExprArray arr = (ExprArray) a;
            int rows = arr.rows();
            int cols = arr.columns();
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    eval(context, arr.get(i, j), average, values, false);
                }
            }

            return;
        }

        throw new ExprException("Unexpected argument for STDEV: " + a);
    }
}
