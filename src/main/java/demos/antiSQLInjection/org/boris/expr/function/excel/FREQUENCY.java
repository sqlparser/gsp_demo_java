package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprArray;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.ExprNumber;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class FREQUENCY extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 2);
        double[] samples = asArray(context, evalArg(context, args[0]));
        double[] bins = asArray(context, evalArg(context, args[0]));
        return null;
    }

    double[] asArray(IEvaluationContext context, Expr vals)
            throws ExprException {
        if (vals instanceof ExprInteger || vals instanceof ExprDouble) {
            return new double[] { ((ExprNumber) vals).doubleValue() };
        } else if (vals instanceof ExprArray) {
            ExprArray a = (ExprArray) vals;
            double[] arr = new double[a.length()];
            int index = 0;
            for (int i = 0; i < arr.length; i++) {
                Expr e = evalArg(context, a.get(i));
                if (e instanceof ExprNumber) {
                    arr[index++] = ((ExprNumber) e).doubleValue();
                }
            }
            if (arr.length == index)
                return arr;
            else {
                double[] a2 = new double[index];
                System.arraycopy(arr, 0, a2, 0, index);
            }
        }
        return null;
    }
}
