package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;
import org.boris.expr.util.Maths;

public class LOG extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertMinArgCount(args, 1);
        assertMaxArgCount(args, 2);
        double num = asDouble(context, args[0], true);
        if (num < 0)
            return ExprError.NUM;

        int base = 10;
        if (args.length == 2)
            base = asInteger(context, args[1], true);
        if (base < 1)
            return ExprError.NUM;
        if (base == 1)
            return ExprError.DIV0;

        return new ExprDouble(Maths.log( base, num));
    }
}
