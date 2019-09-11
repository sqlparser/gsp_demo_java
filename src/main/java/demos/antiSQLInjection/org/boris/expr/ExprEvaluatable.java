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

public abstract class ExprEvaluatable extends Expr
{
    ExprEvaluatable(ExprType type) {
        super(type, true);
    }

    public boolean isVolatile() {
        return true;
    }

    public abstract Expr evaluate(IEvaluationContext context)
            throws ExprException;
}
