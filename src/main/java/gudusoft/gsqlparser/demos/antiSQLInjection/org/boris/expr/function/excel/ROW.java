package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.ExprVariable;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.engine.Range;
import org.boris.expr.function.AbstractFunction;

public class ROW extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args) throws ExprException {
        assertArgCount(args, 1);

        if (args[0] instanceof ExprVariable) {
            ExprVariable v = (ExprVariable) args[0];
            Range r = (Range) v.getAnnotation();
            if (r == null) {
                r = Range.valueOf(v.getName());
            }
            if (r != null && r.getDimension1() != null) {
                return new ExprInteger(r.getDimension1().getRow());
            }

            return ExprError.NAME;
        }

        throw new ExprException("Invalid argument for function ROW");
    }
}
