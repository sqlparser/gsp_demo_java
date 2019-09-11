package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class PROPER extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 1);
        String str = asString(context, args[0], false);
        StringBuilder sb = new StringBuilder();
        int len = str.length();
        boolean proper = true;
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (Character.isLetter(c)) {
                sb.append(proper ? Character.toUpperCase(c) : Character
                        .toLowerCase(c));
                proper = false;
            } else {
                proper = true;
                sb.append(c);
            }
        }
        return new ExprString(sb.toString());
    }
}
