package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.ExprNumber;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;
import org.boris.expr.util.ExcelDate;

public class DATE extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 3);
        Expr eY = evalArg(context, args[0]);
        if (!isNumber(eY))
            return ExprError.VALUE;
        double y = ((ExprNumber) eY).doubleValue();
        Expr eM = evalArg(context, args[1]);
        if (!isNumber(eM))
            return ExprError.VALUE;
        double m = ((ExprNumber) eM).doubleValue();
        Expr eD = evalArg(context, args[1]);
        if (!isNumber(eD))
            return ExprError.VALUE;
        double d = ((ExprNumber) eD).doubleValue();
        double r = ExcelDate.date(y, m, d);
        if (r < 0)
            return ExprError.NUM;
        return new ExprDouble(r);
    }

}
