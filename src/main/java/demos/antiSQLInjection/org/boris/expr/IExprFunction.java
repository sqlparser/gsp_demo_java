/*******************************************************************************
 * This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Peter Smith
 *******************************************************************************/
package org.boris.expr;

public interface IExprFunction
{
    Expr evaluate(IEvaluationContext context, Expr[] args) throws ExprException;

    boolean isVolatile();
}
