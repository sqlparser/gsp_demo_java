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

import java.util.HashMap;
import java.util.Map;

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprFunction;
import org.boris.expr.ExprVariable;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.function.AbstractFunction;
import org.boris.expr.function.ExcelFunctionProvider;
import org.boris.expr.function.FunctionManager;

public class SimpleEvaluationContext implements IEvaluationContext
{
    private FunctionManager functions = new FunctionManager();
    private Map<String, Expr> variables = new HashMap();

    public SimpleEvaluationContext() {
        functions.add(new ExcelFunctionProvider());
        functions.add("SetVar", new SetVarFunction());
    }

    public void setVariable(String name, Expr value) {
        variables.put(name, value);
    }

    public Expr evaluateFunction(ExprFunction function) throws ExprException {
        return functions.evaluate(this, function);
    }

    public Expr evaluateVariable(ExprVariable variable) throws ExprException {
        return variables.get(variable.getName());
    }

    private class SetVarFunction extends AbstractFunction
    {
        public Expr evaluate(IEvaluationContext context, Expr[] args)
                throws ExprException {
            assertArgCount(args, 2);
            String v = asString(context, args[0], false).toUpperCase();
            Expr e = evalArg(context, args[1]);
            variables.put(v, e);

            return e;
        }
    }
}
