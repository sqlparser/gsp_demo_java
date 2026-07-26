/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Peter Smith
 *******************************************************************************/
package org.boris.expr.util;

import org.boris.expr.Expr;
import org.boris.expr.ExprArray;
import org.boris.expr.ExprBoolean;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprEvaluatable;
import org.boris.expr.ExprException;
import org.boris.expr.ExprFunction;
import org.boris.expr.ExprInteger;
import org.boris.expr.ExprString;
import org.boris.expr.ExprVariable;
import org.boris.expr.IEvaluationContext;

public class Exprs
{
    public static Expr[] parseValues(String[] values) {
        Expr[] e = new Expr[values.length];
        for (int i = 0; i < e.length; i++) {
            e[i] = parseValue(values[i]);
        }
        return e;
    }

    public static Expr parseValue(String expression) {
        Expr result;
        try {
            result = new ExprInteger(Integer.parseInt(expression));
        } catch (Exception e) {
            try {
                result = new ExprDouble(Double.parseDouble(expression));
            } catch (Exception ex) {
                result = new ExprString(expression);
            }
        }
        return result;
    }

    public static String getString(IEvaluationContext context, Expr expr)
            throws ExprException {
        if (expr instanceof ExprEvaluatable) {
            expr = ((ExprEvaluatable) expr).evaluate(context);
        }

        if (expr instanceof ExprString) {
            return ((ExprString) expr).str;
        } else {
            return expr.toString();
        }
    }

    public static Object convertExpr(IEvaluationContext context, Expr e)
            throws ExprException {
        if (e == null)
            return null;

        if (e instanceof ExprEvaluatable)
            e = ((ExprEvaluatable) e).evaluate(context);

        if (e instanceof ExprString)
            return ((ExprString) e).str;

        if (e instanceof ExprDouble)
            return ((ExprDouble) e).doubleValue();

        if (e instanceof ExprInteger)
            return ((ExprInteger) e).intValue();

        if (e instanceof ExprBoolean)
            return ((ExprBoolean) e).booleanValue();

        return e;
    }

    public static Expr convertObject(Object o) {
        if (o == null)
            return null;

        if (o instanceof Double)
            return new ExprDouble(((Double) o).doubleValue());

        if (o instanceof Integer)
            return new ExprInteger(((Integer) o).intValue());

        if (o instanceof Boolean)
            return new ExprBoolean(((Boolean) o).booleanValue());

        if (o instanceof String)
            return new ExprString((String) o);

        if (o instanceof Expr)
            return (Expr) o;

        return null;
    }

    public static Expr[] convertArgs(Object[] args) {
        Expr[] a = new Expr[args.length];
        for (int i = 0; i < args.length; i++) {
            a[i] = Exprs.convertObject(args[i]);
        }
        return a;
    }

    public static ExprArray toArray(Object... args) {
        Expr[] a = Exprs.convertArgs(args);
        ExprArray arr = new ExprArray(1, a.length);
        for (int i = 0; i < a.length; i++) {
            arr.set(0, i, a[i]);
        }
        return arr;
    }

    public static void toUpperCase(Expr e) {
        if (e instanceof ExprFunction) {
            ExprFunction f = (ExprFunction) e;
            f.setName(f.getName().toUpperCase());
            for (int i = 0; i < f.size(); i++) {
                toUpperCase(f.getArg(i));
            }
        } else if (e instanceof ExprVariable) {
            ExprVariable v = (ExprVariable) e;
            v.setName(v.getName().toUpperCase());
        }
    }
}
