package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprArray;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.ExprNumber;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class FORECAST extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 3);

        Expr eF = evalArg(context, args[0]);
        if (!(eF instanceof ExprNumber))
            return ExprError.VALUE;
        Expr eY = evalArg(context, args[1]);
        if (!(eY instanceof ExprArray))
            return ExprError.VALUE;
        Expr eX = evalArg(context, args[2]);
        if (!(eX instanceof ExprArray))
            return ExprError.VALUE;

        double forecastX = ((ExprNumber) eF).doubleValue();
        ExprArray knownY = (ExprArray) eY;
        ExprArray knownX = (ExprArray) eX;
        if (knownY.length() != knownX.length())
            return ExprError.NA;

        Expr aeY = AVERAGE.average(context, knownY);
        if (aeY instanceof ExprError)
            return aeY;
        Expr aeX = AVERAGE.average(context, knownX);
        if (aeX instanceof ExprError)
            return aeX;

        double averageY = ((ExprNumber) aeY).doubleValue();
        double averageX = ((ExprNumber) aeX).doubleValue();

        double bnum = 0;
        double bdem = 0;
        int len = knownY.length();
        for (int i = 0; i < len; i++) {
            bnum += (asDouble(context, knownX, i) - averageX) *
                    (asDouble(context, knownY, i) - averageY);
            bdem += Math.pow((asDouble(context, knownX, i) - averageX), 2);
        }

        if (bdem == 0)
            return ExprError.DIV0;

        double b = bnum / bdem;
        double a = averageY - b * averageX;
        double res = a + b * forecastX;

        return new ExprDouble(res);
    }
}
