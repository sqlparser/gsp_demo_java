package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;
import org.boris.expr.util.Financials;

public class DDB extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertMinArgCount(args, 4);
        assertMaxArgCount(args, 5);

        double cost = asDouble(context, args[0], true);
        if (cost < 0)
            return ExprError.NUM;
        double salvage = asDouble(context, args[1], true);
        if (salvage < 0)
            return ExprError.NUM;
        int life = asInteger(context, args[2], true);
        if (life < 0)
            return ExprError.NUM;
        int period = asInteger(context, args[3], true);
        if (period < 0)
            return ExprError.NUM;
        double factor = 2;
        if (args.length == 5)
            factor = asDouble(context, args[4], true);

        if (cost == 0)
            return ExprDouble.ZERO;

        return new ExprDouble(Financials.ddb(cost, salvage, life, period,
                factor));
    }
}
