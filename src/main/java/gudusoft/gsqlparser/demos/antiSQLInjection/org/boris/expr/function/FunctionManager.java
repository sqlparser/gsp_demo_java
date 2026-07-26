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

import java.util.LinkedHashSet;
import java.util.Set;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprFunction;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.IExprFunction;

public class FunctionManager implements IFunctionProvider
{
    private FunctionMap functionMap;
    private Set<IFunctionProvider> providers = new LinkedHashSet();

    public FunctionManager() {
        this(false);
    }

    public FunctionManager(boolean caseSensitive) {
        functionMap = new FunctionMap(caseSensitive);
        providers.add(functionMap);
    }

    public void add(String name, IExprFunction function) {
        functionMap.add(name, function);
    }

    public void add(IFunctionProvider provider) {
        providers.add(provider);
    }

    public Expr evaluate(IEvaluationContext context, ExprFunction function)
            throws ExprException {
        for (IFunctionProvider p : providers) {
            if (p.hasFunction(function))
                return p.evaluate(context, function);
        }
        return null;
    }

    public boolean hasFunction(ExprFunction function) {
        for (IFunctionProvider p : providers) {
            if (p.hasFunction(function))
                return true;
        }

        return false;
    }
}
