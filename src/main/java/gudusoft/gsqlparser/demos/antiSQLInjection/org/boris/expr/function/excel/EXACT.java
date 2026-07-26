package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprBoolean;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class EXACT extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 2);

        String str1 = asString(context, args[0], false);
        String str2 = asString(context, args[1], false);

        return str1.equals(str2) ? ExprBoolean.TRUE : ExprBoolean.FALSE;
    }
}
