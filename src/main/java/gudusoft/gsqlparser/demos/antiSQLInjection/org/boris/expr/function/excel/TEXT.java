package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.ExprNumber;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;
import org.boris.expr.util.ValueFormatter;

public class TEXT extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 2);

        Expr eV = evalArg(context, args[0]);
        if (eV instanceof ExprError)
            return eV;
        if (!isNumber(eV))
            return ExprError.VALUE;
        double value = ((ExprNumber) eV).doubleValue();

        Expr eF = evalArg(context, args[0]);
        if (eF instanceof ExprError)
            return eF;
        String s = asString(context, eF, false);

        String res = ValueFormatter.format(value, s);
        if (res != null)
            return new ExprString(res);
        else
            return ExprError.VALUE;
    }
}
