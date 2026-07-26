package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.ExprNumber;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class CODE extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 1);

        Expr a = evalArg(context, args[0]);
        String s = null;

        if (a instanceof ExprString) {
            s = ((ExprString) a).str;
        } else if (a instanceof ExprNumber) {
            s = a.toString();
        }

        if (s != null && s.length() > 0)
            return new ExprInteger(s.charAt(0));

        return ExprError.VALUE;
    }
}
