package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.SimpleDatabaseFunction;

public class DSUM extends SimpleDatabaseFunction
{
    protected Expr evaluateMatches(IEvaluationContext context, Expr[] matches)
            throws ExprException {
        return SUM.sum(context, matches);
    }
}
