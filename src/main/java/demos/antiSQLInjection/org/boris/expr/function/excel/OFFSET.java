package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprError;
import org.boris.expr.ExprException;
import org.boris.expr.ExprMissing;
import org.boris.expr.ExprVariable;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.engine.GridReference;
import org.boris.expr.engine.Range;
import org.boris.expr.function.AbstractFunction;

public class OFFSET extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertMinArgCount(args, 3);
        assertMaxArgCount(args, 5);

        Expr r = args[0];
        if (!(r instanceof ExprVariable)) {
            throw new ExprException("First argument to OFFSET not a reference");
        }

        ExprVariable ref = (ExprVariable) r;
        Range range = (Range) ref.getAnnotation();
        if (range == null) {
            range = Range.valueOf(ref.getName());
        }
        if (range == null) {
            throw new ExprException("First argument to OFFSET not a reference");
        }
        GridReference gf = range.getDimension1();
        int x = gf.getColumn();
        int y = gf.getRow();
        int rows = asInteger(context, args[1], true);
        int cols = asInteger(context, args[2], true);
        int height = 1;
        int width = 1;
        if (args.length > 3) {
            Expr h = args[3];
            if (!(h instanceof ExprMissing)) {
                height = asInteger(context, h, true);
            }
        }
        if (height < 1) {
            return ExprError.VALUE;
        }
        if (args.length > 4) {
            Expr w = args[4];
            if (!(w instanceof ExprMissing)) {
                width = asInteger(context, w, true);
            }
        }
        if (width < 1) {
            return ExprError.VALUE;
        }

        GridReference dim1 = new GridReference(x + cols, y + rows);
        if (dim1.getColumn() < 1 || dim1.getRow() < 1) {
            return ExprError.REF;
        }
        GridReference dim2 = null;
        if (height > 1 || width > 1) {
            dim2 = new GridReference(x + cols + width - 1, y + rows + height -
                    1);
        }
        Range result = new Range(range.getNamespace(), dim1, dim2);
        ExprVariable var = new ExprVariable(result.toString());
        var.setAnnotation(result);

        return var;
    }
}
