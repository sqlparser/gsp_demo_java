package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprArray;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class SUMX2MY2 extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 2);

        Expr eX = evalArg(context, args[0]);
        if (!(eX instanceof ExprArray))
            return ExprError.VALUE;
        Expr eY = evalArg(context, args[1]);
        if (!(eY instanceof ExprArray))
            return ExprError.VALUE;

        ExprArray arrayX = (ExprArray) eX;
        ExprArray arrayY = (ExprArray) eY;
        if (arrayY.length() != arrayX.length())
            return ExprError.NA;

        int len = arrayX.length();
        double sum = 0;
        for (int i = 0; i < len; i++) {
            sum += eval(asDouble(context, arrayX, i), asDouble(context, arrayY,
                    i));
        }

        return new ExprDouble(sum);
    }

    protected double eval(double x, double y) {
        return Math.pow(x, 2) - Math.pow(y, 2);
    }
}
