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

public abstract class AbstractComparisonOperator extends AbstractBinaryOperator
{
    public AbstractComparisonOperator(ExprType type, Expr lhs, Expr rhs) {
        super(type, lhs, rhs);
    }

    protected double compare(IEvaluationContext context) throws ExprException {
        Expr l = eval(lhs, context);
        Expr r = eval(rhs, context);

        if (l instanceof ExprString || r instanceof ExprString) {
            return l.toString().compareTo(r.toString());
        }

        if (l instanceof ExprNumber && r instanceof ExprNumber) {
            return (((ExprNumber) l).doubleValue() - ((ExprNumber) r)
                    .doubleValue());
        }

        return 0;
    }
}
