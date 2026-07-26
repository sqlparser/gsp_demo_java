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

public class ExprLessThanOrEqualTo extends AbstractComparisonOperator
{
    public ExprLessThanOrEqualTo(Expr lhs, Expr rhs) {
        super(ExprType.LessThanOrEqualTo, lhs, rhs);
    }

    public Expr evaluate(IEvaluationContext context) throws ExprException {
        return bool(compare(context) <= 0);
    }

    public String toString() {
        return lhs + "<=" + rhs;
    }
}