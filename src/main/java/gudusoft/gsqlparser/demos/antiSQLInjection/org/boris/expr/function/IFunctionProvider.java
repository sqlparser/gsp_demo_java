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
import org.boris.expr.ExprFunction;
import org.boris.expr.IEvaluationContext;

public interface IFunctionProvider
{
    boolean hasFunction(ExprFunction function);

    Expr evaluate(IEvaluationContext context, ExprFunction function)
            throws ExprException;
}
