package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprEvaluatable;
import org.boris.expr.ExprException;
import org.boris.expr.ExprNumber;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class CHOOSE extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        if (args.length < 2)
            throw new ExprException("Too few arguments for CHOOSE");

        Expr index = args[0];
        if (index instanceof ExprEvaluatable)
            index = ((ExprEvaluatable) index).evaluate(context);

        if (!(index instanceof ExprNumber)) {
            throw new ExprException(
                    "First argument must be a number for CHOOSE");
        }

        int idx = ((ExprNumber) index).intValue();
        if (idx < 1 || idx >= args.length)
            throw new ExprException("Invalid index for CHOOSE");

        return args[idx];
    }
}
