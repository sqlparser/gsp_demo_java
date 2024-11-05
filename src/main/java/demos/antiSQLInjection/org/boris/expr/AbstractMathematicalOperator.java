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

public abstract class AbstractMathematicalOperator extends
        AbstractBinaryOperator
{
    public AbstractMathematicalOperator(ExprType type, Expr lhs, Expr rhs) {
        super(type, lhs, rhs);
    }

    protected double evaluateExpr(Expr e, IEvaluationContext context)
            throws ExprException {
        e = eval(e, context);
        if (e == null)
            return 0;
        if (e instanceof ExprMissing)
            return 0;
        ExprTypes.assertType(e, ExprType.Integer, ExprType.Double);
        return ((ExprNumber) e).doubleValue();
    }

    public Expr evaluate(IEvaluationContext context) throws ExprException {
        Expr l = eval(lhs, context);
        if (l instanceof ExprError) {
            return l;
        }
        Expr r = eval(rhs, context);
        if (r instanceof ExprError) {
            return r;
        }
        return evaluate(evaluateExpr(l, context), evaluateExpr(r, context));
    }

    protected abstract Expr evaluate(double lhs, double rhs)
            throws ExprException;
}
