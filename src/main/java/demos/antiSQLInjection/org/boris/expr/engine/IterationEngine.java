/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Peter Smith
 *******************************************************************************/
package org.boris.expr.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.boris.expr.Expr;
import org.boris.expr.ExprArray;
import org.boris.expr.ExprEvaluatable;
import org.boris.expr.ExprException;
import org.boris.expr.ExprNumber;

public class IterationEngine extends AbstractCalculationEngine
{
    private int maxIterations = 100;
    private double maxChange = 0.0001;
    private Map<Range, Expr> inputExprs = new HashMap();

    public IterationEngine(EngineProvider provider) {
        super(provider);
    }

    public void setMaxIterations(int iterations) {
        this.maxIterations = iterations;
    }

    public void setMaxChange(double change) {
        this.maxChange = Math.abs(change);
    }

    public void calculate(boolean force) throws ExprException {
        if (autoCalculate && !force)
            return;

        calc();
    }

    private void calc() throws ExprException {
        Set<Range> inputs = getInputRanges();
        Map<Range, Expr> valueChanges = new HashMap();
        for (int i = 0; i < maxIterations; i++) {
            double change = 0;
            for (Range r : inputs) {
                Expr e = inputExprs.get(r);
                if (e instanceof ExprEvaluatable) {
                    e = ((ExprEvaluatable) e).evaluate(this);
                    values.put(r, e);
                    valueChanges.put(r, e);
                    double c = findChange(values.get(r), e);
                    if (c > change)
                        change = c;
                }
            }

            if (change < maxChange)
                break;
        }

        for (Range r : valueChanges.keySet()) {
            provider.valueChanged(r, valueChanges.get(r));
        }
    }

    private double findChange(Expr old, Expr nu) {
        if (old == null || nu == null)
            return 0;

        if (old.type != nu.type)
            return 0;

        if (nu instanceof ExprNumber) {
            return Math.abs(((ExprNumber) old).doubleValue() -
                    ((ExprNumber) nu).doubleValue());
        }

        if (nu instanceof ExprArray) {
            Expr[] oldA = ((ExprArray) old).getInternalArray();
            Expr[] nuA = ((ExprArray) nu).getInternalArray();

            double change = 0;
            for (int i = 0; i < oldA.length && i < nuA.length; i++) {
                double c = findChange(oldA[i], nuA[i]);
                if (c > change)
                    change = c;
            }

            return change;
        }

        return 0;
    }

    public void set(Range range, String expression) throws ExprException {
        validateRange(range);

        // If null then remove all references
        if (expression == null) {
            rawInputs.remove(range);
            inputExprs.remove(range);
            values.remove(range);
            inputs.remove(range);
            return;
        }

        rawInputs.put(range, expression);

        Expr expr = parseExpression(expression);
        inputExprs.put(range, expr);

        // Set the inputs
        provider.inputChanged(range, expr);
        inputs.put(range, expr);

        // Always evaluate the expression entered
        if (expr.evaluatable) {
            Expr eval = ((ExprEvaluatable) expr).evaluate(this);
            provider.valueChanged(range, eval);
            values.put(range, eval);
        } else {
            provider.valueChanged(range, expr);
            values.put(range, expr);
        }

        // Recalculate the dependencies if required
        if (autoCalculate) {
            calc();
        }
    }
}
