package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.ExprNumber;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class SUMPRODUCT extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertMinArgCount(args, 2);

        int len = 0;
        Expr[] ea = new Expr[args.length];

        for (int i = 0; i < args.length; i++) {
            ea[i] = evalArg(context, args[i]);
            if (i == 0) {
                len = getLength(ea[i]);
            } else {
                if (len != getLength(ea[i]))
                    return ExprError.VALUE;
            }
        }

        double sum = 0;

        for (int i = 0; i < len; i++) {
            double p = 1;
            for (int j = 0; j < ea.length; j++) {
                Expr a = get(ea[j], i);
                if (a instanceof ExprDouble || a instanceof ExprInteger) {
                    p *= ((ExprNumber) a).doubleValue();
                } else {
                    p = 0;
                    break;
                }
            }
            sum += p;
        }

        return new ExprDouble(sum);
    }
}
