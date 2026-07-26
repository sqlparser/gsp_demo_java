package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class INFO extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 1);
        Expr a = evalArg(context, args[0]);
        if (!(a instanceof ExprString))
            return ExprError.VALUE;

        String s = ((ExprString) a).str;
        if ("directory".equalsIgnoreCase(s)) {
            return new ExprString(System.getProperty("user.dir"));
        } else if ("memavail".equalsIgnoreCase(s)) {
            return new ExprDouble(Runtime.getRuntime().freeMemory());
        } else if ("memused".equalsIgnoreCase(s)) {
            return new ExprDouble(Runtime.getRuntime().totalMemory() -
                    Runtime.getRuntime().freeMemory());
        } else if ("numfile".equalsIgnoreCase(s)) {
            return ExprError.REF; // TODO
        } else if ("origin".equalsIgnoreCase(s)) {
            return ExprError.REF; // TODO
        } else if ("osversion".equalsIgnoreCase(s)) {
            return ExprError.REF; // TODO
        } else if ("recalc".equalsIgnoreCase(s)) {
            return ExprError.REF; // TODO
        } else if ("release".equalsIgnoreCase(s)) {
            return ExprError.REF; // TODO
        } else if ("system".equalsIgnoreCase(s)) {
            return ExprError.REF; // TODO
        } else if ("totmem".equalsIgnoreCase(s)) {
            return new ExprDouble(Runtime.getRuntime().totalMemory());
        }

        return ExprError.VALUE;
    }
}
