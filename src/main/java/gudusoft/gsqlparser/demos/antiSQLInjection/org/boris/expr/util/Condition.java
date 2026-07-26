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

import java.util.ArrayList;
import java.util.List;

import org.boris.expr.AbstractBinaryOperator;
import org.boris.expr.Expr;
import org.boris.expr.ExprBoolean;
import org.boris.expr.ExprDouble;
import org.boris.expr.ExprEqual;
import org.boris.expr.ExprException;
import org.boris.expr.ExprGreaterThan;
import org.boris.expr.ExprGreaterThanOrEqualTo;
import org.boris.expr.ExprInteger;
import org.boris.expr.ExprLessThan;
import org.boris.expr.ExprLessThanOrEqualTo;
import org.boris.expr.ExprMissing;
import org.boris.expr.ExprNotEqual;
import org.boris.expr.ExprString;
import org.boris.expr.function.excel.AND;

public class Condition
{
    private List<AbstractBinaryOperator> operators = new ArrayList();
    private AND func = new AND();

    public static Condition valueOf(Expr arg) {
        if (arg instanceof ExprString) {
            String s = ((ExprString) arg).str;
            if ("".equals(s))
                return null;
            Condition c = new Condition();
            AbstractBinaryOperator operator;
            int offset = 0;
            boolean str = false;
            if (s.startsWith(">=")) {
                operator = new ExprGreaterThanOrEqualTo(null, null);
                offset = 2;
            } else if (s.startsWith("<=")) {
                operator = new ExprLessThanOrEqualTo(null, null);
                offset = 2;
            } else if (s.startsWith("<>")) {
                operator = new ExprNotEqual(null, null);
                offset = 2;
            } else if (s.startsWith("=")) {
                operator = new ExprEqual(null, null);
                offset = 1;
            } else if (s.startsWith("<")) {
                operator = new ExprLessThan(null, null);
                offset = 1;
            } else if (s.startsWith(">")) {
                operator = new ExprGreaterThan(null, null);
                offset = 1;
            } else {
                operator = new ExprEqual(null, null);
                str = true;
                offset = 0;
            }

            operator.setRHS(c.getRHS(s, offset, str));
            c.operators.add(operator);
            return c;
        } else if (arg instanceof ExprDouble || arg instanceof ExprInteger) {
            Condition c = new Condition();
            AbstractBinaryOperator operator = new ExprEqual(null, arg);
            c.operators.add(operator);
            return c;
        }
        return null;
    }

    private Expr getRHS(String text, int offset, boolean str) {
        if (str) {
            return new ExprString(text.substring(offset));
        } else {
            try {
                return new ExprDouble(Double
                        .parseDouble(text.substring(offset)));
            } catch (NumberFormatException e) {
                return ExprMissing.MISSING;
            }
        }
    }

    public boolean eval(Expr arg) throws ExprException {
        if (operators.isEmpty()) {
            return true;
        } else {
            for (AbstractBinaryOperator operator : operators)
                operator.setLHS(arg);

            ExprBoolean v = (ExprBoolean) func.evaluate(null, operators
                    .toArray(new Expr[0]));
            return v.value;
        }
    }

    public void add(Condition c) {
        operators.addAll(c.operators);
    }
}