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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.boris.expr.Expr;
import org.boris.expr.ExprEvaluatable;
import org.boris.expr.ExprException;
import org.boris.expr.ExprFunction;
import org.boris.expr.ExprVariable;
import org.boris.expr.IEvaluationContext;
import org.boris.expr.parser.ExprParser;

public class ExprAlias
{
    private Expr expr;
    private ExprEvaluatable evaluatable;
    private IEvaluationContext delegate;
    private Map<String, Expr> variables = new HashMap();
    private String text;

    public void setText(String text) throws IOException, ExprException {
        this.text = text;
        this.expr = ExprParser.parse(text);
        if (expr instanceof ExprEvaluatable) {
            evaluatable = (ExprEvaluatable) expr;
        } else {
            evaluatable = null;
        }
    }

    public String getText() {
        return text;
    }

    public synchronized Expr evaluate(Expr[] args, IEvaluationContext context)
            throws ExprException {
        if (evaluatable != null) {
            variables.clear();
            for (int i = 0; i < args.length; i++) {
                variables.put("$" + (i + 1), args[i]);
            }
            return evaluatable.evaluate(context);
        } else {
            return expr;
        }
    }

    public Expr evaluateFunction(ExprFunction function) throws ExprException {
        return delegate.evaluateFunction(function);
    }

    public Expr evaluateVariable(ExprVariable variable) throws ExprException {
        Expr var = variables.get(variable.getName());
        if (var != null) {
            return var;
        } else {
            return delegate.evaluateVariable(variable);
        }
    }
}
