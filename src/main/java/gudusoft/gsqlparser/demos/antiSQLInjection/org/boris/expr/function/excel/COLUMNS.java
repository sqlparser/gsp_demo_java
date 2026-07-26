package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprError;
import org.boris.expr.ExprEvaluatable;
import org.boris.expr.ExprException;
import org.boris.expr.ExprInteger;
import org.boris.expr.ExprVariable;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.engine.GridReference;
import org.boris.expr.engine.Range;
import org.boris.expr.function.AbstractFunction;

public class COLUMNS extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertArgCount(args, 1);
        return columnsOrRows(context, args, true);
    }

    public static Expr columnsOrRows(IEvaluationContext context, Expr[] args,
            boolean cols) throws ExprException {

        if (args[0] instanceof ExprVariable) {
            ExprVariable v = (ExprVariable) args[0];
            Range r = (Range) v.getAnnotation();
            if (r == null) {
                r = Range.valueOf(v.getName());
            }
            if (r != null && r.getDimension1() != null) {
                GridReference dim1 = r.getDimension1();
                GridReference dim2 = r.getDimension2();
                if (dim2 == null) {
                    return new ExprInteger(1);
                } else {
                    if (cols) {
                        return new ExprInteger(Math.abs(dim2.getColumn() -
                                dim1.getColumn()) + 1);
                    } else {
                        return new ExprInteger(Math.abs(dim2.getRow() -
                                dim1.getRow()) + 1);
                    }
                }
            }

            return ExprError.NAME;
        }

        Expr a = args[0];
        if (a instanceof ExprEvaluatable) {
            a = ((ExprEvaluatable) a).evaluate(context);
        }

        if (a instanceof ExprInteger || a instanceof ExprDouble) {
            return new ExprInteger(1);
        }

        if (a instanceof ExprError) {
            return a;
        }

        return ExprError.VALUE;
    }
}
