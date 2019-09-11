package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class LOOKUP extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertMinArgCount(args, 2);
        assertMaxArgCount(args, 3);
        if (args.length == 2) {
            return arrayLookup(context, args);
        } else {
            return vectorLookup(context, args);
        }
    }

    public static Expr vectorLookup(IEvaluationContext context, Expr[] args)
            throws ExprException {
        Expr ev = evalArg(context, args[0]);
        Expr el = evalArg(context, args[1]);
        Expr er = evalArg(context, args[2]);
        return null;
    }

    public static Expr arrayLookup(IEvaluationContext context, Expr[] args) {
        return null;
    }
}
