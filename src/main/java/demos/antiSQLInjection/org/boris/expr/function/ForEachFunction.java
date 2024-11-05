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
import org.boris.expr.ExprArray;
import org.boris.expr.ExprEvaluatable;
import org.boris.expr.ExprException;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.util.Counter;

public abstract class ForEachFunction extends AbstractFunction
{
    protected int iterations = 1;

    protected void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public final Expr evaluate(IEvaluationContext context, Expr[] args)
            throws ExprException {
        assertMinArgCount(args, 1);
        Counter c = new Counter();
        initialize(c);
        ol: for (int i = 0; i < iterations; i++) {
            c.iteration = i + 1;
            iteration(c);
            for (Expr e : args) {
                evalItem(context, e, c);
                if (!c.doit)
                    break ol;
            }
        }
        return evaluate(c);
    }

    private void evalItem(IEvaluationContext context, Expr e, Counter c)
            throws ExprException {
        if (e instanceof ExprEvaluatable) {
            e = ((ExprEvaluatable) e).evaluate(context);
        }

        if (e instanceof ExprArray) {
            ExprArray a = (ExprArray) e;
            Expr[] ai = a.getInternalArray();
            for (Expr aie : ai) {
                evalItem(context, aie, c);
                if (!c.doit)
                    break;
            }
        } else {
            value(c, e);
        }
    }

    protected abstract void initialize(Counter counter) throws ExprException;

    protected abstract void iteration(Counter counter);

    protected abstract void value(Counter counter, Expr value)
            throws ExprException;

    protected abstract Expr evaluate(Counter counter) throws ExprException;
}
