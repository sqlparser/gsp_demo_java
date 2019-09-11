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

import java.util.HashMap;
import java.util.Map;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprFunction;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.IExprFunction;

public class FunctionMap implements IFunctionProvider
{
    private boolean caseSensitive;
    private Map<String, IExprFunction> functions = new HashMap();

    public FunctionMap() {
    }

    public FunctionMap(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public void add(String name, IExprFunction function) {
        functions.put(caseSensitive ? name : name.toUpperCase(), function);
    }

    public Expr evaluate(IEvaluationContext context, ExprFunction function)
            throws ExprException {
        IExprFunction f = functions.get(function.getName());
        if (f != null)
            return f.evaluate(context, function.getArgs());
        return null;
    }

    public boolean hasFunction(ExprFunction function) {
        return functions.containsKey(function.getName());
    }
}
