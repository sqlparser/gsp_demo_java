package org.boris.expr.function.excel;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprString;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.engine.GridReference;
import org.boris.expr.function.AbstractFunction;

public class ADDRESS extends AbstractFunction
{
    public Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        if (args.length < 2 || args.length > 5)
            throw new ExprException("Invalid arguments to function ADDRESS");

        int row = asInteger(context, args[0], true);
        int col = asInteger(context, args[1], true);
        int abs = 1;
        if (args.length >= 3) {
            abs = asInteger(context, args[2], true);
        }
        boolean a1 = true;
        if (args.length >= 4) {
            a1 = asBoolean(context, args[3], true);
        }
        String sheetText = null;
        boolean needsQuotes = false;
        if (args.length >= 5) {
            sheetText = asString(context, args[4], true);
            needsQuotes = needsQuotes(sheetText);
        }

        StringBuilder sb = new StringBuilder();
        if (sheetText != null) {
            if (needsQuotes)
                sb.append('\'');
            sb.append(sheetText);
            if (needsQuotes)
                sb.append('\'');
            sb.append('!');
        }
        if (a1) {
            if (abs == 1 || abs == 3)
                sb.append('$');
            sb.append(GridReference.toColumnName(col));
            if (abs == 1 || abs == 2)
                sb.append('$');
            sb.append(row);
        } else {
            sb.append('R');
            if (abs == 3 || abs == 4) {
                sb.append('[');
                sb.append(row);
                sb.append(']');
            } else {
                sb.append(row);
            }
            sb.append('C');
            if (abs == 2 || abs == 4) {
                sb.append('[');
                sb.append(col);
                sb.append(']');
            } else {
                sb.append(col);
            }
        }

        return new ExprString(sb.toString());
    }

    private boolean needsQuotes(String sheetText) {
        int len = sheetText.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isLetterOrDigit(sheetText.charAt(i)))
                return true;
        }
        return false;
    }
}
