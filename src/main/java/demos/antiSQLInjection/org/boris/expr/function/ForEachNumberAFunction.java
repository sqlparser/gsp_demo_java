/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Peter Smith
 *******************************************************************************/
package org.boris.expr.function;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprNumber;
import org.boris.expr.ExprString;
import org.boris.expr.util.Counter;

public abstract class ForEachNumberAFunction extends ForEachFunction
{
    protected void initialize(Counter counter) throws ExprException {
    }

    protected void iteration(Counter counter) {
    }

    protected final void value(Counter counter, Expr value)
            throws ExprException {
        if (value instanceof ExprNumber) {
            value(counter, ((ExprNumber) value).doubleValue());
        } else if (value instanceof ExprString) {
            value(counter, 0);
        }
    }

    protected abstract void value(Counter counter, double value);
}
