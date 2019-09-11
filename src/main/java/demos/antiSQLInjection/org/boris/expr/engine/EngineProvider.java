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

import org.boris.expr.Expr;
import org.boris.expr.ExprException;
import org.boris.expr.ExprFunction;
import org.boris.expr.ExprVariable;
import org.boris.expr.IEvaluationContext;

public interface EngineProvider
{
    void validate(ExprVariable variable) throws ExprException;

    void inputChanged(Range range, Expr input);

    void valueChanged(Range range, Expr value);

    Expr evaluateFunction(IEvaluationContext context, ExprFunction function) throws ExprException;

    Expr evaluateVariable(IEvaluationContext context, ExprVariable variable) throws ExprException;
}
