package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.SimpleDatabaseFunction;

public class DSTDEVP extends SimpleDatabaseFunction
{
    protected Expr evaluateMatches(IEvaluationContext context, Expr[] matches)
            throws ExprException {
        return STDEVP.stdevp(context, matches);
    }
}
