package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprArray;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class REPT extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 2);
        Expr str = evalArg(context, args[0]);
        if (str instanceof ExprArray) {
            str = ((ExprArray) str).get(0);
        }
        int rep = asInteger(context, args[1], true);
        if (rep < 0)
            return ExprError.VALUE;
        switch (rep) {
        case 0:
            return ExprString.EMPTY;
        case 1:
            return evalArg(context, args[0]);
        default:
            String s = str.toString();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rep; i++) {
                sb.append(s);
            }
            return new ExprString(sb.toString());
        }
    }
}
