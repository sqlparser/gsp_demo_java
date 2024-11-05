package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprArray;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.ExprNumber;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class COVAR extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 2);
        ExprArray array1 = asArray(context, args[0], false);
        ExprArray array2 = asArray(context, args[1], false);

        if (array1.length() != array2.length())
            return ExprError.NA;
        if (array1.length() == 0 || array2.length() == 0)
            return ExprError.DIV0;

        Expr ea1 = AVERAGE.average(context, array1);
        if (ea1 instanceof ExprError)
            return ea1;
        double average1 = ((ExprNumber) ea1).doubleValue();

        Expr ea2 = AVERAGE.average(context, array2);
        if (ea2 instanceof ExprError)
            return ea2;
        double average2 = ((ExprNumber) ea2).doubleValue();

        int count = 0;
        double p = 0;

        int len = array1.length();
        for (int i = 0; i < len; i++) {
            Expr x = array1.get(i);
            Expr y = array2.get(i);
            if (isNumber(x) && isNumber(y)) {
                p += (asDouble(context, x, true) - average1) *
                        (asDouble(context, y, true) - average2);
                count++;
            }
        }

        if (count == 0)
            return ExprError.DIV0;

        return new ExprDouble(p / count);
    }
}
