package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class ERRORTYPE extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args) throws ExprException {
        assertArgCount(args, 1);

        if (args[0] instanceof ExprError) {
            if (ExprError.NULL.equals(args[0])) {
                return new ExprInteger(1);
            } else if (ExprError.DIV0.equals(args[0])) {
                return new ExprInteger(2);
            } else if (ExprError.VALUE.equals(args[0])) {
                return new ExprInteger(3);
            } else if (ExprError.REF.equals(args[0])) {
                return new ExprInteger(4);
            } else if (ExprError.NAME.equals(args[0])) {
                return new ExprInteger(5);
            } else if (ExprError.NUM.equals(args[0])) {
                return new ExprInteger(6);
            } else if (ExprError.NA.equals(args[0])) {
                return new ExprInteger(7);
            }
        }

        return ExprError.NA;
    }
}
