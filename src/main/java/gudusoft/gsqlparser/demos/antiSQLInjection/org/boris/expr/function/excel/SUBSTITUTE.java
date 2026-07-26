package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class SUBSTITUTE extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertMinArgCount(args, 3);
        assertMaxArgCount(args, 4);
        String str = asString(context, args[0], false);
        String old = asString(context, args[1], false);
        String nu = asString(context, args[2], false);
        int inum = 1;
        if (args.length == 4)
            inum = asInteger(context, args[3], true);

        int idx = str.indexOf(old);
        for (int i = 1; i < inum; i++) {
            if (idx == -1)
                break;
            idx = str.indexOf(old, idx + 1);
        }

        if (idx != -1) {
            StringBuilder sb = new StringBuilder();
            if (idx > 0)
                sb.append(str.substring(0, idx));
            sb.append(nu);
            sb.append(str.substring(idx + old.length()));
            return new ExprString(sb.toString());
        } else {
            return new ExprString(str);
        }
    }
}
