package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class LEFT extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertMinArgCount(args, 1);
        assertMaxArgCount(args, 2);
        String str = asString(context, args[0], false);
        int r = 1;
        if (args.length == 2) {
            r = asInteger(context, args[1], true);
        }
        if (r > str.length())
            r = str.length();
        return new ExprString(str.substring(0, r));
    }
}
