package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprArray;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;

public class TRANSPOSE extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 1);
        Expr e = evalArg(context, args[0]);
        if (e instanceof ExprArray) {
            return transpose((ExprArray) e);
        } else {
            return e;
        }
    }

    public static Expr transpose(ExprArray array) throws ExprException {
        int rows = array.columns();
        int cols = array.rows();
        ExprArray a = new ExprArray(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                a.set(i, j, array.get(j, i));
            }
        }
        return a;
    }
}
